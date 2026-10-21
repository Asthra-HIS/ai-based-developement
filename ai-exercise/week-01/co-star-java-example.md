# CO-STAR Example: A BMI Calculator in Java

CO-STAR is a simple way to write a good prompt. It has six parts: **C**ontext, **O**bjective, **S**tyle, **T**one, **A**udience and **R**esponse.
This file shows one prompt built from all six parts, the Java code it would produce, and why it works.

## What each letter means for a developer

| Letter | CO-STAR part | Software term |
|--------|--------------|---------------|
| C | Context | Environment, config and preconditions (Java version, build, dependencies) |
| O | Objective | Method contract and acceptance criteria |
| S | Style | Code style guide and linter rules |
| T | Tone | Wording and voice of the output, for example error messages |
| A | Audience | Who reads, uses and maintains the code |
| R | Response | Return type and output format |

## The prompt (copy and paste it)

````text
# CONTEXT
I work on a healthtech Java service. We use Java 17. We cannot add new dependencies.
The code must compile with plain `javac`. This is one small task: add a BMI calculator.

# OBJECTIVE
Create `BmiCalculator.java` with a static method `calculate(double weightKg, double heightM)`.
It returns the BMI value and a category: UNDERWEIGHT (below 18.5), NORMAL (below 25),
OVERWEIGHT (below 30) or OBESE (30 or more).
It must throw IllegalArgumentException if weight or height is not greater than 0. Do this one task only.

# STYLE
Follow Google Java Style. Use a record for the result, an enum for the category,
`static final` constants for the thresholds and a private constructor.
Write Javadoc on every public member. Do not use external libraries or `System.out`.

# TONE
Neutral and technical. Error messages must be short, clear and name the bad value.

# AUDIENCE
A mid-level Java developer who is new to this repo and will maintain this code.

# RESPONSE
Give the full file in one java code block, then a short bullet list of what you did.
Do not change anything outside this class.
````

## The result (`BmiCalculator.java`)

```java
/** Calculates Body Mass Index (BMI) and its category. */
public final class BmiCalculator {
  /** BMI below this value is underweight. */
  public static final double UNDERWEIGHT_LIMIT = 18.5;
  /** BMI below this value (and not underweight) is normal. */
  public static final double NORMAL_LIMIT = 25.0;
  /** BMI below this value (and not normal) is overweight. Anything higher is obese. */
  public static final double OVERWEIGHT_LIMIT = 30.0;

  /** The BMI category. */
  public enum Category {
    /** BMI below 18.5. */
    UNDERWEIGHT,
    /** BMI from 18.5 up to, but not including, 25. */
    NORMAL,
    /** BMI from 25 up to, but not including, 30. */
    OVERWEIGHT,
    /** BMI of 30 or more. */
    OBESE
  }

  /**
   * The outcome of a BMI calculation.
   *
   * @param bmi the BMI value in kg/m^2
   * @param category the BMI category
   */
  public record BmiResult(double bmi, Category category) {}

  private BmiCalculator() {}

  /**
   * Calculates the BMI and its category.
   *
   * @param weightKg body weight in kilograms, must be greater than 0
   * @param heightM body height in metres, must be greater than 0
   * @return the BMI value and its category
   * @throws IllegalArgumentException if weight or height is not greater than 0
   */
  public static BmiResult calculate(double weightKg, double heightM) {
    // "!(x > 0)" also rejects NaN.
    if (!(weightKg > 0) || !(heightM > 0)) {
      throw new IllegalArgumentException(
          "weightKg and heightM must be greater than 0, but were " + weightKg + " and " + heightM);
    }
    double bmi = weightKg / (heightM * heightM);
    if (bmi < UNDERWEIGHT_LIMIT) {
      return new BmiResult(bmi, Category.UNDERWEIGHT);
    }
    if (bmi < NORMAL_LIMIT) {
      return new BmiResult(bmi, Category.NORMAL);
    }
    if (bmi < OVERWEIGHT_LIMIT) {
      return new BmiResult(bmi, Category.OVERWEIGHT);
    }
    return new BmiResult(bmi, Category.OBESE);
  }
}
```

## Why this works

- **Context** (Java 17, no dependencies, plain `javac`): the code uses only core Java and compiles with no build tool.
- **Objective** (signature, four categories, thresholds, throw): you get `calculate(double, double)`, the `Category` enum and the `IllegalArgumentException`.
- **Style**: the record `BmiResult`, the `static final` limits, the private constructor and the Javadoc all come from the style rules. Nothing uses `System.out`.
- **Tone**: the error message is short and shows the bad values, as asked.
- **Audience**: every public member has Javadoc, so a new developer can follow it without asking.
- **Response**: one file in one code block, and nothing else in the repo is touched.

## Bad prompt vs good prompt

**Bad prompt:** `Write a BMI calculator`

It leaves out almost everything: the Java version and dependencies (Context), the inputs, categories and bad-input rules (Objective), the code style (Style), the voice (Tone), the reader (Audience) and the output format (Response).
The AI has to guess. You may get a `main` method with `Scanner` and `System.out`, magic numbers and no input checks.

**Good prompt:** the CO-STAR prompt above. Same task, but nothing is left to guess.
