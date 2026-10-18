package com.workwave.notifications;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

public class NotificationService {

    private final Map<String, NotificationChannel> channels;
    private final ExecutorService executor;
    private final NotificationRepository repository;
    private final RateLimiter rateLimiter;
    private final TemplateEngine templateEngine;

    public NotificationService(NotificationRepository repository,
                               TemplateEngine templateEngine,
                               int threadPoolSize) {
        this.channels = new ConcurrentHashMap<>();
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
        this.repository = repository;
        this.rateLimiter = new RateLimiter(100, 60); // 100 per minute
        this.templateEngine = templateEngine;
    }

    public void registerChannel(String name, NotificationChannel channel) {
        channels.put(name, channel);
    }

    public CompletableFuture<NotificationResult> send(NotificationRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            validate(request);

            if (!rateLimiter.tryAcquire(request.getRecipientId())) {
                return NotificationResult.rateLimited(request.getId());
            }

            String renderedBody = templateEngine.render(
                request.getTemplateName(),
                request.getTemplateVariables()
            );

            NotificationChannel channel = channels.get(request.getChannelName());
            if (channel == null) {
                throw new IllegalArgumentException(
                    "Unknown channel: " + request.getChannelName()
                );
            }

            DeliveryResult delivery = channel.deliver(
                request.getRecipientId(),
                request.getSubject(),
                renderedBody
            );

            NotificationRecord record = new NotificationRecord(
                request.getId(),
                request.getRecipientId(),
                request.getChannelName(),
                delivery.isSuccess() ? "DELIVERED" : "FAILED",
                Instant.now(),
                delivery.getProviderMessageId()
            );
            repository.save(record);

            return delivery.isSuccess()
                ? NotificationResult.success(request.getId(), delivery.getProviderMessageId())
                : NotificationResult.failed(request.getId(), delivery.getErrorMessage());
        }, executor);
    }

    public CompletableFuture<BatchResult> sendBatch(List<NotificationRequest> requests) {
        List<CompletableFuture<NotificationResult>> futures = requests.stream()
            .map(this::send)
            .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> {
                List<NotificationResult> results = futures.stream()
                    .map(CompletableFuture::join)
                    .toList();
                return new BatchResult(results);
            });
    }

    public List<NotificationRecord> getHistory(String recipientId, int limit) {
        return repository.findByRecipient(recipientId, limit);
    }

    public NotificationStats getStats(String channelName, Instant from, Instant to) {
        List<NotificationRecord> records = repository.findByChannelAndTimeRange(
            channelName, from, to
        );
        long total = records.size();
        long delivered = records.stream()
            .filter(r -> "DELIVERED".equals(r.getStatus()))
            .count();
        long failed = total - delivered;
        double successRate = total > 0 ? (double) delivered / total : 0.0;

        return new NotificationStats(channelName, total, delivered, failed, successRate);
    }

    public void retryFailed(String channelName, Instant since) {
        List<NotificationRecord> failed = repository.findFailedByChannel(channelName, since);
        for (NotificationRecord record : failed) {
            NotificationRequest retryRequest = repository.getOriginalRequest(record.getId());
            if (retryRequest != null && record.getRetryCount() < 3) {
                send(retryRequest);
                repository.incrementRetryCount(record.getId());
            }
        }
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void validate(NotificationRequest request) {
        Objects.requireNonNull(request.getRecipientId(), "recipientId is required");
        Objects.requireNonNull(request.getChannelName(), "channelName is required");
        Objects.requireNonNull(request.getTemplateName(), "templateName is required");
        if (request.getRecipientId().isBlank()) {
            throw new IllegalArgumentException("recipientId cannot be blank");
        }
    }

    // --- Inner types (would normally be separate files) ---

    public interface NotificationChannel {
        DeliveryResult deliver(String recipientId, String subject, String body);
    }

    public record NotificationRequest(
        String id, String recipientId, String channelName,
        String subject, String templateName,
        Map<String, Object> templateVariables
    ) {
        public String getId() { return id; }
        public String getRecipientId() { return recipientId; }
        public String getChannelName() { return channelName; }
        public String getSubject() { return subject; }
        public String getTemplateName() { return templateName; }
        public Map<String, Object> getTemplateVariables() { return templateVariables; }
    }

    public record DeliveryResult(boolean success, String providerMessageId, String errorMessage) {
        public boolean isSuccess() { return success; }
        public String getProviderMessageId() { return providerMessageId; }
        public String getErrorMessage() { return errorMessage; }
    }

    public record NotificationResult(String requestId, String status, String messageId, String error) {
        static NotificationResult success(String reqId, String msgId) {
            return new NotificationResult(reqId, "SUCCESS", msgId, null);
        }
        static NotificationResult failed(String reqId, String error) {
            return new NotificationResult(reqId, "FAILED", null, error);
        }
        static NotificationResult rateLimited(String reqId) {
            return new NotificationResult(reqId, "RATE_LIMITED", null, "Rate limit exceeded");
        }
    }

    public record BatchResult(List<NotificationResult> results) {
        public long successCount() { return results.stream().filter(r -> "SUCCESS".equals(r.status())).count(); }
        public long failedCount() { return results.size() - successCount(); }
    }

    public record NotificationRecord(
        String id, String recipientId, String channel,
        String status, Instant timestamp, String providerMessageId
    ) {
        public String getId() { return id; }
        public String getRecipientId() { return recipientId; }
        public String getStatus() { return status; }
        public int getRetryCount() { return 0; } // simplified
    }

    public record NotificationStats(
        String channel, long total, long delivered, long failed, double successRate
    ) {}

    public interface NotificationRepository {
        void save(NotificationRecord record);
        List<NotificationRecord> findByRecipient(String recipientId, int limit);
        List<NotificationRecord> findByChannelAndTimeRange(String channel, Instant from, Instant to);
        List<NotificationRecord> findFailedByChannel(String channel, Instant since);
        NotificationRequest getOriginalRequest(String id);
        void incrementRetryCount(String id);
    }

    public interface TemplateEngine {
        String render(String templateName, Map<String, Object> variables);
    }

    public static class RateLimiter {
        private final int maxRequests;
        private final int windowSeconds;
        private final Map<String, List<Instant>> requestLog = new ConcurrentHashMap<>();

        public RateLimiter(int maxRequests, int windowSeconds) {
            this.maxRequests = maxRequests;
            this.windowSeconds = windowSeconds;
        }

        public boolean tryAcquire(String key) {
            Instant now = Instant.now();
            Instant cutoff = now.minusSeconds(windowSeconds);
            List<Instant> timestamps = requestLog.computeIfAbsent(key, k -> new ArrayList<>());
            synchronized (timestamps) {
                timestamps.removeIf(t -> t.isBefore(cutoff));
                if (timestamps.size() >= maxRequests) {
                    return false;
                }
                timestamps.add(now);
                return true;
            }
        }
    }
}
