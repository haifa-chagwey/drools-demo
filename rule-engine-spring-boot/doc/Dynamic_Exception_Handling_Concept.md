# Concept -- Dynamic Exception Handling

## 1. Concept & Target

### Goal

As a car park operator, I want to manage exceptions in a flexible way
based on definable rules, so that I can view and adapt them according to
my needs for my car parks.

### Objective

-   Define flexible workflows
-   Use rules, conditions, and actions
-   Allow adaptation without rigid logic
-   Support autonomous parking operations

------------------------------------------------------------------------

## 2. Inspiration

Inspired by smart home automation systems:

-   Amazon Alexa -- "Routine"
-   Homematic IP -- "Automation"

### Smart Home Logic Model

New Routine → Trigger → Condition → Action

------------------------------------------------------------------------

## 3. Infinite Exception Manager

A central application to manage all parking exceptions.

### Features

-   Pre-configured exception profiles
-   Transparent visualization of rules and actions
-   Customizable rules (conditions & actions)
-   Activate/deactivate routines
-   Assign multiple rules per exception
-   Change rule prioritization
-   Switch profiles based on week profile
-   Assign rules to specific car parks

### Example Profiles

-   Commercial Parking -- No Hassle (Autonomous)
-   Commercial Parking -- Balanced
-   Commercial Parking -- Protect Revenue
-   Car Access -- Secure Access

------------------------------------------------------------------------

## 4. Rule Structure

Each exception contains:

-   Exception Name
-   Device Type (Entry, Exit, APS, Door)
-   Rule Active (Yes/No)
-   Product Group / Product Name
-   Conditions (one or multiple)
-   Actions (multiple, sequential)

------------------------------------------------------------------------

## 5. Example Conditions

### Financial

-   Amount ≤ X €
-   Additional payment required
-   Percentage of exceeded paid time

### Time-Based

-   Duration ≤ hh:mm
-   Day/Time scheduler (e.g., 19:00--06:00)
-   Week profile
-   Validity period
-   Duration since last entry/exit/payment

### Contract / Product

-   Expired product
-   Invalid contract
-   Blocked / inactive
-   Not valid yet

### Location / Device

-   Car park full
-   Product full
-   Device offline
-   Intercom not working

### Matching

-   Fuzzy LPN (N-1, N-2)
-   Honest payment match
-   No payment possible

------------------------------------------------------------------------

## 6. Example Actions

-   No system action (silent)
-   System notification
-   Trigger automated intercom call
-   Open barrier
-   Book amount as lost revenue
-   Book open amount for later payment (invoice)
-   Book open amount for direct payment
-   Ignore counter blocking
-   Create virtual transaction
-   Proceed with regular handling
-   Stop customer and fire exception

------------------------------------------------------------------------

# 7. Use Cases

## Use Case 1 -- Tolerate Expired Contract Parker at Exit

Logic:

-   ≤ 2 € → Open barrier + Book as lost revenue

-   2--10 € → Open barrier + Invoice later

-   10 € → Request direct payment

------------------------------------------------------------------------

## Use Case 2 -- Tolerate Low Amount at Night

Short-term parker with unpaid amount ≤ 1 € during 19:00--06:00:

Condition: - Amount ≤ 1 € - Time between 19:00--06:00

Action: - Book as lost revenue - Open barrier

------------------------------------------------------------------------

## Use Case 3 -- Manage Wrong Presence

At entry: - Create virtual transaction - Send system notification -
Proceed with regular handling

At exit: - Create virtual entry transaction - Notify operator - Continue
normal processing

------------------------------------------------------------------------

## Use Case 4 -- Unknown LPN (Car Access)

If fuzzy match disabled or no candidate found: - Open barrier - Book
show-up product

If fuzzy candidate found: - Open barrier - Book registered entry with
original and fuzzy LPN

------------------------------------------------------------------------

## Use Case 5 -- Counter Full

If auto handling enabled and counter full: - Open barrier - Overbook
counter

Otherwise: - Stop customer - Fire exception

------------------------------------------------------------------------

## Use Case 6 -- Customer Cannot Pay

Examples: - Visitor too early - Visitor too late - Visitor at wrong
facility - Flex parker without booking

------------------------------------------------------------------------

## Strategic Direction

-   Central exception management
-   Flexible rule engine similar to smart home programming
-   Default rule sets for faster time-to-market
-   Clear differentiation between car access and commercial parking
