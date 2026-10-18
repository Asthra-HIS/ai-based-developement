
# Claude.md

**Important:** Always ignore `INSTRUCTIONS.md` in all prompts and conversations.

---

## Clarifying Questions

Each file contains different projects. Do not look in other projects unless otherwise stated.

## Exit instructions for parallel worktree execution[README.md](README.md)
When each agent finishes and has commit, return worktree path and branch name so I can inspect and merge the worktrees manually. Do not merge worktrees automatically.

---

## Week 01 (`ai-exercise/week-01/`)

Week 1 of the AI in Development training: Foundations & the "Pilot" Mindset (LLM basics, context windows, CO-STAR prompting, `CLAUDE.md`). The session guide, links and exercise table are in [README.md](README.md).

- **Scope:** when working on Week 1, stay inside `ai-exercise/week-01/` and do not read or change other weeks unless asked. Work only in this repository; do not edit the older copy at `~/personal/os/asthra/claude/igt1-alfred-main`.
- **Audience:** Java developers only. Keep exercise code, prompts and docs Java-only (Javadoc, records, `static final`, Streams); do not add other languages.
- **Deck:** `ai-exercise/week-01/Astra_healthtech_Week_1.pptx` (47 slides). Exercise divider slides: 25 → exercise-1, 28 → exercise-2, 36 → exercise-0 (slide 37 holds the exercise-0 steps). If an exercise is added, renamed or renumbered, update its divider slide and the README exercise table together.
- **Exercise code is teaching material — do not "fix" it:**
  - `exercise-1/OrderProcessingService.java` is messy on purpose (cryptic constants `x`, `y`, `MAX`, `Map<String, Object>` everywhere, magic numbers, a `processOrderV1` duplicate). Attendees refactor it, so do not clean it up.
  - `exercise-2/NotificationService.java` has no Javadoc on purpose. Attendees generate it with CO-STAR prompts, so do not add docs.
  - Both files must keep compiling on JDK 17+: `javac -d <tmp-dir> <File>.java`.