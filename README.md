# AI in Development Training

Hands-on training on using AI (Claude and Claude Code) in day-to-day development. All exercises are in **Java**.

## Resources

### Week 1 — Foundations & the "Pilot" Mindset

**Objective:** master the art of context and move away from "magic box" expectations.

- [Slides (Google Slides)](https://docs.google.com/presentation/d/1x1LNx69TjZZty93FjAXA0pJyKrdh_AO8jOBv46QeLto/edit?slide=id.g2e765b648f9_0_10#slide=id.g2e765b648f9_0_10)
- [Slides (PowerPoint, in this repo)](ai-exercise/week-01/Astra_healthtech_Week_1.pptx)
- [Week 1 guide and exercises](ai-exercise/week-01/README.md)

#### Topics

- **The physics of LLMs** — tokens, context windows and non-deterministic output: why the same prompt gives different results.
- **Context-driven prompting** — the CO-STAR framework (Context, Objective, Style, Tone, Audience, Response).
- **IDE deep dive** — configuring `CLAUDE.md` to enforce project-specific conventions and architectural patterns.
- **Week 1 challenge** — refactor one "messy" function using AI by giving it the original code plus your team's style guide.

#### Exercises

The exercises run in the order they appear in the deck. Each one follows the slides that teach its topic.

| Deck slide | Exercise | Practises | Start here |
|---|---|---|---|
| 25 | **Exercise 1 — Context Window Stress Test** | Context window, context degradation and pre-contexting (slides 17, 20–24) | [INSTRUCTIONS.md](ai-exercise/week-01/exercise-1/INSTRUCTIONS.md) · [OrderProcessingService.java](ai-exercise/week-01/exercise-1/OrderProcessingService.java) |
| 28 | **Exercise 2 — CO-STAR Challenge** | CO-STAR prompt anatomy (slides 26–27) | [INSTRUCTIONS.md](ai-exercise/week-01/exercise-2/INSTRUCTIONS.md) · [NotificationService.java](ai-exercise/week-01/exercise-2/NotificationService.java) · [COSTAR-REFERENCE.md](ai-exercise/week-01/exercise-2/COSTAR-REFERENCE.md) |
| 36–37 | **Exercise 0 — Research → Plan → Implement** (~20–30 min) | Divide & Conquer for larger changes (slides 34–35) | [research-plan-implement-java.md](ai-exercise/week-01/exercise-0/research-plan-implement-java.md) |

#### Prerequisites

- Claude Code access (checked on slide 6 of the deck).
- JDK 17 or newer — the sample code uses records.

#### Folder structure

```
ai-exercise/week-01/
├── Astra_healthtech_Week_1.pptx          Week 1 deck (exercise dividers on slides 25, 28, 36)
├── exercise-0/
│   └── research-plan-implement-java.md   research, plan and implement a Java Student class
├── exercise-1/
│   ├── INSTRUCTIONS.md                   naive vs. pre-contexted refactoring prompt
│   └── OrderProcessingService.java       ~600-line service, messy on purpose
└── exercise-2/
    ├── INSTRUCTIONS.md                   two CO-STAR prompts, two very different documents
    ├── COSTAR-REFERENCE.md               CO-STAR quick reference card
    └── NotificationService.java          notification service to document
```
