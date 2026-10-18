package com.workwave.orders;

import java.util.*;
import java.time.*;
import java.math.*;
import java.util.stream.*;

public class OrderProcessingService {

    private Map<String, Object> cfg;
    private Object db;
    private Object notif;
    private Object cache;
    private static final double x = 0.08;
    private static final double y = 0.15;
    private static final int MAX = 999;
    private static int counter = 0;

    // process order
    public Map<String, Object> processOrder(Map<String, Object> data) {
        counter++;
        Map<String, Object> res = new HashMap<>();
        if (data == null) {
            res.put("error", "no data");
            return res;
        }
        String id = (String) data.get("id");
        if (id == null || id.isEmpty()) {
            res.put("error", "bad id");
            return res;
        }
        List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");
        if (items == null || items.size() == 0) {
            res.put("error", "no items");
            return res;
        }
        String cust = (String) data.get("customerId");
        if (cust == null) {
            res.put("error", "no customer");
            return res;
        }
        // check customer
        Map<String, Object> custData = getCustomer(cust);
        if (custData == null) {
            res.put("error", "customer not found");
            return res;
        }
        if ((Boolean) custData.getOrDefault("blocked", false)) {
            res.put("error", "customer blocked");
            return res;
        }
        double total = 0;
        List<Map<String, Object>> processedItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> item = items.get(i);
            String sku = (String) item.get("sku");
            int qty = (Integer) item.getOrDefault("quantity", 0);
            if (qty <= 0 || qty > MAX) {
                res.put("error", "bad quantity for " + sku);
                return res;
            }
            double price = getPrice(sku);
            if (price < 0) {
                res.put("error", "invalid sku " + sku);
                return res;
            }
            // check stock
            int stock = checkStock(sku);
            if (stock < qty) {
                res.put("error", "insufficient stock for " + sku);
                return res;
            }
            double lineTotal = price * qty;
            // apply discount
            String disc = (String) data.get("discountCode");
            if (disc != null && !disc.isEmpty()) {
                double d = getDiscount(disc, sku);
                lineTotal = lineTotal - (lineTotal * d);
            }
            Map<String, Object> pItem = new HashMap<>();
            pItem.put("sku", sku);
            pItem.put("quantity", qty);
            pItem.put("unitPrice", price);
            pItem.put("lineTotal", lineTotal);
            processedItems.add(pItem);
            total += lineTotal;
        }
        // tax
        String region = (String) data.getOrDefault("region", "US");
        double tax = 0;
        if (region.equals("US")) {
            tax = total * x;
        } else if (region.equals("EU")) {
            tax = total * y; // NOTE: simplified, should be per-country
        } else if (region.equals("UK")) {
            tax = total * 0.20;
        } else {
            tax = total * 0.10;
        }
        double grandTotal = total + tax;
        // shipping
        double shipping = 0;
        String shipMethod = (String) data.getOrDefault("shipping", "standard");
        if (shipMethod.equals("express")) {
            shipping = 15.99;
        } else if (shipMethod.equals("overnight")) {
            shipping = 29.99;
        } else if (shipMethod.equals("standard")) {
            if (grandTotal > 100) {
                shipping = 0; // free shipping
            } else {
                shipping = 5.99;
            }
        }
        grandTotal += shipping;
        // loyalty points
        int pts = 0;
        String tier = (String) custData.getOrDefault("tier", "bronze");
        if (tier.equals("gold")) {
            pts = (int) (grandTotal * 2);
        } else if (tier.equals("silver")) {
            pts = (int) (grandTotal * 1.5);
        } else {
            pts = (int) grandTotal;
        }
        // build result
        res.put("orderId", id);
        res.put("customerId", cust);
        res.put("items", processedItems);
        res.put("subtotal", total);
        res.put("tax", tax);
        res.put("shipping", shipping);
        res.put("grandTotal", grandTotal);
        res.put("loyaltyPoints", pts);
        res.put("status", "PENDING");
        res.put("createdAt", LocalDateTime.now().toString());
        // save
        saveOrder(res);
        // notify
        sendNotification(cust, "Order " + id + " placed. Total: $" + String.format("%.2f", grandTotal));
        // update stock
        for (Map<String, Object> pi : processedItems) {
            decrementStock((String) pi.get("sku"), (Integer) pi.get("quantity"));
        }
        updateLoyaltyPoints(cust, pts);
        return res;
    }

    // get customer data
    private Map<String, Object> getCustomer(String id) {
        // simulated DB call
        Map<String, Object> c = new HashMap<>();
        c.put("id", id);
        c.put("name", "Test User");
        c.put("email", "test@test.com");
        c.put("tier", "gold");
        c.put("blocked", false);
        return c;
    }

    private double getPrice(String sku) {
        // simulated price lookup
        Map<String, Double> prices = new HashMap<>();
        prices.put("SKU001", 29.99);
        prices.put("SKU002", 49.99);
        prices.put("SKU003", 9.99);
        return prices.getOrDefault(sku, -1.0);
    }

    private int checkStock(String sku) {
        // simulated
        return 100;
    }

    private double getDiscount(String code, String sku) {
        // simulated
        if (code.equals("SAVE10")) return 0.10;
        if (code.equals("SAVE20")) return 0.20;
        if (code.equals("VIP")) return 0.25;
        return 0.0;
    }

    private void saveOrder(Map<String, Object> order) {
        // simulated DB save
        System.out.println("Saved order: " + order.get("orderId"));
    }

    private void sendNotification(String customerId, String message) {
        // simulated
        System.out.println("Notification to " + customerId + ": " + message);
    }

    private void decrementStock(String sku, int qty) {
        // simulated
        System.out.println("Decremented " + sku + " by " + qty);
    }

    private void updateLoyaltyPoints(String customerId, int points) {
        // simulated
        System.out.println("Added " + points + " points to " + customerId);
    }

    // batch process - run at midnight
    public List<Map<String, Object>> batchProcess(List<Map<String, Object>> orders) {
        List<Map<String, Object>> results = new ArrayList<>();
        int success = 0;
        int fail = 0;
        long start = System.currentTimeMillis();
        for (Map<String, Object> o : orders) {
            try {
                Map<String, Object> r = processOrder(o);
                if (r.containsKey("error")) {
                    fail++;
                    Map<String, Object> failResult = new HashMap<>();
                    failResult.put("orderId", o.get("id"));
                    failResult.put("status", "FAILED");
                    failResult.put("reason", r.get("error"));
                    results.add(failResult);
                } else {
                    success++;
                    results.add(r);
                }
            } catch (Exception e) {
                fail++;
                Map<String, Object> errResult = new HashMap<>();
                errResult.put("orderId", o.get("id"));
                errResult.put("status", "ERROR");
                errResult.put("reason", e.getMessage());
                results.add(errResult);
            }
        }
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("Batch done: " + success + " ok, " + fail + " failed in " + elapsed + "ms");
        return results;
    }

    // report thing
    public Map<String, Object> generateReport(String from, String to) {
        // this is supposed to generate a sales report
        // but it's never actually been finished
        Map<String, Object> report = new HashMap<>();
        report.put("from", from);
        report.put("to", to);
        report.put("status", "NOT_IMPLEMENTED");
        // TODO: implement this
        return report;
    }

    // check if order can be cancelled
    public boolean canCancel(Map<String, Object> order) {
        String status = (String) order.get("status");
        if (status == null) return false;
        if (status.equals("PENDING")) return true;
        if (status.equals("PROCESSING")) {
            // check if it's been less than 30 minutes
            String created = (String) order.get("createdAt");
            if (created != null) {
                LocalDateTime ct = LocalDateTime.parse(created);
                LocalDateTime now = LocalDateTime.now();
                long mins = java.time.Duration.between(ct, now).toMinutes();
                if (mins < 30) return true;
            }
        }
        return false;
    }

    public Map<String, Object> cancelOrder(Map<String, Object> order) {
        Map<String, Object> res = new HashMap<>();
        if (!canCancel(order)) {
            res.put("error", "cannot cancel");
            return res;
        }
        String id = (String) order.get("orderId");
        // restore stock
        List<Map<String, Object>> items = (List<Map<String, Object>>) order.get("items");
        if (items != null) {
            for (Map<String, Object> item : items) {
                String sku = (String) item.get("sku");
                int qty = (Integer) item.get("quantity");
                incrementStock(sku, qty);
            }
        }
        // reverse loyalty
        String cust = (String) order.get("customerId");
        int pts = (Integer) order.getOrDefault("loyaltyPoints", 0);
        updateLoyaltyPoints(cust, -pts);
        // update status
        order.put("status", "CANCELLED");
        order.put("cancelledAt", LocalDateTime.now().toString());
        saveOrder(order);
        // notify
        sendNotification(cust, "Order " + id + " has been cancelled.");
        // refund
        double total = (Double) order.get("grandTotal");
        processRefund(cust, total);
        res.put("status", "CANCELLED");
        res.put("refundAmount", total);
        return res;
    }

    private void incrementStock(String sku, int qty) {
        System.out.println("Incremented " + sku + " by " + qty);
    }

    private void processRefund(String customerId, double amount) {
        System.out.println("Refund of $" + amount + " to " + customerId);
    }

    // calculate priority
    public int calcPriority(Map<String, Object> order) {
        int p = 0;
        double total = (Double) order.getOrDefault("grandTotal", 0.0);
        if (total > 1000) p += 3;
        else if (total > 500) p += 2;
        else if (total > 100) p += 1;
        String ship = (String) order.getOrDefault("shipping", "standard");
        if (ship.equals("overnight")) p += 3;
        else if (ship.equals("express")) p += 2;
        String cust = (String) order.get("customerId");
        if (cust != null) {
            Map<String, Object> custData = getCustomer(cust);
            String tier = (String) custData.getOrDefault("tier", "bronze");
            if (tier.equals("gold")) p += 2;
            else if (tier.equals("silver")) p += 1;
        }
        return p;
    }

    // old method - don't use
    @Deprecated
    public Map<String, Object> processOrderV1(Map<String, Object> data) {
        // This was the original implementation
        // kept for backwards compatibility but should not be called
        Map<String, Object> res = new HashMap<>();
        String id = (String) data.get("id");
        List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");
        double total = 0;
        for (Map<String, Object> item : items) {
            double price = (Double) item.get("price");
            int qty = (Integer) item.get("quantity");
            total += price * qty;
        }
        res.put("orderId", id);
        res.put("total", total);
        res.put("status", "OK");
        return res;
    }

    // validate address - incomplete
    public boolean validateAddress(Map<String, Object> addr) {
        if (addr == null) return false;
        String street = (String) addr.get("street");
        String city = (String) addr.get("city");
        String zip = (String) addr.get("zip");
        String country = (String) addr.get("country");
        if (street == null || street.length() < 5) return false;
        if (city == null || city.length() < 2) return false;
        if (zip == null) return false;
        if (country == null) return false;
        // TODO: validate zip format per country
        // TODO: validate against address verification API
        return true;
    }

    // apply promotions
    public Map<String, Object> applyPromo(Map<String, Object> order, String promoCode) {
        Map<String, Object> res = new HashMap<>();
        double gt = (Double) order.get("grandTotal");
        double sub = (Double) order.get("subtotal");
        if (promoCode == null || promoCode.isEmpty()) {
            res.put("error", "no promo code");
            return res;
        }
        // hardcoded promos
        if (promoCode.equals("FLAT10")) {
            gt = gt - 10;
            if (gt < 0) gt = 0;
        } else if (promoCode.equals("FLAT25")) {
            if (sub >= 100) {
                gt = gt - 25;
            } else {
                res.put("error", "minimum order $100 for FLAT25");
                return res;
            }
        } else if (promoCode.equals("FREESHIP")) {
            double sh = (Double) order.getOrDefault("shipping", 0.0);
            gt = gt - sh;
            order.put("shipping", 0.0);
        } else if (promoCode.equals("BOGO")) {
            // buy one get one - complicated
            List<Map<String, Object>> items = (List<Map<String, Object>>) order.get("items");
            if (items != null && items.size() > 0) {
                // find cheapest item
                double minPrice = Double.MAX_VALUE;
                for (Map<String, Object> item : items) {
                    double lp = (Double) item.get("lineTotal");
                    int q = (Integer) item.get("quantity");
                    double up = lp / q;
                    if (up < minPrice) minPrice = up;
                }
                gt = gt - minPrice;
            }
        } else {
            res.put("error", "unknown promo");
            return res;
        }
        res.put("grandTotal", gt);
        res.put("promoApplied", promoCode);
        return res;
    }

    // helper - format currency
    private String fmt(double val) {
        return String.format("$%.2f", val);
    }

    // helper
    private String genId() {
        return "ORD-" + System.currentTimeMillis() + "-" + (++counter);
    }

    // something about inventory sync
    public void syncInventory() {
        // this runs every hour
        // pull from external system
        System.out.println("Syncing inventory...");
        // TODO: implement actual sync
        // for now just log
        System.out.println("Inventory sync complete (noop)");
    }

    // export orders
    public String exportToCsv(List<Map<String, Object>> orders) {
        StringBuilder sb = new StringBuilder();
        sb.append("orderId,customerId,subtotal,tax,shipping,grandTotal,status\n");
        for (Map<String, Object> o : orders) {
            sb.append(o.getOrDefault("orderId", ""));
            sb.append(",");
            sb.append(o.getOrDefault("customerId", ""));
            sb.append(",");
            sb.append(o.getOrDefault("subtotal", "0"));
            sb.append(",");
            sb.append(o.getOrDefault("tax", "0"));
            sb.append(",");
            sb.append(o.getOrDefault("shipping", "0"));
            sb.append(",");
            sb.append(o.getOrDefault("grandTotal", "0"));
            sb.append(",");
            sb.append(o.getOrDefault("status", "UNKNOWN"));
            sb.append("\n");
        }
        return sb.toString();
    }

    // merge duplicate orders (sometimes happens with retries)
    public List<Map<String, Object>> dedup(List<Map<String, Object>> orders) {
        Map<String, Map<String, Object>> seen = new LinkedHashMap<>();
        for (Map<String, Object> o : orders) {
            String id = (String) o.get("orderId");
            if (id != null && !seen.containsKey(id)) {
                seen.put(id, o);
            }
        }
        return new ArrayList<>(seen.values());
    }

    // split order by warehouse
    public Map<String, List<Map<String, Object>>> splitByWarehouse(Map<String, Object> order) {
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        List<Map<String, Object>> items = (List<Map<String, Object>>) order.get("items");
        if (items == null) return result;
        for (Map<String, Object> item : items) {
            String sku = (String) item.get("sku");
            String wh = getWarehouse(sku);
            result.computeIfAbsent(wh, k -> new ArrayList<>()).add(item);
        }
        return result;
    }

    private String getWarehouse(String sku) {
        // simulated warehouse lookup
        if (sku.startsWith("SKU0")) return "WAREHOUSE_EAST";
        if (sku.startsWith("SKU1")) return "WAREHOUSE_WEST";
        return "WAREHOUSE_CENTRAL";
    }

    // update order status
    public void updateStatus(Map<String, Object> order, String newStatus) {
        String[] valid = {"PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"};
        boolean ok = false;
        for (String v : valid) {
            if (v.equals(newStatus)) {
                ok = true;
                break;
            }
        }
        if (!ok) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }
        String current = (String) order.get("status");
        // state machine check - very basic
        if (current != null && current.equals("CANCELLED")) {
            throw new IllegalStateException("Cannot transition from CANCELLED");
        }
        if (current != null && current.equals("DELIVERED") && !newStatus.equals("CANCELLED")) {
            throw new IllegalStateException("Cannot transition from DELIVERED to " + newStatus);
        }
        order.put("status", newStatus);
        order.put("updatedAt", LocalDateTime.now().toString());
        saveOrder(order);
        String cust = (String) order.get("customerId");
        if (cust != null) {
            sendNotification(cust, "Order " + order.get("orderId") + " status: " + newStatus);
        }
    }

    // calculate estimated delivery
    public String estimateDelivery(Map<String, Object> order) {
        String ship = (String) order.getOrDefault("shipping", "standard");
        LocalDate now = LocalDate.now();
        LocalDate est;
        if (ship.equals("overnight")) {
            est = now.plusDays(1);
        } else if (ship.equals("express")) {
            est = now.plusDays(3);
        } else {
            est = now.plusDays(7);
        }
        // skip weekends
        while (est.getDayOfWeek() == DayOfWeek.SATURDAY || est.getDayOfWeek() == DayOfWeek.SUNDAY) {
            est = est.plusDays(1);
        }
        return est.toString();
    }

    // this was for A/B testing, probably dead code now
    public double calculateShippingV2(double orderTotal, String method, boolean isPrime) {
        if (isPrime) return 0;
        if (method.equals("express")) return 12.99;
        if (method.equals("overnight")) return 24.99;
        if (orderTotal > 75) return 0;
        return 4.99;
    }

    // audit log
    public void auditLog(String action, String orderId, String userId) {
        String ts = LocalDateTime.now().toString();
        String entry = ts + "|" + action + "|" + orderId + "|" + userId;
        System.out.println("AUDIT: " + entry);
        // TODO: write to actual audit table
    }

    // recalculate totals (used after modifications)
    public void recalculate(Map<String, Object> order) {
        List<Map<String, Object>> items = (List<Map<String, Object>>) order.get("items");
        if (items == null) return;
        double sub = 0;
        for (Map<String, Object> item : items) {
            double lt = (Double) item.getOrDefault("lineTotal", 0.0);
            sub += lt;
        }
        order.put("subtotal", sub);
        String region = (String) order.getOrDefault("region", "US");
        double tax;
        if (region.equals("US")) tax = sub * x;
        else if (region.equals("EU")) tax = sub * y;
        else if (region.equals("UK")) tax = sub * 0.20;
        else tax = sub * 0.10;
        order.put("tax", tax);
        double ship = (Double) order.getOrDefault("shipping", 0.0);
        order.put("grandTotal", sub + tax + ship);
    }

    // get order summary for display
    public String getSummary(Map<String, Object> order) {
        StringBuilder sb = new StringBuilder();
        sb.append("Order: " + order.get("orderId") + "\n");
        sb.append("Customer: " + order.get("customerId") + "\n");
        sb.append("Status: " + order.get("status") + "\n");
        sb.append("Items:\n");
        List<Map<String, Object>> items = (List<Map<String, Object>>) order.get("items");
        if (items != null) {
            for (Map<String, Object> item : items) {
                sb.append("  - " + item.get("sku") + " x" + item.get("quantity") + " = " + fmt((Double) item.get("lineTotal")) + "\n");
            }
        }
        sb.append("Subtotal: " + fmt((Double) order.getOrDefault("subtotal", 0.0)) + "\n");
        sb.append("Tax: " + fmt((Double) order.getOrDefault("tax", 0.0)) + "\n");
        sb.append("Shipping: " + fmt((Double) order.getOrDefault("shipping", 0.0)) + "\n");
        sb.append("Total: " + fmt((Double) order.getOrDefault("grandTotal", 0.0)) + "\n");
        return sb.toString();
    }
}
