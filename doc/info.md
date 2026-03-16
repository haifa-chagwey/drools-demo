Two possible ways to implement this business need
There are 2 common designs.

Option A — Different rules per tenant
Each tenant stores its own rules.
Example
Tenant A rule:``` text
if contract=true and amount<=20 => allow exit
```

Tenant B rule:``` text
if contract=true and amount<=5 => allow exit
```

Pros
very flexible
every client can fully customize behavior
Cons
more rules to manage

Option B — Same rule, different tenant configuration
You keep one generic rule, but the threshold comes from tenant config.
Example generic rule``` text
if contract=true and amount<=tenant.maxAllowedDebt => allow exit
```

Then tenant configs:
airport-parking → maxAllowedDebt = 20
mall-parking → maxAllowedDebt = 5
Pros
cleaner
fewer duplicated rules
easier maintenance
Cons
less flexible if clients need very different logic


Recommended sample business data
Here is a small realistic dataset.
 
Tenants``` sql
INSERT INTO tenants (tenant_key, name, status)
VALUES
('airport-parking', 'Airport Parking', 'ACTIVE'),
('mall-parking', 'Mall Parking', 'ACTIVE');
```


Tenant configs``` sql
INSERT INTO tenant_configs (tenant_id, config_key, config_value, value_type, status)
VALUES
(1, 'max_allowed_debt_for_exit', '20', 'NUMBER', 'ACTIVE'),
(1, 'contract_required_for_exit', 'true', 'BOOLEAN', 'ACTIVE'),

(2, 'max_allowed_debt_for_exit', '5', 'NUMBER', 'ACTIVE'),
(2, 'contract_required_for_exit', 'true', 'BOOLEAN', 'ACTIVE');
```

 
Rule sets``` sql
INSERT INTO rule_sets (tenant_id, rule_set_key, name, description, status, version)
VALUES
(1, 'exit-authorization', 'Exit Authorization', 'Exit logic for airport parking', 'ACTIVE', 1),
(2, 'exit-authorization', 'Exit Authorization', 'Exit logic for mall parking', 'ACTIVE', 1);
```


Rules``` sql
INSERT INTO rules (
rule_set_id,
name,
description,
priority,
rule_type,
condition_expression,
action_expression,
status
)
VALUES
(
1,
'allow_exit_small_debt',
'Allow exit for small debt',
1,
'CONDITION_ACTION',
'hasContract == true && unpaidAmount <= tenantConfig.max_allowed_debt_for_exit',
'output.put("allowExit", true); output.put("reason", "Allowed");',
'ACTIVE'
),
(
2,
'allow_exit_small_debt',
'Allow exit for small debt',
1,
'CONDITION_ACTION',
'hasContract == true && unpaidAmount <= tenantConfig.max_allowed_debt_for_exit',
'output.put("allowExit", true); output.put("reason", "Allowed");',
'ACTIVE'
);
```

Notice:
same rule name can exist in different tenants because they belong to different rulesets
config value changes behavior
Very elegant. Database does the heavy lifting without drama.
