# CronMaker - Custom DateTime to cron expression - java

CUSTOM QUARTZ CRON EXPRESSION GENERATION

This document explains how the CronMaker class generates Quartz cron expressions. It covers different recurrence types and their configurations.

Quartz Cron Expression Format:
<second> <minute> <hour> <day of the month> <month> <day of the week> <year>

See the Quartz Cron Syntax Documentation: https://productresources.collibra.com/docs/collibra/latest/Content/Cron/co_quartz-cron-syntax.htm

RECURRENCE TYPES AND EXAMPLES:

Examples show inputs based on recurrence type and customRecurrence object.

Valid Values for 'recurrence':
- NO_REPEAT ("does not repeat")
- WEEKDAY ("every week day (mon - fri)")
- DAILY ("daily")
- WEEKLY ("weekly")
- MONTHLY ("monthly")
- YEARLY ("yearly")
- WEEKEND ("weekend")
- CUSTOM ("custom")

Weekday Mapping (Quartz uses Sunday=1 to Saturday=7):

monday -> 2
tuesday -> 3
wednesday -> 4
thursday -> 5
friday -> 6
saturday -> 7
sunday -> 1

EXAMPLES BY RECURRENCE TYPE:

1. Do Not Repeat (NO_REPEAT):

Input:
{
  "startDate": "2025-02-12",
  "startTime": "10:08",
  "recurrence": "does not repeat"
}

Explanation: Occurs once on 2025-02-12 at 10:08.

Cron Expression: 0 8 10 12 2 ? 2025

2. Daily (DAILY):

Input:
{
  "startDate": "2025-02-12",
  "startTime": "10:08",
  "recurrence": "daily"
}

Explanation: Repeats every day at 10:08 from 2025-02-12.

Cron Expression: 0 8 10 * * ? *

3. Weekdays (Mon-Fri) (WEEKDAY):

Input:
{
  "startDate": "2025-02-12",
  "startTime": "10:08",
  "recurrence": "every week day (mon - fri)"
}

Explanation: Repeats every weekday (Monday to Friday) at 10:08 from 2025-02-12.

Cron Expression: 0 8 10 ? * 2-6 *

4. Weekly (WEEKLY):

Input:
{
  "startDate": "2025-02-12",
  "startTime": "10:08",
  "recurrence": "weekly"
}

Explanation: Repeats every week on the same day as 2025-02-12 (Wednesday) at 10:08.

Cron Expression: 0 8 10 ? * 4 *

5. Monthly (MONTHLY):

Input:
{
  "startDate": "2025-02-12",
  "startTime": "10:08",
  "recurrence": "monthly"
}

Explanation: Repeats every month on the 12th at 10:08 from 2025-02-12.

Cron Expression: 0 8 10 12 * ? *

6. Yearly (YEARLY):

Input:
{
  "startDate": "2025-02-12",
  "startTime": "10:08",
  "recurrence": "yearly"
}

Explanation: Repeats every year on February 12 at 10:08 from 2025-02-12.

Cron Expression: 0 8 10 12 2 ? *

7. Weekends (Sat, Sun) (WEEKEND):

Input:
{
  "startDate": "2025-02-12",
  "startTime": "10:08",
  "recurrence": "weekend"
}

Explanation: Repeats every weekend at 10:08 from 2025-02-12.

Cron Expression: 0 8 10 ? * 1,7 *

8. Custom Recurrence (CUSTOM):

For custom recurrence, customRecurrence object is used.

a. Custom Weekly Recurrence:

Input:
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

Explanation: Repeats every 2 weeks on Monday and Saturday at 10:08 from 2025-02-12. Frequency is handled in executor.

Cron Expression: 0 8 10 ? * 2,7 *

b. Custom Monthly Recurrence:

Input:
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

Explanation: Repeats every 4 months on the 12th at 10:08 from 2025-02-12.

Cron Expression: 0 8 10 12 */4 ? *

(Note: "*/4" triggers every month; skipping months is handled in executor code.)

c. Custom Yearly Recurrence:

Input:
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

Explanation: Repeats every 4 years on February 12 at 10:08 from 2025-02-12.

