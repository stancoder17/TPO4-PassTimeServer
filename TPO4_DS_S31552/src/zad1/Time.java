/**
 *
 *  @author Dyrda Stanisław S31552
 *
 */

package zad1;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Time {
    public static String passed(String from, String to) {
        DateTimeFormatter dateFormat;
        // Validate first
        try {
            validate(from, to);

            dateFormat = classify(from);
        } catch (Exception e) {
            System.out.print("*** ");
            System.out.print(e);
            return "";
        }

        // Get dates
        LocalDate fromDate = LocalDate.parse(from, dateFormat);
        LocalDate toDate = LocalDate.parse(to, dateFormat);

        // Obligatory: days and weeks
        long days = ChronoUnit.DAYS.between(fromDate, toDate);
        double weeks = (double) Math.round((days / 7.0) * 100) / 100; // rounded to .xx


        // Calendar part
        int years = -1;
        int months = -1;
        int daysCalendar = -1;
        if (days > 0) {
            Period period = Period.between(fromDate, toDate);
            years = period.getYears();
            months = period.getMonths();
            daysCalendar = period.getDays();
        }

        // Hours and minutes part
        long hours;
        long minutes;
        if (dateFormat.toString().equals(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm").toString())) {
            // LocalDate -> ZoneDateTime (to involve time change in Warsaw)
            ZoneId zone = ZoneId.of("Europe/Warsaw");

            ZonedDateTime fromDateTime = LocalDateTime.parse(from, dateFormat).atZone(zone);
            ZonedDateTime toDateTime = LocalDateTime.parse(to, dateFormat).atZone(zone);

            Duration duration = Duration.between(fromDateTime, toDateTime);
            hours = duration.toHours();
            minutes = duration.toMinutes();

            return getTimeText(fromDateTime.toLocalDateTime(), toDateTime.toLocalDateTime(), days, weeks, years, months, daysCalendar, hours, minutes);
        }


        return getTimeText(fromDate, toDate, days, weeks, years, months, daysCalendar);
    }

    public static String getTimeText(LocalDate fromDate, LocalDate toDate, long days, double weeks, int years, int months, int daysCalendar) {
        boolean hasCalendarPart = years != -1 && months != -1 && daysCalendar != -1;

        // Print header
        String from = String.format("Od %d %s %d (%s) ",
                fromDate.getDayOfMonth(),
                PolishLanguage.getMonthDeclension(PolishLanguage.numberToMonth(fromDate.getMonthValue())),
                fromDate.getYear(),
                PolishLanguage.translateDayOfWeekTo(fromDate.getDayOfWeek().toString())
        );

        String to = String.format("do %d %s %d (%s)",
                toDate.getDayOfMonth(),
                PolishLanguage.getMonthDeclension(PolishLanguage.numberToMonth(toDate.getMonthValue())),
                toDate.getYear(),
                PolishLanguage.translateDayOfWeekTo(toDate.getDayOfWeek().toString())
        );

        // Obligatory days and weeks
        StringBuilder result = new StringBuilder(from + to);
        result.append(String.format("\n - mija: %d %s, tygodni %s",
                days,
                PolishLanguage.getDayDeclension((int) days),
                (weeks % 1 == 0) ? String.valueOf((int) weeks) : String.valueOf(weeks)
        ));


        // "Optional" calendar part
        if (hasCalendarPart) {
            result.append("\n - kalendarzowo: ");
            if (years > 0) result.append(years)
                    .append(" ")
                    .append(PolishLanguage.getYearDeclension(years))
                    .append(", ");
            if (months > 0) result.append(months)
                    .append(" ")
                    .append(PolishLanguage.getMonthDeclension(months))
                    .append(", ");
            if (daysCalendar > 0) result.append(daysCalendar)
                    .append(" ")
                    .append(PolishLanguage.getDayDeclension(daysCalendar));
        }

        return result.toString();
    }


    public static String getTimeText(LocalDateTime fromDateTime, LocalDateTime toDateTime, long days, double weeks, int years, int months, int daysCalendar, long hours, long minutes) {
        boolean hasCalendarPart = years != -1 && months != -1 && daysCalendar != -1;

        // Print header
        String from = String.format("Od %d %s %d (%s) godz. %02d:%02d ",
                fromDateTime.getDayOfMonth(),
                PolishLanguage.getMonthDeclension(PolishLanguage.numberToMonth(fromDateTime.getMonthValue())),
                fromDateTime.getYear(),
                PolishLanguage.translateDayOfWeekTo(fromDateTime.getDayOfWeek().toString()),
                fromDateTime.getHour(),
                fromDateTime.getMinute()
        );

        String to = String.format("do %d %s %d (%s) godz. %02d:%02d",
                toDateTime.getDayOfMonth(),
                PolishLanguage.getMonthDeclension(PolishLanguage.numberToMonth(toDateTime.getMonthValue())),
                toDateTime.getYear(),
                PolishLanguage.translateDayOfWeekTo(toDateTime.getDayOfWeek().toString()),
                toDateTime.getHour(),
                toDateTime.getMinute()
        );

        // Obligatory days and weeks
        StringBuilder result = new StringBuilder(from + to);
        result.append(String.format("\n - mija: %d %s, tygodni %s",
                days,
                PolishLanguage.getDayDeclension((int) days),
                (weeks % 1 == 0) ? String.valueOf((int) weeks) : String.valueOf(weeks)
        ));

        // Hours and minutes
        result.append(String.format("\n - godzin: %d, minut: %d", hours, minutes));


        // "Optional" calendar part
        if (hasCalendarPart) {
            result.append("\n - kalendarzowo: ");
            if (years > 0) result.append(years)
                    .append(" ")
                    .append(PolishLanguage.getYearDeclension(years));
            if (months > 0) result.append(years > 0 ? " " : "").append(months)
                    .append(" ")
                    .append(PolishLanguage.getMonthDeclension(months))
                    .append(" ");
            if (daysCalendar > 0) result.append(years > 0 || months > 0 ? " " : "").append(daysCalendar)
                    .append(" ")
                    .append(PolishLanguage.getDayDeclension(daysCalendar));
        }

        return result.toString();
    }

    public static void validate(String date1, String date2) {
        DateTimeFormatter date1Classification = classify(date1);
        DateTimeFormatter date2Classification = classify(date2);

        if (!date1Classification.toString().equals(date2Classification.toString()))
            throw new RuntimeException("Dates are not of the same type");
    }

    public static DateTimeFormatter classify(String date) {
        if (date.contains("T")) {
            LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        }
        else {
            LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            return DateTimeFormatter.ISO_LOCAL_DATE;
        }
    }
}
