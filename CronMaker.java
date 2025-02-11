package com.action_service.Service;

import com.action_service.Entites.JobConfigurations;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Converts a {@code JobConfigurations} object into a cron expression.
 *
 * <p>The generated cron expression follows the format:</p>
 * {@code <second> <minute> <hour> <day of the month> <month> <day of the week> <year>}
 *
 * <h3>JobConfigurations Object:</h3>
 * <p>Example for a custom configuration:</p>
 * <pre>
 * {@code {
 *     "startDate": "2025-02-12",
 *     "startTime": "10:08",
 *     "recurrence": "custom",
 *     "customRecurrence": {
 *           "repeatEvery": 2,
 *           "unit": "WEEKLY",
 *           "selectedDays": ["MONDAY", "TUESDAY", "WEDNESDAY", "FRIDAY"]
 *       }
 * }
 * }
 * </pre>
 *
 * <p>Example for a predefined recurrence:</p>
 * <pre>
 *
 * {@code {
 *     "startDate": "2025-02-12",
 *     "startTime": "10:08",
 *     "recurrence": "Does Not Repeat"
 * }
 * }
 *
 * </pre>
 *
 * <h3>Supported Recurrence Types (Case-Insensitive):</h3>
 * <ul>
 *   <li>"Does Not Repeat"</li>
 *   <li>"Every Weekday (Mon - Fri)"</li>
 *   <li>"Daily"</li>
 *   <li>"Weekly"</li>
 *   <li>"Monthly"</li>
 *   <li>"Yearly"</li>
 *   <li>"Weekend"</li>
 *   <li>"Hour"</li>
 *   <li>"Minute"</li>
 *   <li>"Custom"</li>
 * </ul>
 *
 * <h3>Supported Units for Custom Recurrence:</h3>
 * <ul>
 *   <li>"weekly"</li>
 *   <li>"hour"</li>
 *   <li>"daily"</li>
 *   <li>"monthly"</li>
 *   <li>"yearly"</li>
 * </ul>
 *
 * <h3>Handling Start Date and Time:</h3>
 * <p>The start date and time from the {@code JobConfigurations} object are stored in a {@code JobDataMap}.
 * This ensures that job execution does not occur before the specified start time.</p>
 *
 * <p>Example of storing job metadata:</p>
 * <pre>
 * {@code
 *   JobDataMap jobDataMap = new JobDataMap();
 *   jobDataMap.put("jobId", jobId);
 *   jobDataMap.put("startDate", config.getStartDate());
 *   jobDataMap.put("startTime", config.getStartTime());
 *
 *   if (config.getRecurrence().equalsIgnoreCase("custom") &&
 *   config.getCustomRecurrence().getUnit().equalsIgnoreCase("WEEKLY")) {
 *   jobDataMap.put("frequency", String.valueOf(config.getCustomRecurrence().getRepeatEvery()));
 *   jobDataMap.put("count", "-1");
 *   jobDataMap.put("numOfDays", String.valueOf(config.getCustomRecurrence().getSelectedDays().size()));
 *   jobDataMap.put("actualCount", "-1");
 *   }
 * }
 * </pre>
 *
 * <h3>Job Scheduling:</h3>
 * <p>The job is built with the stored metadata to control execution timing:</p>
 * <pre>
 *
 * {@code
 *     JobDetail jobDetail = JobBuilder.newJob(JobExecutioner.class)
 *           .withIdentity(config.getJobName(), config.getJobGroup())
 *           .withDescription(config.getJobDesc())
 *           .usingJobData(jobDataMap)
 *           .build();
 * }
 * </pre>
 *
 * <h3>Job Execution Logic:</h3>
 * <p>Before execution, the system ensures that the current time is after the start date and time.</p>
 *
 * <pre>
 * {@code
 *           JobDetail jobDetail = context.getJobDetail();
 *           JobDataMap jobDataMap = jobDetail.getJobDataMap();
 *           String jobId = jobDataMap.getString("jobId");
 *           String startDate = jobDataMap.getString("startDate");
 *           String startTime = jobDataMap.getString("startTime");
 *           LocalDate date = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
 *           String minutes = startTime.substring(3, 5);
 *           String hours = startTime.substring(0, 2);
 *           LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.of(Integer.parseInt(hours), Integer.parseInt(minutes)));
 *           LocalDateTime now = LocalDateTime.now();
 *
 *          if (now.isBefore(dateTime)) {
 *              System.out.println("Current time is too early, current time: " + now + ", start time: " + dateTime);
 *              return;
 *          }
 *}
 * </pre>
 *
 * <h3>Handling Weekly Custom Recurrence:</h3>
 * <p>The execution logic ensures jobs run only on selected days and at the correct frequency:</p>
 * <pre>
 * {@code synchronized (this) {
 *     if (jobDataMap.getString("frequency") != null) {
 *         int frequency = Integer.parseInt(jobDataMap.getString("frequency"));
 *         int count = Integer.parseInt(jobDataMap.getString("count"));
 *         int numOfDays = Integer.parseInt(jobDataMap.getString("numOfDays"));
 *         int actualCount = Integer.parseInt(jobDataMap.getString("actualCount"));
 *
 *         if (count == numOfDays) {
 *             actualCount += 1;
 *             count = 0;
 *         }
 *         count += 1;
 *         if (actualCount % frequency != 0) {
 *             jobDataMap.put("actualCount", String.valueOf(actualCount));
 *             jobDataMap.put("count", String.valueOf(count));
 *             return;
 *         }
 *         jobDataMap.put("count", String.valueOf(count));
 *         jobDataMap.put("actualCount", String.valueOf(actualCount));
 *     }
 * }}
 * </pre>
 */

