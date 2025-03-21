QUARTZ CRON EXPRESSION GENERATOR
=====================================

DESCRIPTION
-----------
This document explains how to generate Quartz cron expressions for different types 
of scheduling requirements. It provides comprehensive examples and explanations 
for various recurrence patterns.

CRON EXPRESSION FORMAT
---------------------
Quartz cron expressions follow this format:
```
<second> <minute> <hour> <day of the month> <month> <day of the week> <year>
```
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
```
Sunday    = 1
Monday    = 2
Tuesday   = 3
Wednesday = 4
Thursday  = 5
Friday    = 6
Saturday  = 7
```
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

2. Daily:
Input:
```
{
    "startDate": "2025-02-12",
    "startTime": "10:08",
    "recurrence": "daily"
}
```
Cron: 0 8 10 * * ? *

3. Weekdays:
Input:
```
{
    "startDate": "2025-02-12",
    "startTime": "10:08",
    "recurrence": "every week day (mon - fri)"
}
```
Cron: 0 8 10 ? * 2-6 *

4. Weekly:
Input:
```
{
    "startDate": "2025-02-12",
    "startTime": "10:08",
    "recurrence": "weekly"
}
```
Cron: 0 8 10 ? * 4 *

5. Monthly:
Input:
```
{
    "startDate": "2025-02-12",
    "startTime": "10:08",
    "recurrence": "monthly"
}
```
Cron: 0 8 10 12 * ? *

6. Yearly:
Input:
```
{
    "startDate": "2025-02-12",
    "startTime": "10:08",
    "recurrence": "yearly"
}
```
Cron: 0 8 10 12 2 ? *

7. Weekend:
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

2. Custom Monthly:
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

3. Custom Yearly:
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

4. Custom Hourly:
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

5. Custom Daily:
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

6. Custom Minutes:
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

**EXECUTOR CODE SNIPPET (FOR CUSTOM RECURRENCES):**
------------------------------------------------

Custom recurrences use Quartz cron for triggering at regular intervals, but skipping logic is in executor code:
````
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
````
This code ensures that the action is only executed if the current time is after the start date and time.

       //handling the weekly case with custom frequency
        synchronized (this) {
            if (jobDataMap.getString("frequency") != null) {
                int frequency = Integer.parseInt(jobDataMap.getString("frequency"));
                int count = Integer.parseInt(jobDataMap.getString("count"));
                int numOfDays = Integer.parseInt(jobDataMap.getString("numOfDays"));
                int actualCount = Integer.parseInt(jobDataMap.getString("actualCount"));
                //count=0, actualCount=0 in start
                if (count == numOfDays) {
                    actualCount += 1;
                    count = 0;
                }
                count += 1;
                if (actualCount % frequency != 0) {
                    jobDataMap.put("actualCount", String.valueOf(actualCount));
                    jobDataMap.put("count", String.valueOf(count));
                    return;
                }
                jobDataMap.put("count", String.valueOf(count));
                jobDataMap.put("actualCount", String.valueOf(actualCount));
            }
        }
        
update the job context with updated jobDataMAp
        
        try {
            Scheduler scheduler = context.getScheduler();
            JobKey jobKey = context.getJobDetail().getKey();

            JobDetail newJobDetail = JobBuilder.newJob(JobExecutioner.class)
                    .withIdentity(jobKey)
                    .usingJobData(jobDataMap)
                    .storeDurably()
                    .withDescription(context.getJobDetail().getDescription())
                    .build();

            scheduler.addJob(newJobDetail, true);//updating existing job only
            log.info("Successfully updated job data for job: {}", jobKey.getName());
            } catch (Exception e) {
            log.error("Failed to update job data: {}", e.getMessage(), e);
            }

This logic handles the only case of triggering when there are some days of week are selected to repeat after a set frequency.

UNDERSTANDING CRON EXPRESSIONS
----------------------------
Sample cron expression breakdown: 0 8 10 ? * 2,6 *

* 0   : Seconds (00)
* 8   : Minutes (08)
* 10  : Hours (10)
* ?   : Day of the month (no specific day)
* "*" : Month (every month)
* 2,6 : Day of the week (Monday and Friday)
* "*" : Year (every year)

IMPLEMENTATION NOTE
-----------------
For custom recurrences, the Quartz cron expression handles the basic scheduling,
while additional skip logic is implemented in the executor code. The executor 
ensures that actions are only executed after the specified start time.