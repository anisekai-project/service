package me.anisekai.toshiko.helpers;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateHelper {

    private DateHelper() {}

    public static String format(ChronoLocalDateTime<LocalDate> time) {
        return time.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm:ss"));
    }

}
