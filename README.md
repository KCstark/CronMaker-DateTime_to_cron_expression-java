# CronMaker - Custom DateTime to cron expression - java

CUSTOM QUARTZ CRON EXPRESSION GENERATOR
=====================================

DESCRIPTION
-----------
This document explains how to generate Quartz cron expressions for different types 
of scheduling requirements. It provides comprehensive examples and explanations 
for various recurrence patterns.
Coming soon: an update with a new feature for range and end time supported cron.

CRON EXPRESSION FORMAT
---------------------
Quartz cron expressions follow this format:
<second> <minute> <hour> <day of the month> <month> <day of the week> <year>

RECURRENCE TYPES
---------------
The following recurrence types are supported:
* NO_REPEAT  - Does not repeat
* WEEKDAY    - Every week day (Monday - Friday)
* DAILY      - Daily
* WEEKLY     - Weekly
* MONTHLY    - Monthly
* YEARLY     - Yearly
* WEEKEND    - Weekend (Saturday, Sunday)
* CUSTOM     - Custom recurrence pattern

DAY OF WEEK MAPPING
------------------
Quartz uses numbers 1-7 for days of the week:
Sunday    = 1
Monday    = 2
Tuesday   = 3
Wednesday = 4
Thursday  = 5
Friday    = 6
Saturday  = 7

EXAMPLES
--------

Simple Recurrence Examples:
-------------------------

1. No Repeat:
Input:
```
{
    "startDate": "2025-02-12",
    "startTime": "10:08",
    "recurrence": "does not repeat"
}
```
Cron: 0 8 10 12 2 ? 2025

3. Daily:
Input:
```
{
    "startDate": "2025-02-12",
    "startTime": "10:08",
    "recurrence": "daily"
}
```
Cron: 0 8 10 * * ? *

5. Weekdays:
Input:
```
{
    "startDate": "2025-02-12",
    "startTime": "10:08",
    "recurrence": "every week day (mon - fri)"
}
```
Cron: 0 8 10 ? * 2-6 *

7. Weekly:
Input:
```
{
    "startDate": "2025-02-12",
    "startTime": "10:08",
    "recurrence": "weekly"
}
```
Cron: 0 8 10 ? * 4 *

9. Monthly:
Input:
```
{
    "startDate": "2025-02-12",
    "startTime": "10:08",
    "recurrence": "monthly"
}
```
Cron: 0 8 10 12 * ? *

11. Yearly:
Input:
```
{
    "startDate": "2025-02-12",
    "startTime": "10:08",
    "recurrence": "yearly"
}
```
Cron: 0 8 10 12 2 ? *

13. Weekend:
Input:
```
{
    "startDate": "2025-02-12",
    "startTime": "10:08",
    "recurrence": "weekend"
}
```
Cron: 0 8 10 ? * 1,7 *

Custom Recurrence Examples:
-------------------------

IMPLEMENTATION NOTE
-----------------
For custom recurrences, the Quartz cron expression handles the basic scheduling,
while additional skip logic is implemented in the executor code. 
e.g. "0 8 10 12 */4 ? *", "*/4" just means step size 4 after the current execution's month and so on.
The executor ensures that actions are only executed after the specified start time, then quartz continues
the step increment logic. That code is avaiable as example snipets in CronMaker Class as 
javaDoc if you are interested.

1. Custom Weekly:
Input:
```
{
    "startDate": "2025-02-12",
    "startTime": "10:08",
    "recurrence": "custom",
    "customRecurrence": {
        "startDate": "2025-02-12",
        "repeatEvery": 2,
        "unit": "weekly",
        "selectedDays": ["Monday", "Saturday"]
    }
}
```
Cron: 0 8 10 ? * 2,7 *

3. Custom Monthly:
Input:
```
{
    "startDate": "2025-02-12",
    "startTime": "10:08",
    "recurrence": "custom",
    "customRecurrence": {
        "startDate": "2025-02-12",
        "repeatEvery": 4,
        "unit": "monthly"
    }
}
```
Cron: 0 8 10 12 */4 ? *

5. Custom Yearly:
Input:
```
{
    "startDate": "2025-02-12",
    "startTime": "10:08",
    "recurrence": "custom",
    "customRecurrence": {
        "startDate": "2025-02-12",
        "repeatEvery": 4,
        "unit": "yearly"
    }
}
```
Cron: 0 8 10 12 2 ? */4

7. Custom Hourly:
Input:
```
{
    "startDate": "2025-02-12",
    "startTime": "10:08",
    "recurrence": "custom",
    "customRecurrence": {
        "startDate": "2025-02-12",
        "repeatEvery": 4,
        "unit": "hour"
    }
}
```
Cron: 0 8 */4 * * ? *

9. Custom Daily:
Input:
```
{
    "startDate": "2025-02-12",
    "startTime": "10:08",
    "recurrence": "custom",
    "customRecurrence": {
        "startDate": "2025-02-12",
        "repeatEvery": 4,
        "unit": "daily"
    }
}
```
Cron: 0 8 10 */4 * ? *

11. Custom Minutes:
Input:
```
{
    "startDate": "2025-02-12",
    "startTime": "10:08",
    "recurrence": "custom",
    "customRecurrence": {
        "startDate": "2025-02-12",
        "repeatEvery": 15,
        "unit": "minutes"
    }
}
```
Cron: 0 */15 10 * * ? *

UNDERSTANDING CRON EXPRESSIONS
----------------------------
Sample cron expression breakdown: 0 8 10 ? * 2,6 *

0   : Seconds (00)
8   : Minutes (08)
10  : Hours (10)
?   : Day of the month (no specific day)
*   : Month (every month)
2,6 : Day of the week (Monday and Friday)
*   : Year (every year)
