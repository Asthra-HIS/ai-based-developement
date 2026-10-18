# Using AI Models: Research, Plan & Implement
### Training Exercise — Java Class Example

---

## Overview

This exercise demonstrates how to use an AI model across three structured steps:
**Research → Plan → Implement**

You will use AI to research a concept, save a plan, and then use that plan to generate working Java code.

---

## Step 1 — Research

**Goal:** Ask the AI to explain the concept and save the result.

### Prompt 1 — Ask for research

```
What are the key concepts I need to understand before creating a Java class?
Explain in simple terms with a real-world analogy. Format your response in Markdown.
```

### Prompt 2 — Save the research

```
Save that research as a Markdown file called research.md
```

✅ **You should now have a file called `research.md`**

---

## Step 2 — Plan

**Goal:** Ask the AI to design the class and save the plan as a checklist.

### Prompt 3 — Ask for a plan

```
I want to create a Java class to represent a Student in a school system.
What fields, constructors, and methods should it have?
Format your response in Markdown with clear headings and a checklist.
```

### Prompt 4 — Save the plan

```
Save that as plan.md
```

✅ **You should now have a file called `plan.md`**

---

## Step 3 — Implement

**Goal:** Ask the AI to read the saved plan and write the Java code based on it.

### Prompt 5 — Implement from the plan

```
Read the plan in plan.md and implement the Java Student class exactly as planned.
Follow every item in the checklist.
```

✅ **The AI will produce a complete Java class based on your plan**

Expected output will include:
- `name`, `age`, and `grade` fields
- A constructor
- Getters and setters
- A `hasPassed()` method that returns `true` if grade is 50 or above

---

## Bonus — Iterate

**Goal:** Show how to update the plan and reflect changes in the code.

### Prompt 6 — Extend the plan

```
Update plan.md to add a static student counter field,
then implement that change in the Java class.
```

✅ **The AI updates the plan document and the code together**

---

## Key Takeaways

| Step | What You Did | What AI Did |
|------|-------------|-------------|
| Research | Asked a concept question | Explained with analogies |
| Plan | Described the class requirements | Designed the structure |
| Implement | Referenced the saved plan | Generated working Java code |
| Iterate | Asked for a change | Updated both plan and code |

---

## Discussion Points for Trainer

- The AI does not write code blindly — it thinks through the design first
- Saving to `research.md` and `plan.md` gives the AI memory across steps
- Referring back to the plan keeps the implementation consistent
- Small follow-up prompts can grow the solution step by step
- This workflow applies to any programming language, not just Java

---

*Exercise duration: approximately 20–30 minutes*
