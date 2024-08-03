package me.anisekai.api.plannifier;

import me.anisekai.api.plannifier.data.T_Plannifiable;
import me.anisekai.api.plannifier.data.T_WatchParty;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class PlannifierTestData {

    public static final ZonedDateTime BASE_DATETIME = ZonedDateTime.of(
            LocalDate.of(2024, 1, 1),
            LocalTime.of(0, 0),
            ZoneId.systemDefault()
    );

    public static final Duration DELAY    = Duration.of(25, ChronoUnit.MINUTES);
    public static final Duration DURATION = Duration.of(24, ChronoUnit.MINUTES);

    public static ZonedDateTime delay(int time) {

        Function<ZonedDateTime, ZonedDateTime> mod   = time < 0 ? (z) -> z.minus(DELAY) : (z) -> z.plus(DELAY);
        int                                    count = Math.abs(time);

        ZonedDateTime zdt = BASE_DATETIME;
        for (int i = 0; i < count; i++) {
            zdt = mod.apply(zdt);
        }

        return zdt;
    }

    public final T_Plannifiable plannifiableOne = new T_Plannifiable(1L, BASE_DATETIME, DURATION);
    public final T_Plannifiable plannifiableTwo = new T_Plannifiable(2L, delay(2), DURATION);

    public List<T_Plannifiable> simpleSchedulerData() {

        return Arrays.asList(
                this.plannifiableOne,
                this.plannifiableTwo
        );
    }

    public final T_WatchParty watchPartyOne   = new T_WatchParty(1L, BASE_DATETIME, 1L, 1L);
    public final T_WatchParty watchPartyTwo   = new T_WatchParty(2L, delay(2), 2L, 1L);
    public final T_WatchParty watchPartyThree = new T_WatchParty(3L, delay(4), 2L, 1L);

    public List<T_WatchParty> advancedSchedulerData() {

        return Arrays.asList(
                this.watchPartyOne,
                this.watchPartyTwo,
                this.watchPartyThree
        );
    }

}
