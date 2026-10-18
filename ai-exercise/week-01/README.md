# Week 01 — Foundations & the "Pilot" Mindset

📊 **Session slides:**
- Google Slides: https://docs.google.com/presentation/d/1x1LNx69TjZZty93FjAXA0pJyKrdh_AO8jOBv46QeLto/edit?slide=id.g2e765b648f9_0_10#slide=id.g2e765b648f9_0_10
- PowerPoint (this folder): [Astra_healthtech_Week_1.pptx](Astra_healthtech_Week_1.pptx)

**Objective:** master the art of context and move away from "magic box" expectations.

Topics: tokens, context windows and non-deterministic output; the CO-STAR prompt framework; configuring `CLAUDE.md`; refactoring "messy" code with AI.

> All exercises are in **Java** (JDK 17 or newer).

## Exercises

The exercises run in the order they appear in the deck. Each one follows the slides that teach its topic.

| Deck slide | Exercise | Practises | Start here |
|---|---|---|---|
| 25 | **Exercise 1 — Context Window Stress Test** | Context window, context degradation and pre-contexting (slides 17, 20–24) | [exercise-1/INSTRUCTIONS.md](exercise-1/INSTRUCTIONS.md) |
| 28 | **Exercise 2 — CO-STAR Challenge** | CO-STAR prompt anatomy (slides 26–27) | [exercise-2/INSTRUCTIONS.md](exercise-2/INSTRUCTIONS.md) |
| 36–37 | **Exercise 0 — Research → Plan → Implement** (~20–30 min) | Divide & Conquer for larger changes (slides 34–35) | [exercise-0/research-plan-implement-java.md](exercise-0/research-plan-implement-java.md) |

## Folder structure

```
week-01/
├── README.md                             ← this file
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
