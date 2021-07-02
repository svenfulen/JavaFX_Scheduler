package utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class time_convert {
    /**
     * Converts a timestamp from a SQL query into a LocalDateTime with the system's default timezone.
     * @param database_time A timestamp in the format YYYY-MM-DD HH:MM:SS
     * @return LocalDateTime object
     */
    public static LocalDateTime toLocal(String database_time){
        // format for parsing
        String format = database_time.replace(" ", "T");

        // Parse the date and time from the Timestamp
        LocalDateTime appointment = LocalDateTime.parse(format);

        // ZoneId of UTC and the system default zone
        ZoneId utcZone = ZoneId.of("Etc/UTC");
        ZoneId localZone = ZoneId.systemDefault();

        // Appointment information object in UTC
        ZonedDateTime appointmentUTC = ZonedDateTime.of(appointment, utcZone);

        //convert utc time to system time zone
        ZonedDateTime appointmentZDT = appointmentUTC.withZoneSameInstant(localZone);

        // convert ZonedDateTime to LocalDateTime and return
        return appointmentZDT.toLocalDateTime();
    }

    /**
     * Convert a LocalDateTime object into a Timestamp that can be saved into the database
     * @param inLocalTime LocalTime object using the system time zone
     * @return Timestamp as a string in format yyyy-MM-dd HH:mm:ss
     */
    public static String toUTCString(LocalDateTime inLocalTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Convert the LocalDateTime into a ZonedDateTime and then to UTC
        ZonedDateTime ldtZoned = inLocalTime.atZone(ZoneId.systemDefault());
        ZonedDateTime utcZoned = ldtZoned.withZoneSameInstant(ZoneId.of("UTC"));

        // Return a string value that can be added as a timestamp in the database
        return formatter.format(utcZoned);
    }

    /**
     * Check if a start and end time for an appointment are within business hours of 8AM - 10PM EST.
     * @param startDateTime LocalDateTime, Start time in system time zone.
     * @param endDateTime LocalDateTime, End time in system time zone.
     * @return true if within business hours
     */
    public static boolean isWithinBusinessHours(LocalDateTime startDateTime, LocalDateTime endDateTime){ // p.s.  this function's code is unreadable, sorry
        ZoneId estZone = ZoneId.of("US/Eastern");

        // Set start/end appointment times to an object containing system time zone.
        ZonedDateTime startDateTime_zdt = startDateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime endDateTime_zdt = endDateTime.atZone(ZoneId.systemDefault());

        // Convert start/end times to EST time zone.
        ZonedDateTime startDateTimeZoned = startDateTime_zdt.withZoneSameInstant(estZone);
        ZonedDateTime endDateTimeZoned = endDateTime_zdt.withZoneSameInstant(estZone);

        // Create business hours in EST time zone for comparison.
        LocalDateTime businessStart_ldt = LocalDateTime.of(startDateTime.toLocalDate(), LocalTime.of(8, 0));
        LocalDateTime businessEnd_ldt = LocalDateTime.of(startDateTime.toLocalDate(), LocalTime.of(22, 0));
        ZonedDateTime businessStartZoned = ZonedDateTime.of(businessStart_ldt, estZone);
        ZonedDateTime businessEndZoned = ZonedDateTime.of(businessEnd_ldt, estZone);

        // Check if appointment is within business hours.
        if(startDateTimeZoned.isBefore(businessStartZoned)){ return false; }
        else if (startDateTimeZoned.isAfter(businessEndZoned)){ return false; }
        else if (endDateTimeZoned.isBefore(businessStartZoned)){ return false; }
        else if (endDateTimeZoned.isAfter(businessEndZoned)) { return false; }
        else { return true; }
    }

    /**
     * Check if a LocalDateTime is within 15 minutes of the system time.
     * @param startDateTime LocalDateTime object
     * @return true if the LocalDateTime is within 15 minutes
     */
    public static boolean isWithin15Minutes(LocalDateTime startDateTime){
        LocalDateTime today = LocalDateTime.now();
        LocalTime currentTime = LocalTime.now();

        LocalTime startTime = startDateTime.toLocalTime();
        long timeDifference = ChronoUnit.MINUTES.between(currentTime, startTime);

        return timeDifference > 0 && timeDifference <= 15;
    }

    /**
     * Check if LocalDateTime overlaps between two other LocalDateTimes.
     * @param new_start The object to check for overlapping.
     * @param existing_start The start time of the existing item that new_start is being checked against.
     * @param existing_end The end time of the existing item that new_start is being checked against.
     * @return false if there is no overlap
     */
    public static boolean overlaps(LocalDateTime new_start, LocalDateTime new_end, LocalDateTime existing_start, LocalDateTime existing_end) {
        if(new_start.isAfter(existing_start) && new_start.isBefore(existing_end)){
            return true;
        }
        else if(new_start.equals(existing_start)){
            return true;
        }
        else if(new_end.isAfter(existing_start) && new_start.isBefore(existing_end)) {
            System.out.println(new_end + " is after " + existing_start);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Get the start date of any given week.
     * @param any_date any LocalDate
     * @return LocalDate at the start of the given week.
     */
    public static LocalDate getStartOfWeek(LocalDate any_date){
        // Go backward to get Sunday
        while (any_date.getDayOfWeek() != DayOfWeek.SUNDAY) {
            any_date = any_date.minusDays(1);
        }
        return any_date;
    }

    /**
     * Get the end date of any given week.
     * @param any_date any LocalDate
     * @return LocalDate at the end of the given week.
     */
    public static LocalDate getEndOfWeek(LocalDate any_date){
        // Go forward to get Saturday
        while (any_date.getDayOfWeek() != DayOfWeek.SATURDAY) {
            any_date = any_date.plusDays(1);
        }
        return any_date;
    }

    /**
     * Get the end of the month for any given date.
     * @param any_date any LocalDate
     * @return LocalDate at the end day of the given month.
     */
    public static LocalDate getEndOfMonth(LocalDate any_date){
        YearMonth thisYearMonth = YearMonth.of(any_date.getYear(), any_date.getMonth());
        return thisYearMonth.atEndOfMonth();
    }

    /**
     * Create a string with the month name and the year, i.e. JUNE 2021
     * @param any_date Any LocalDate
     * @return a string with the month name and the year, i.e. JUNE 2021
     */
    public static String formatMonthNicely(LocalDate any_date){
        String monthName = any_date.getMonth().toString();
        int year = any_date.getYear();
        return monthName + " " + year;
    }

    /**
     * Create a string with the start and end dates of a week, i.e. JUNE 6 2021 - JUNE 12 2021
     * @param start_week LocalDate The start date of the week to format.
     * @return A formatted string showing the date range of 1 week.
     */
    public static String formatWeekNicely(LocalDate start_week){
        LocalDate end_week = getEndOfWeek(start_week);
        String StartMonthName = start_week.getMonth().toString();
        String EndMonthName = end_week.getMonth().toString();
        int StartWeekDay = start_week.getDayOfMonth();
        int EndWeekDay = end_week.getDayOfMonth();
        int startYear = start_week.getYear();
        int endYear = end_week.getYear();
        return StartMonthName + " " + StartWeekDay + " " + startYear + " - " + EndMonthName + " " + EndWeekDay + " " + endYear;
    }

}
