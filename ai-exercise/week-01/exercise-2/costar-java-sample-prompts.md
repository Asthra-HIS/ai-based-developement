# CO-STAR Sample Prompts — Simple Java Code Generation

Four small prompts that use the [CO-STAR framework](exercise-2/COSTAR-REFERENCE.md) to generate Java code. Each one asks for a single small class, so the output is easy to read and check.

> **Java:** JDK 17 or newer. Paste a prompt into Claude Code or any chat, then compile the result with `javac`.

---

## Before and after: why CO-STAR helps

**Without CO-STAR** (vague):

```text
Write a Java method to calculate BMI.
```

The model has to guess everything: the units, the return type, whether to validate input, how much to comment, and what the output should look like.

**With CO-STAR** (see Prompt 1 below): the same request now says what the code is for, who will read it, and what shape the answer must take.

---

## Prompt 1 — BMI calculator (a static method)

```text
Context: I am building a small patient-intake tool for a healthtech app. Patients
enter their weight in kilograms and their height in metres.

Objective: Write a Java class named BmiCalculator with one public static method,
calculate(double weightKg, double heightM), that returns the BMI as a double.
Throw an IllegalArgumentException if weight or height is zero or negative.

Style: Clean, idiomatic Java 17. Use a Javadoc comment on the class and the method.
Do not use any external libraries.

Tone: Neutral and professional. No jokes and no filler text.

Audience: Junior Java developers who are learning to read well-structured code.

Response: One Java code block containing the complete BmiCalculator.java file,
followed by a two-line explanation of the formula.
```

**Expected result:** one class, one static method, a guard clause that throws on bad input, and the formula `weightKg / (heightM * heightM)`.

---

## Prompt 2 — Temperature converter (constants and two methods)

```text
Context: A hospital dashboard shows body temperature. Some devices report Celsius
and some report Fahrenheit.

Objective: Write a Java class named TemperatureConverter with two public static
methods, celsiusToFahrenheit(double c) and fahrenheitToCelsius(double f).
Put the numbers 32 and 1.8 in private static final constants.

Style: Clean Java 17 with Javadoc on each public method. Use descriptive names
and no magic numbers.

Tone: Neutral and concise.

Audience: Developers who are new to the codebase and need to understand it quickly.

Response: One Java code block with the complete TemperatureConverter.java file.
Then add a short main method example, in a separate code block, that converts
37.0 Celsius to Fahrenheit and prints the result.
```

**Expected result:** two methods, named constants, and a small `main` demo that prints `98.60000000000001`. This is a good moment to point out floating-point output.

---

## Prompt 3 — Patient record (a Java `record` with validation)

```text
Context: A clinic app needs a simple, immutable object to hold basic patient data.

Objective: Write a Java record named Patient with the components String name,
int age and String email. Add a compact constructor that rejects a blank name,
an age below 0 or above 120, and an email that does not contain "@".
Throw IllegalArgumentException with a clear message for each rule.

Style: Modern Java 17. Use a record, not a regular class. Add Javadoc on the record.

Tone: Neutral and professional.

Audience: Java developers who know classes but have not used records before.

Response: One Java code block containing the complete Patient.java file. After the
code, list the three validation rules as three short bullet points.
```

**Expected result:** a `record` with a compact constructor and three separate checks. Attendees see that `equals`, `hashCode`, `toString` and the getters come for free.

---

## Prompt 4 — Filter a list with Streams

```text
Context: I have a List<Patient> in memory. Patient is a Java 17 record with the
components String name, int age and String email.

Objective: Write a Java class named PatientFilter with one public static method,
adultNames(List<Patient> patients), that returns a List<String> of the names of
all patients aged 18 or over, sorted alphabetically.

Style: Use the Streams API (filter, map, sorted, toList). Do not use for loops.
Add Javadoc on the method.

Tone: Neutral and concise.

Audience: Java developers who are learning the Streams API.

Response: One Java code block containing PatientFilter.java. Under it, add one
line that explains what each stream step does.
```

**Expected result:** a single stream pipeline: `filter` on age, `map` to name, `sorted`, then `toList()`.

---

## How to run what the model gives you

```bash
# Save the generated code as BmiCalculator.java, then:
javac -d /tmp/out BmiCalculator.java
```

## Try it yourself

1. Run Prompt 1 as written and look at the output.
2. Delete **Audience** and run it again. Does the Javadoc or the explanation change?
3. Change **Response** to "Return only the code, with no explanation." What disappears?
4. Change only **one** element at a time, as the [quick reference](exercise-2/COSTAR-REFERENCE.md) suggests, so you can see what each element does.

> **Tip:** for tiny tasks like these, Context + Objective + Response is often enough. The six-element version shows what each part does.
