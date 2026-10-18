# Exercise 1.1: Context Window Stress Test

## Objective
Demonstrate how pre-contexting the AI improves refactoring quality, and understand the "lost in the middle" limitation of LLMs.

## Setup
You have a ~600-line, poorly-documented Java service class: `OrderProcessingService.java`

## Phase 1: Naive Prompt

Open a **new conversation** with Claude (or use Claude Code). Paste the entire file and use this prompt:

> Refactor the `processOrder` method at the top of this class to improve its readability and maintainability.

**Record your observations:**
- Did the AI understand the dependencies between methods?
- Did it preserve the business logic correctly?
- Did it introduce any new methods or classes that don't fit the codebase?
- How confident are you in the result?

## Phase 2: Pre-Contexted Prompt

Open a **new conversation**. Paste the same file, but this time use a two-step approach:

**Step 1 — Context building:**
> Before making any changes, analyze this Java class and tell me:
> 1. What are the main responsibilities of this service?
> 2. What patterns or conventions does it use (naming, error handling, data structures)?
> 3. What are the dependencies between the public methods and private helper methods?
> 4. Are there any dead code or incomplete features?
> 5. Are there any mutable `static` fields or cryptically named constants that should be well-named `static final` constants?
> 6. Are there any `Map<String, Object>` structures that can be replaced with typed classes or records?
> 7. Are there any if-statements that can be replaced with more expressive code (switch expressions, pattern matching, Streams, etc.)?
> 8. Is there any critical readability issue that needs to be addressed?

**Step 2 — Targeted refactoring (in the same conversation):**
> Now refactor the `processOrder` method. Based on your analysis:
> - Keep the same error handling pattern used elsewhere in the class
> - Preserve all the business rules (tax rates, shipping tiers, loyalty points)
> - Extract logical sub-operations into well-named and human-understandable private methods
> - Add meaningful parameter validation
> - Remove eventual dead code
> - Group `static final` constants at the top of the class with a meaningful naming convention
> - Prefer typed classes or records to `Map<String, Object>` for structured data
> - Avoid excessive use of if-statements, use more expressive constructs if possible (e.g., switch expressions, pattern matching, Streams, polymorphism)
> - Do not add any methods or classes that don't fit the codebase and the existing business logic

**Record your observations:**
- How does this output compare to Phase 1?
- Did the AI preserve more business logic correctly?
- Is the refactored code more consistent with the rest of the file?

## Phase 3: Group Discussion

Compare your Phase 1 and Phase 2 outputs. Discuss:

1. **Quality difference:** which output would you trust to merge into production?
2. **Cost of context:** the two-step approach uses more tokens; is it worth it?
3. **The pilot analogy:** how is this like a pilot using a pre-flight checklist?

## Key Takeaway
> LLMs allocate attention across all tokens in the context window. Pre-contexting creates a compressed "mental model" that improves subsequent reasoning. Always warm up the AI before asking it to do complex work.