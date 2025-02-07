# CronMaker-DateTime_to_cron_expression-java
This utility transforms date and time specifications into Quartz cron expressions, enabling precise and straightforward scheduling of background jobs.

CUSTOM QUARTZ CRON EXPLANATIONS:

This document provides a comprehensive explanation of how the CronMaker class generates Quartz cron expressions. It includes a detailed breakdown of different recurrence types and their corresponding configurations.

Format: <second> <minute> <hour> <day of the month> <month> <day of the week> <year>

LINK: https://productresources.collibra.com/docs/collibra/latest/Content/Cron/co_quartz-cron-syntax.htm

Examples of different combinations of inputs based on the recurrence type (recurrence) and the customRecurrence object (if applicable). Each example corresponds to a specific recurrence pattern.

Values for the recurrence:
   - NO_REPEAT("does not repeat")
   - WEEKDAY("every week day (mon - fri)")
   - DAILY("daily")
   - WEEKLY("weekly")
   - MONTHLY("monthly")
   - YEARLY("yearly")
   - WEEKEND("weekend")
   - CUSTOM("custom")

Values for weekdays starts from sunday=1 to sat=7 in quartz cron. The following mapping is used:

Map<String, String> map = new HashMap<>();
map.put("monday", "2");
map.put("tuesday", "3");
map.put("wednesday", "4");
map.put("thursday", "5");
map.put("friday", "6");
map.put("saturday", "7");
map.put("sunday", "1");


Examples:

Each example shows the input configuration and the resulting cron expression, along with an explanation of the schedule it represents.

1. Do Not Repeat ("0")
{
 "startDate": "2025-02-12",
 "startTime": "10:08",
 "recurrence": "does not repeat"
}
Explanation: The event occurs only once on 2025-02-12 at 10:08. No repetition.

-> 0 8 10 12 2 ? 2025 (Correct)


2. Daily ("1")
{
 "startDate": "2025-02-12",
 "startTime": "10:08",
 "recurrence": "daily"
}
Explanation: The event repeats every day at 10:08, starting from 2025-02-12.

-> 0 8 10 * * ? * (Correct)


3. Weekdays (Mon-Fri) ("5")
{
 "startDate": "2025-02-12",
 "startTime": "10:08",
 "recurrence": "every week day (mon - fri)"
}
Explanation: The event repeats every weekday (Monday to Friday) at 10:08, starting from 2025-02-12.

-> 0 8 10 ? * 2-6 * (Correct)


4. Weekly ("7")
{
 "startDate": "2025-02-12",
 "startTime": "10:08",
 "recurrence": "weekly"
}
Explanation: The event repeats every week on the same day as the start date (2025-02-12 is a Wednesday) at 10:08.

-> 0 8 10 ? * 4 * (Correct)


5. Monthly ("30")
{
 "startDate": "2025-02-12",
 "startTime": "10:08",
 "recurrence": "monthly"
}
Explanation: The event repeats every month on the 12th day at 10:08, starting from 2025-02-12.

-> 0 8 10 12 * ? * (Correct)


6. Yearly ("12")
{
 "startDate": "2025-02-12",
 "startTime": "10:08",
 "recurrence": "yearly"
}
Explanation: The event repeats every year on February 12 at 10:08, starting from 2025-02-12.

-> 0 8 10 12 2 ? * (Correct)


7. Weekends (Sat, Sun)
{
 "startDate": "2025-02-12",
 "startTime": "10:08",
 "recurrence": "weekend"
}
Explanation: The event repeats every weekend at 10:08, starting from 2025-02-12.

-> 0 8 10 ? * 1,7 * (Correct)


8. Custom Recurrence ("Custom")
For custom recurrence, the customRecurrence object is populated. Here are examples for each possible unit value (week, month, year):

 a. Custom Weekly Recurrence
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
Explanation: The event repeats every 2 weeks on Monday and Saturday at 10:08, starting from 2025-02-12. The frequency is handled in the executor using 4 variables(count, frequency, actualCount, numOfDays), set while scheduling job in job.

-> 0 8 10 ? * 2,7 * (Correct)
-> Works


 b. Custom Monthly Recurrence
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
Explanation: The event repeats every 4 months on the 12th day of the month at 10:08, starting from 2025-02-12.

-> 0 8 10 12 */4 ? * (Correct)
-> Works


 c. Custom Yearly Recurrence
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
Explanation: The event repeats every 4 years on February 12 at 10:08, starting from 2025-02-12.

-> 0 8 10 12 2 ? */4 (Correct)
-> Works


 d. Custom Hourly Recurrence
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
Explanation: The event repeats every 4 hours, starting from 2025-02-12, 10:08.

-> 0 8 10/4 * * ? * (Correct)
-> Works


 e. Custom Daily Recurrence
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
Explanation: The event repeats every 4 days, starting from 2025-02-12.

-> 0 8 10 12/4 * ? * (Correct)
-> Works


Summary of Input Examples

The following table provides a concise summary of the recurrence types and example input configurations.

| Recurrence Type  | Example Input
|------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Do Not Repeat    | { "startDate": "2025-02-12", "startTime": "10:08", "recurrence": "does not repeat" }
| Daily            | { "startDate": "2025-02-12", "startTime": "10:08", "recurrence": "daily" }
| Weekdays         | { "startDate": "2025-02-12", "startTime": "10:08", "recurrence": "every week day (mon - fri)" }
| Weekly           | { "startDate": "2025-02-12", "startTime": "10:08", "recurrence": "weekly" }
| Monthly          | { "startDate": "2025-02-12", "startTime": "10:08", "recurrence": "monthly" }
| Yearly           | { "startDate": "2025-02-12", "startTime": "10:08", "recurrence": "yearly" }
| Weekends         | { "startDate": "2025-02-12", "startTime": "10:08", "recurrence": "weekend" }
| Custom Weekly    | { "startDate": "2025-02-12", "startTime": "10:08", "recurrence": "custom", "customRecurrence": { "startDate": "2025-02-12", "repeatEvery": 2, "unit": "weekly", "selectedDays": ["Monday", "Saturday"] } }
| Custom Monthly   | { "startDate": "2025-02-12", "startTime": "10:08", "recurrence": "custom", "customRecurrence": { "startDate": "2025-02-12", "repeatEvery": 3, "unit": "monthly"} }
| Custom Yearly    | { "startDate": "2025-02-12", "startTime": "10:08", "recurrence": "custom", "customRecurrence": { "startDate": "2025-02-12", "repeatEvery": 2, "unit": "yearly"} }


Example Cron Expression Breakdown

For example, the cron expression 0 8 10 ? * 2,6 * is broken down as follows:

- 0: Seconds (00)
- 8: Minutes (08)
- 10: Hours (10)
- ?: Day of the month (no specific day)
- *: Month (every month)
- 2,6: Day of the week (Monday and Friday)
- *: Year (every year)
