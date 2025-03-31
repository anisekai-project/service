package me.anisekai.server.enums;

import java.time.Month;
import java.time.ZonedDateTime;

public enum Season {

    WINTER("Hiver"),
    SPRING("Printemps"),
    SUMMER("Été"),
    AUTUMN("Automne");

    final String label;

    Season(String label) {

        this.label = label;
    }

    /**
     * Retrieve the {@link Season} matching the provided month.
     *
     * @param month
     *         Month number, from 1 to 12
     *
     * @return A {@link Season}
     */
    public static Season fromDate(ZonedDateTime date) {

        Month month = date.getMonth();
        return switch (month) {
            case DECEMBER, JANUARY, FEBRUARY -> WINTER;
            case MARCH, APRIL, MAY -> SPRING;
            case JUNE, JULY, AUGUST -> SUMMER;
            case SEPTEMBER, OCTOBER, NOVEMBER -> AUTUMN;
        };
    }

    public String getLabel() {

        return this.label;
    }
}