Cron Expression: 0 8 10 12 2 ? */4

d. Custom Hourly Recurrence:

Input:
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

Explanation: Repeats every 4 hours, starting from 2025-02-12 at 10:08 AM.

Cron Expression: 0 8 */4 * * ? *

e. Custom Daily Recurrence:

Input:
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

Explanation: Repeats every 4 days, starting from 2025-02-12.

Cron Expression: 0 8 10 */4 * ? *

f. Custom Minute Recurrence:

Input:
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

Explanation: Repeats every 15 minutes, starting from 2025-02-12 at 10:08 AM.

Cron Expression: 0 */15 10 * * ? *

EXECUTOR CODE SNIPPET (FOR CUSTOM RECURRENCES):

Custom recurrences use Quartz cron for triggering at regular intervals, but skipping logic is in executor code:

{
            JobDetail jobDetail = context.getJobDetail();
            JobDataMap jobDataMap = jobDetail.getJobDataMap();
            String jobId = jobDataMap.getString("jobId");
            String startDate = jobDataMap.getString("startDate");
            String startTime = jobDataMap.getString("startTime");
            LocalDate date = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String minutes = startTime.substring(3, 5);
            String hours = startTime.substring(0, 2);
            LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.of(Integer.parseInt(hours), Integer.parseInt(minutes)));
            LocalDateTime now = LocalDateTime.now();
 
           if (now.isBefore(dateTime)) {
               System.out.println("Current time is too early, current time: " + now + ", start time: " + dateTime);
               return;
          }
}

This code ensures that the action is only executed if the current time is after the start time.

INPUT EXAMPLES SUMMARY

Recurrence Type	Example Input
Do Not Repeat	{"startDate": "2025-02-12", "startTime": "10:08", "recurrence": "does not repeat"}
Daily	{"startDate": "2025-02-12", "startTime": "10:08", "recurrence": "daily"}
Weekdays	{"startDate": "2025-02-12", "startTime": "10:08", "recurrence": "every week day (mon - fri)"}
Weekly	{"startDate": "2025-02-12", "startTime": "10:08", "recurrence": "weekly"}
Monthly	{"startDate": "2025-02-12", "startTime": "10:08", "recurrence": "monthly"}
Yearly	{"startDate": "2025-02-12", "startTime": "10:08", "recurrence": "yearly"}
Weekends	{"startDate": "2025-02-12", "startTime": "10:08", "recurrence": "weekend"}
Custom Weekly	{"startDate": "2025-02-12", "startTime": "10:08", "recurrence": "custom", "customRecurrence": {"startDate": "2025-02-12", "repeatEvery": 2, "unit": "weekly", "selectedDays": ["Monday", "Saturday"]}}
Custom Monthly	{"startDate": "2025-02-12", "startTime": "10:08", "recurrence": "custom", "customRecurrence": {"startDate": "2025-02-12", "repeatEvery": 3, "unit": "monthly"}}
Custom Yearly	{"startDate": "2025-02-12", "startTime": "10:08", "recurrence": "custom", "customRecurrence": {"startDate": "2025-02-12", "repeatEvery": 2, "unit": "yearly"}}
Custom Hourly	{"startDate": "2025-02-12", "startTime": "10:08", "recurrence": "custom", "customRecurrence": {"startDate": "2025-02-12", "repeatEvery": 4, "unit": "hour"}}
Custom Daily	{"startDate": "2025-02-12", "startTime": "10:08", "recurrence": "custom", "customRecurrence": {"startDate": "2025-02-12", "repeatEvery": 4, "unit": "daily"}}
Custom Minute	{"startDate": "2025-02-12", "startTime": "10:08", "recurrence": "custom", "customRecurrence": {"startDate": "2025-02-12", "repeatEvery": 15, "unit": "minutes"}}

CRON EXPRESSION BREAKDOWN EXAMPLE

For example, the cron expression 0 8 10 ? * 2,6 * is:

0: Seconds (00)

8: Minutes (08)

10: Hours (10)

?: Day of the month (no specific day)

*: Month (every month)

2,6: Day of the week (Monday and Friday)

*: Year (every year)
