package com.haifachagwey.ruleengine.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CamundaRuleServiceTest {

    @Autowired
    private CamundaRuleService camundaRuleService;

    @Test
    public void testEvaluateDish() {
        assertEquals("Pasta", camundaRuleService.evaluateDish("Spring", 5));
        assertEquals("Pizza", camundaRuleService.evaluateDish("Spring", 15));
        assertEquals("Salad", camundaRuleService.evaluateDish("Summer", 5));
        assertEquals("Steak", camundaRuleService.evaluateDish("Winter", 5));
    }

    @Test
    public void testEvaluateBeverage() {
        assertEquals("Coffee", camundaRuleService.evaluateBeverage("Morning"));
        assertEquals("Tea", camundaRuleService.evaluateBeverage("Afternoon"));
        assertEquals("Beer", camundaRuleService.evaluateBeverage("Evening"));
    }

    @Test
    public void testEvaluateFromXml() {
        String customDmn = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<definitions xmlns=\"https://www.omg.org/spec/DMN/20191111/MODEL/\" id=\"custom\" name=\"Custom\" namespace=\"http://camunda.org/schema/1.0/dmn\">\n" +
                "  <decision id=\"custom-decision\" name=\"Custom Decision\">\n" +
                "    <decisionTable id=\"DecisionTable_3\">\n" +
                "      <input id=\"Input_4\">\n" +
                "        <inputExpression id=\"InputExpression_4\" typeRef=\"string\"><text>input</text></inputExpression>\n" +
                "      </input>\n" +
                "      <output id=\"Output_3\" name=\"output\" typeRef=\"string\" />\n" +
                "      <rule id=\"Rule_1\">\n" +
                "        <inputEntry id=\"Entry_1\"><text>\"Hello\"</text></inputEntry>\n" +
                "        <outputEntry id=\"Entry_2\"><text>\"World\"</text></outputEntry>\n" +
                "      </rule>\n" +
                "    </decisionTable>\n" +
                "  </decision>\n" +
                "</definitions>";

        String result = camundaRuleService.evaluateFromXml(customDmn, "custom-decision", java.util.Map.of("input", "Hello"));
        assertEquals("World", result);
    }

    @Test
    public void testAddNewRuleAndEvaluate() {
        // 1. Define a new DMN XML (e.g., a Discount Rule)
        // This represents a rule being added dynamically (e.g., from a UI or database)
        String discountDmnXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<definitions xmlns=\"https://www.omg.org/spec/DMN/20191111/MODEL/\" id=\"discount\" name=\"Discount\" namespace=\"http://camunda.org/schema/1.0/dmn\">\n" +
                "  <decision id=\"discount-decision\" name=\"Discount Decision\">\n" +
                "    <decisionTable id=\"DecisionTable_Discount\">\n" +
                "      <input id=\"Input_CustomerType\" label=\"Customer Type\">\n" +
                "        <inputExpression id=\"Exp_Type\" typeRef=\"string\"><text>customerType</text></inputExpression>\n" +
                "      </input>\n" +
                "      <output id=\"Output_Discount\" name=\"discount\" typeRef=\"integer\" />\n" +
                "      <rule id=\"Rule_VIP\">\n" +
                "        <inputEntry id=\"Entry_VIP\"><text>\"VIP\"</text></inputEntry>\n" +
                "        <outputEntry id=\"Entry_VIP_Val\"><text>20</text></outputEntry>\n" +
                "      </rule>\n" +
                "      <rule id=\"Rule_Regular\">\n" +
                "        <inputEntry id=\"Entry_Regular\"><text>\"Regular\"</text></inputEntry>\n" +
                "        <outputEntry id=\"Entry_Regular_Val\"><text>5</text></outputEntry>\n" +
                "      </rule>\n" +
                "    </decisionTable>\n" +
                "  </decision>\n" +
                "</definitions>";

        // 2. Test the "newly added" rule using the generic evaluateFromXml method
        // Case: VIP customer
        Object vipDiscount = camundaRuleService.evaluateFromXml(discountDmnXml, "discount-decision", 
                java.util.Map.of("customerType", "VIP"));
        assertEquals(20, vipDiscount);

        // Case: Regular customer
        Object regularDiscount = camundaRuleService.evaluateFromXml(discountDmnXml, "discount-decision", 
                java.util.Map.of("customerType", "Regular"));
        assertEquals(5, regularDiscount);
    }
}