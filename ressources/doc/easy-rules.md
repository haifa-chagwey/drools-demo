You are right that Easy Rules is very famous for its **Java Class format** (using `@Rule` annotations), but it actually supports **two different ways** to define rules.

Here is the difference between what you expected and what we are using:

### 1. The Java Class Way (Static)
This is the "standard" way where you create a real Java file for every rule:

```java
@Rule(name = "VIPDiscount")
public class VIPDiscountRule {
    @When
    public boolean isVIP(@Fact("type") String type) {
        return "VIP".equals(type);
    }
    @Then
    public void applyDiscount(@Fact("output") Map output) {
        output.put("discount", 20);
    }
}
```
*   **Problem:** If you want to change the discount from 20 to 30, you must **change the Java code, recompile, and restart the server.**

---

### 2. The MVEL Way (Dynamic) — *What we are using*
Easy Rules has a special module called `easy-rules-mvel`. It allows you to define the **exact same logic** as a simple string.

Instead of a Java class, we use a **Rule Builder** in `RuleService.java`:

```java
Rule easyRule = new RuleBuilder()
    .name(entity.getName())
    .when(new MVELCondition(entity.getCondition())) // The "if" string from DB
    .then(new MVELAction(entity.getAction()))       // The "do" string from DB
    .build();
```

### Why we chose the Dynamic Way:
1.  **Stored in Database:** You don't need to create hundreds of Java files. You just have one table in your database.
2.  **No Restarts:** You can add, delete, or update a rule while the server is running. The `reloadRules()` method in your `RuleService` instantly updates the engine without a restart.
3.  **Flexible:** It gives you the power of a database-driven system with the simplicity of Easy Rules.

### Summary
While Easy Rules *can* use Java classes, using **MVEL strings** (as we do here) is the standard way to make a **Dynamic Rule Engine** where rules can be managed through a UI or API without touching the source code.