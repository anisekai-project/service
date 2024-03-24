package me.anisekai.globals.utils;

import jakarta.annotation.Nullable;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DateTimeUtils {

    private final static DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DateTimeUtils() {}

    /**
     * Retrieve the system current {@link LocalTime} with nanoseconds set to 0.
     *
     * @return The system current {@link LocalTime}
     */
    public static LocalTime timeNow() {

        return LocalTime.now().withNano(0);
    }

    /**
     * Retrieve the system current {@link ZonedDateTime} with nanoseconds set to 0.
     *
     * @return The system current {@link ZonedDateTime}
     */
    public static ZonedDateTime now() {

        return ZonedDateTime.now().withNano(0);
    }

    /**
     * Parse the provided time string (in HH:mm format)
     *
     * @param time
     *         The string time
     *
     * @return The parsed {@link LocalTime}
     */
    public static LocalTime parseTime(String time) {

        return LocalTime.parse(time, TIME_FORMAT);
    }

    /**
     * Parse the provided date string (in dd/MM/yyyy format)
     *
     * @param date
     *         The string date
     *
     * @return The parsed {@link LocalDate}
     */
    public static LocalDate parseDate(String date) {

        return LocalDate.parse(date, DATE_FORMAT);
    }

    /**
     * Create a {@link ZonedDateTime} using provided time and date as string representation. Both arguments are optional
     * and will default to their current system value if not provided ({@link LocalTime#now()} and
     * {@link LocalDate#now()} respectively)
     * <p>
     * Although possible, sending 2 null values will return the same result as calling {@link ZonedDateTime#now()}, in a
     * less efficient way.
     *
     * @param time
     *         The optional time for the {@link ZonedDateTime}.
     * @param date
     *         The optional date for the {@link ZonedDateTime}.
     *
     * @return A {@link ZonedDateTime} based on input parameters
     */
    public static ZonedDateTime of(@Nullable String time, @Nullable String date) {

        LocalTime localTime = Optional.ofNullable(time).map(DateTimeUtils::parseTime).orElseGet(DateTimeUtils::timeNow);
        LocalDate localDate = Optional.ofNullable(date).map(DateTimeUtils::parseDate).orElseGet(LocalDate::now);
        return of(localTime, localDate);
    }

    /**
     * Create a {@link ZonedDateTime} using provided {@link LocalTime} and {@link LocalDate}. Both arguments are
     * optional and will default to their current system value if not provided ({@link LocalTime#now()} and
     * {@link LocalDate#now()} respectively)
     * <p>
     * Although possible, sending 2 null values will return the same result as calling {@link ZonedDateTime#now()}, in a
     * less efficient way.
     *
     * @param time
     *         The optional {@link LocalTime} for the {@link ZonedDateTime}.
     * @param date
     *         The optional {@link LocalDate} for the {@link ZonedDateTime}.
     *
     * @return A {@link ZonedDateTime} based on input parameters
     */
    public static ZonedDateTime of(@Nullable LocalTime time, @Nullable LocalDate date) {

        LocalTime effectiveTime = Optional.ofNullable(time).orElseGet(LocalTime::now);
        LocalDate effectiveDate = Optional.ofNullable(date).orElseGet(LocalDate::now);
        return ZonedDateTime.of(effectiveDate, effectiveTime, ZoneId.systemDefault());
    }

    /**
     * Converts a string representation of time to its equivalent value in minutes.
     *
     * @param timeString
     *         The time in a human-friendly format (e.g., "3d2h30m" for 3 days, 2 hours, and 30 minutes)
     *
     * @return The total amount of time represented by the string, in minutes
     *
     * @throws NumberFormatException
     *         If one of the parts in the string is not a valid number
     * @deprecated Use {@link #toDuration(String)} instead.
     */
    @Deprecated(forRemoval = true)
    public static long toMinutes(String timeString) throws NumberFormatException {

        return toDuration(timeString).toMinutes();
    }

    /**
     * Converts a string representation of time to its equivalent {@link Duration}.
     *
     * @param timeString
     *         The time in a human-friendly format (e.g., "3d2h30m" for 3 days, 2 hours, and 30 minutes)
     *
     * @return The total amount of time represented by the string, in minutes
     *
     * @throws NumberFormatException
     *         If one of the parts in the string is not a valid number
     */
    public static Duration toDuration(String timeString) {

        String  regex   = "(?<minus>-)?(?<days>\\d*d)?(?<hours>\\d*h)?(?<minutes>\\d+m)?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(timeString);

        if (matcher.find()) {
            String  daysStr    = matcher.group("days");
            String  hoursStr   = matcher.group("hours");
            String  minutesStr = matcher.group("minutes");
            boolean isNegative = matcher.group("minus") != null;

            Duration duration = getDuration(daysStr, hoursStr, minutesStr);

            return isNegative ? duration.negated() : duration;
        }
        throw new IllegalArgumentException("Invalid duration format: " + timeString);
    }

    private static Duration getDuration(String daysStr, String hoursStr, String minutesStr) {

        int days    = 0;
        int hours   = 0;
        int minutes = 0;

        if (daysStr != null) {
            days = Integer.parseInt(daysStr.substring(0, daysStr.length() - 1));
        }

        if (hoursStr != null) {
            hours = Integer.parseInt(hoursStr.substring(0, hoursStr.length() - 1));
        }

        if (minutesStr != null) {
            minutes = Integer.parseInt(minutesStr.substring(0, minutesStr.length() - 1));
        }

        return Duration.ofDays(days)
                       .plusHours(hours)
                       .plusMinutes(minutes);
    }

}
