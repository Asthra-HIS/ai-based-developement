# CO-STAR Prompt Framework — Quick Reference

> **Origin:** The CO-STAR framework was popularized by Sheila Teo and won the GovTech Singapore GPT-4 Prompt Engineering Competition (2024).
> **Source:** https://towardsdatascience.com/how-i-won-singapores-gpt-4-prompt-engineering-competition-34c195a93d41/

---

## The Six Elements

| Element | What It Means | Example |
|---------|---------------|---------|
| **C** — Context | Background info the AI needs to understand the situation | "This is a notification microservice in a B2B SaaS platform for field service management companies." |
| **O** — Objective | The specific task you want accomplished | "Generate complete Javadoc documentation for all public methods." |
| **S** — Style | The writing or coding style to follow | "Formal, technical, precise — following Oracle's Javadoc conventions." |
| **T** — Tone | The emotional quality of the output | "Neutral and authoritative — no casual language or humor." |
| **A** — Audience | Who will read/use the output | "Senior backend engineers maintaining this codebase." |
| **R** — Response | The format and structure of the output | "Javadoc comment blocks ready to paste above each public method, including @param, @return, and @throws tags." |

---

## Why It Works

LLMs generate tokens based on statistical patterns from their training data. Each CO-STAR element **constrains** the probability distribution of the output:

- **Context** primes the model with domain-specific vocabulary and patterns
- **Objective** focuses the task (avoids "helpful assistant" generic answers)
- **Style** selects from the model's learned writing patterns
- **Tone** fine-tunes emotional register
- **Audience** adjusts complexity and assumed knowledge
- **Response** constrains the structural format

Without these constraints, the model defaults to its most "average" output — generic, middle-of-the-road, often ChatGPT-style friendly prose.

---

## Template

```
Context: [Background information about the project, codebase, or situation]

Objective: [Exactly what you want the AI to produce]

Style: [Formal/Informal/Technical/Conversational/Persuasive/Academic]

Tone: [Neutral/Enthusiastic/Urgent/Empathetic/Authoritative/Friendly]

Audience: [Who will consume this output — their role, expertise level, needs]

Response: [Desired format — bullet list, paragraph, code block, table, JSON, etc.]
```

---

## Common Mistakes

1. **Skipping Context:** "Write documentation for this code" → the AI doesn't know if this is a startup prototype or a banking system.
2. **Vague Objective:** "Make this better" → better how? Faster? More readable? More secure?
3. **Forgetting Audience:** documentation for junior developers vs. senior engineers looks completely different.
4. **No Response format:** the AI picks its own format, which may not be what you need.

---

## Pro Tips

- **You don't need all six every time.** For simple tasks, Context + Objective + Response is often enough.
- **Audience is the secret weapon.** It changes vocabulary, depth, and assumed knowledge more than any other element.
- **Style and Tone are different.** Style = structural (formal, technical). Tone = emotional (warm, urgent). You can have "formal style, warm tone" or "casual style, authoritative tone."
- **Iterate on one element.** If the output is close but not right, change just one CO-STAR element and regenerate.