public class CronMaker {

    public enum Recurrence {
        NO_REPEAT("does not repeat"),
        WEEKDAY("every week day (mon - fri)"),
        DAILY("daily"),
        WEEKLY("weekly"),
        MONTHLY("monthly"),
        YEARLY("yearly"),
        WEEKEND("weekend"),
        HOUR("hour"),
        MINUTES("minutes"),
        CUSTOM("custom");

        private final String value;

        Recurrence(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Recurrence fromString(String value) {
            for (Recurrence recurrence : values()) {
                if (recurrence.getValue().equals(value)) {
                    return recurrence;
                }
            }
            throw new IllegalArgumentException("Invalid recurrence value. Valid values are: " + Arrays.toString(Recurrence.values()));
        }
    }

    //not used in this version but might be useful later
    public enum Weekdays {
        MONDAY("monday"),
        TUESDAY("tuesday"),
        WEDNESDAY("wednesday"),
        THURSDAY("thursday"),
        FRIDAY("friday"),
        SATURDAY("saturday"),
        SUNDAY("sunday");

        private final String value;

        Weekdays(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Weekdays fromString(String value) {
            for (Weekdays days : values()) {
                if (days.getValue().equals(value)) {
                    return days;
                }
            }
            return null;
        }
    }

    public static String createCron(JobConfigurations configurations) {
        LocalDate startDate;
        try {
            startDate = LocalDate.parse(configurations.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid startDate format. Expected format is yyyy-MM-dd.");
        }

        String startTime = configurations.getStartTime();
        if (startTime == null || startTime.length() != 5 || startTime.charAt(2) != ':' ||
                !Character.isDigit(startTime.charAt(0)) || !Character.isDigit(startTime.charAt(1)) ||
                !Character.isDigit(startTime.charAt(3)) || !Character.isDigit(startTime.charAt(4))) {
            throw new IllegalArgumentException("Invalid startTime format. Expected format is HH:mm.");
        }

        String minutes = startTime.substring(3, 5);
        String hours = startTime.substring(0, 2);
        String day = startDate.getDayOfMonth() < 10 ? "0" + startDate.getDayOfMonth() : String.valueOf(startDate.getDayOfMonth());
        String month = startDate.getMonthValue() < 10 ? "0" + startDate.getMonthValue() : String.valueOf(startDate.getMonthValue());
        String year = String.valueOf(startDate.getYear());
        int dayOfWeek = startDate.getDayOfWeek().getValue();
        String cronDayOfWeek = (dayOfWeek == 7) ? "1" : String.valueOf(dayOfWeek + 1);

        String recurrenceStr = configurations.getRecurrence().toLowerCase();
        Recurrence recurrence = Recurrence.fromString(recurrenceStr);

        return switch (recurrence) {
            case NO_REPEAT -> makeCron("0", minutes, hours, day, month, "?", year);
            case WEEKDAY -> makeCron("0", minutes, hours, "?", "*", "2-6", "*");
            case DAILY -> makeCron("0", minutes, hours, "*", "*", "?", "*");
            case WEEKLY -> makeCron("0", minutes, hours, "?", "*", cronDayOfWeek, "*");
            case MONTHLY -> makeCron("0", minutes, hours, day, "*", "?", "*");
            case YEARLY -> makeCron("0", minutes, hours, day, month, "?", "*");
            case WEEKEND -> makeCron("0", minutes, hours, "?", "*", "1,7", "*");
            case CUSTOM -> customCron(configurations);
            default ->
                    throw new IllegalArgumentException("Cannot provide cron expression for recurrence: " + recurrence);
        };
    }

    private static String customCron(JobConfigurations configurations) {
        LocalDate startDate;
        try {
            startDate = LocalDate.parse(configurations.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid startDate format. Expected format is yyyy-MM-dd.");
        }

        String startTime = configurations.getStartTime();
        if (startTime == null || startTime.length() != 5 || startTime.charAt(2) != ':' ||
                !Character.isDigit(startTime.charAt(0)) || !Character.isDigit(startTime.charAt(1)) ||
                !Character.isDigit(startTime.charAt(3)) || !Character.isDigit(startTime.charAt(4))) {
            throw new IllegalArgumentException("Invalid startTime format. Expected format is HH:mm.");
        }

        String minutes = startTime.substring(3, 5);
        String hours = startTime.substring(0, 2);
        String day = startDate.getDayOfMonth() < 10 ? "0" + startDate.getDayOfMonth() : String.valueOf(startDate.getDayOfMonth());
        String month = startDate.getMonthValue() < 10 ? "0" + startDate.getMonthValue() : String.valueOf(startDate.getMonthValue());
        String frequency = String.valueOf(configurations.getCustomRecurrence().getRepeatEvery());

        String recurrenceStr = configurations.getCustomRecurrence().getUnit().toLowerCase();
        Recurrence recurrence = Recurrence.fromString(recurrenceStr);//Weekly,Monthly,Yearly,hour,daily
        List<String> selecteDaysList = new ArrayList<>();
        if (recurrence.getValue().equalsIgnoreCase("Weekly")) {
            selecteDaysList = getStrings(configurations.getCustomRecurrence().getSelectedDays());
        }
        String selectedDays = String.join(",", selecteDaysList);

        return switch (recurrence) {
            //different case for "weekly" only handle at execution lvl
            case WEEKLY -> makeCron("0", minutes, hours, "?", "*", selectedDays, "*");

            case MINUTES -> makeCron("0", "*/"+frequency,  "*", "?", "*", "*", "*");
            case HOUR -> makeCron("0", minutes,  "*/" + frequency, "?", "*", "*", "*");
            case DAILY -> makeCron("0", minutes, hours,"*/" + frequency, "*", "*", "*");
            case MONTHLY -> makeCron("0", minutes, hours, day, "*/" + frequency, "?", "*");
            case YEARLY -> makeCron("0", minutes, hours, day, month, "?", "*/" + frequency);
            default ->
                    throw new IllegalArgumentException("Cannot provide cron expression for recurrence: " + recurrence);
        };
    }

    // <second> <minute> <hour> <day of the month> <month> <day of the week> <year>
    //https://productresources.collibra.com/docs/collibra/latest/Content/Cron/co_quartz-cron-syntax.htm
    private static String makeCron(String second, String minute, String hour, String dayOfMonth, String month, String dayOfWeek, String year) {
        return second + " " + minute + " " + hour + " " + dayOfMonth + " " + month + " " + dayOfWeek + " " + year;
    }

    private static List<String> getStrings(List<String> list) {
        Map<String, String> map = new HashMap<>();
        map.put("monday", "2");
        map.put("tuesday", "3");
        map.put("wednesday", "4");
        map.put("thursday", "5");
        map.put("friday", "6");
        map.put("saturday", "7");
        map.put("sunday", "1");

        List<String> selecteDaysList = new ArrayList<>();
        for (String day : list) {
            selecteDaysList.add(map.get(day.toLowerCase()));
        }
        return selecteDaysList;
    }
}
