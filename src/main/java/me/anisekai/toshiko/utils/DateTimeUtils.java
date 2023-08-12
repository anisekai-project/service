package me.anisekai.toshiko.utils;

import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DateTimeUtils {

    private final static DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DateTimeUtils() {}

    public static ZonedDateTime of(String time, @Nullable String date) {

        LocalTime localTime = LocalTime.parse(time, TIME_FORMAT);
        if (date == null) return of(localTime, null);
        return of(localTime, LocalDate.parse(date, DATE_FORMAT));
    }

    public static ZonedDateTime of(LocalTime time, @Nullable LocalDate date) {

        if (date == null) {
            LocalDate today = LocalDate.now();
            return ZonedDateTime.of(today, time, ZoneId.systemDefault());
        } else {
            return ZonedDateTime.of(date, time, ZoneId.systemDefault());
        }
    }

    public static long toMinutes(String timeString) throws NumberFormatException {

        String  regex   = "(?<minus>-)?(?<days>\\d*d)?(?<hours>\\d*h)?(?<minutes>\\d+m)?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(timeString);
        long    time    = 0L;
        if (matcher.find()) {
            String minus = matcher.group("minus");
            String day = matcher.group("days");
            String hour   = matcher.group("hours");
            String minute = matcher.group("minutes");

            if (day != null) {
                time += Long.parseLong(day.replace("d", "")) * 1440;
            }

            if (hour != null) {
                time += Long.parseLong(hour.replace("h", "")) * 60;
            }
            if (minute != null) {
                time += Long.parseLong(minute.replace("m", ""));
            }

            if (minus != null) {
                time *= -1;
            }
        }
        return time;
    }

}
