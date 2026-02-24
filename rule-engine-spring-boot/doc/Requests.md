Here is a comprehensive list of request bodies and examples for every API endpoint to test the updated dynamic rule engine.

I have included the **Recommended Approach** (using separate `condition` and `action` fields) as it's better for UI integration.

### 1. List All Rules
**Method:** `GET`  
**Endpoint:** `/api/rules`  
**Request Body:** (None)

---

### 2. Add a New Rule (Recommended: Using Condition and Action)
**Method:** `POST`  
**Endpoint:** `/api/rules`  
**Request Body:** (JSON)
```json
{
  "name": "GoldCustomerDiscount",
  "description": "Applies a 20% discount for GOLD customers",
  "condition": "this[\"customerType\"] == \"GOLD\"",
  "action": "output.put(\"discount\", 20); output.put(\"status\", \"GOLD_OFFER_APPLIED\");",
  "createdBy": "admin"
}
```
*   **Note:** The backend will automatically wrap these into a valid Drools rule.

---

### 3. Add a New Rule (Legacy: Using Full DRL String)
**Method:** `POST`  
**Endpoint:** `/api/rules`  
**Request Body:** (JSON)
```json
{
  "name": "VIPRuleFullDRL",
  "description": "Rule added with full DRL syntax",
  "drl": "package rules;\nimport java.util.Map;\nglobal java.util.Map output;\nrule \"VIP Rule\"\nwhen\n    $input : Map(this[\"orderValue\"] > 5000)\nthen\n    output.put(\"vip_status\", \"Diamond\");\nend",
  "createdBy": "admin"
}
```

---

### 4. Update an Existing Rule
**Method:** `POST` (Same as Add; JPA uses ID to differentiate)  
**Endpoint:** `/api/rules`  
**Request Body:** (JSON)
```json
{
  "id": 1,
  "name": "UpdatedMemberDiscount",
  "condition": "this[\"customerType\"] == \"MEMBER\"",
  "action": "output.put(\"discount\", 10);",
  "modifiedBy": "manager"
}
```

---

### 5. Execute Rules (Test Logic)
**Method:** `POST`  
**Endpoint:** `/api/rules/execute`  
**Request Body:** (JSON - Map of inputs)
```json
{
  "customerType": "GOLD",
  "orderValue": 6000,
  "items": 3
}
```
**Expected Response (based on rule in step 2):**
```json
{
  "discount": 20,
  "status": "GOLD_OFFER_APPLIED"
}
```

---

### 6. Manually Reload Rules
**Method:** `POST`  
**Endpoint:** `/api/rules/reload`  
**Request Body:** (None)  
**Description:** Use this if you've modified rules directly in the database and want the engine to pick them up immediately.

---

### 7. Delete a Rule
**Method:** `DELETE`  
**Endpoint:** `/api/rules/{id}`  
**Example:** `DELETE /api/rules/1`  
**Request Body:** (None)

### Summary Table for Testing Tools (like Postman/Insomnia)

| Action | HTTP Method | URL | Body Type |
| :--- | :--- | :--- | :--- |
| **Get Rules** | `GET` | `http://localhost:8080/api/rules` | N/A |
| **Add Rule** | `POST` | `http://localhost:8080/api/rules` | JSON |
| **Execute Rules** | `POST` | `http://localhost:8080/api/rules/execute` | JSON |
| **Reload** | `POST` | `http://localhost:8080/api/rules/reload` | N/A |
| **Delete Rule** | `DELETE` | `http://localhost:8080/api/rules/1` | N/A |