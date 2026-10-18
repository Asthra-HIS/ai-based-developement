# Exercise 1.2: CO-STAR Challenge — Documentation

## Objective
Master controlling AI output quality and style by applying all six CO-STAR elements to produce dramatically different outputs from the same codebase.

## Setup
You have:
- A Java notification service implementation: `NotificationService.java`
- The CO-STAR reference card: `COSTAR-REFERENCE.md`

## Task: Produce Two Outputs

Using the **same codebase**, craft two different CO-STAR prompts to generate:

### Output 1: Technical API Documentation

Write a CO-STAR prompt to generate Javadoc documentation. Here's a starting point, adjust it to your needs:

```
Context: This is a Java notification service in a B2B SaaS platform for field service
management. It handles multi-channel notifications (email, SMS, push), rate limiting,
batch processing, retry logic, and delivery tracking.

Objective: Generate complete Javadoc for all public classes, interfaces, records
and methods.

Style: Formal, technical, precise — following Oracle's Javadoc conventions.

Tone: Neutral and authoritative.

Audience: Senior Java backend engineers who will maintain and extend this codebase.

Response: Javadoc comment blocks ready to paste above each public method, including
@param/@return/@throws tags. Include a class-level Javadoc comment summarizing the
service's architecture.
```

### Output 2: README Section for Stakeholders

Write a CO-STAR prompt to generate a marketing-friendly README section:

```
Context: This notification service is a core component of WorkWave's field service
management platform. It enables real-time customer communication across multiple
channels with enterprise-grade reliability.

Objective: Write a README.md section that explains what this module does and why
it's valuable.

Style: Engaging, persuasive, concise — like a well-written product page.

Tone: Enthusiastic but professional — confident without being salesy.

Audience: Potential enterprise buyers and investors evaluating our platform's
technical capabilities during due diligence.

Response: A markdown section with:
- A compelling opening paragraph
- 4-5 key capabilities as short bullet points with brief explanations
- A closing paragraph about reliability and scalability
- Total length: 200-300 words
```

## Comparison Questions

After generating both outputs, discuss:

1. **How different are they?** Place them side by side. Do they look like they came from the same codebase?
2. **Which CO-STAR elements had the most impact?** Try removing Audience from one prompt and regenerating. What changes?
3. **Accuracy check:** did the AI correctly describe the codebase's features in both outputs, or did it embellish/hallucinate in the marketing version?
4. **Real-world application:** think about a document you wrote recently. How would CO-STAR have helped you prompt for it?

## Bonus Challenge

Create a **third** output: an onboarding guide for a new junior developer joining the team. How does changing the Audience to "Junior developer, first week on the job" change the Style and level of detail?

## Key Takeaway
> The same code can produce wildly different documentation depending on your prompt. CO-STAR gives you a systematic way to control the output instead of hoping the AI guesses correctly.