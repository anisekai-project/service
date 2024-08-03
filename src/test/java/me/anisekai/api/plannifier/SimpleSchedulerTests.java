package me.anisekai.api.plannifier;

import me.anisekai.api.plannifier.data.T_Plannifiable;
import me.anisekai.api.plannifier.data.T_PlannificationManager;
import me.anisekai.api.plannifier.interfaces.Plannifiable;
import me.anisekai.api.plannifier.interfaces.Scheduler;
import org.junit.jupiter.api.*;

import java.time.ZonedDateTime;

import static me.anisekai.api.plannifier.PlannifierTestData.*;
import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@DisplayName("SimpleScheduler")
@Tag("slow-test")
public class SimpleSchedulerTests {

    private PlannifierTestData data;
    private Scheduler<Plannifiable, T_Plannifiable> scheduler;

    @BeforeEach
    public void setup() {

        this.data = new PlannifierTestData();
        this.scheduler = new SimpleScheduler<>(this.data.simpleSchedulerData(), new T_PlannificationManager());
    }

    @Test
    @DisplayName("SimpleScheduler | Check Planning | Should find something before")
    public void testFindPreviousSuccess() {

        ZonedDateTime time = delay(1);
        Assertions.assertTrue(this.scheduler.findPrevious(time).isPresent(), "No event found");

    }

    @Test
    @DisplayName("SimpleScheduler | Check Planning | Should not find something before")
    public void testFindPreviousFail() {

        ZonedDateTime time = delay(-1);
        Assertions.assertTrue(this.scheduler.findPrevious(time).isEmpty(), "An event has been found");
    }

    @Test
    @DisplayName("SimpleScheduler | Check Planning | Should find something after")
    public void testFindNextSuccess() {

        ZonedDateTime time = delay(1);
        Assertions.assertTrue(this.scheduler.findNext(time).isPresent(), "No event found");
    }

    @Test
    @DisplayName("SimpleScheduler | Check Planning | Should not find something after")
    public void testFindNextFail() {

        ZonedDateTime time = delay(5);
        Assertions.assertTrue(this.scheduler.findNext(time).isEmpty(), "An event has been found");
    }

    @Test
    @DisplayName("SimpleScheduler | Overlap | Should fit in between")
    public void testOverlapSafe() {

        Plannifiable  tryWith = Plannifiable.of(delay(1), DURATION);
        Assertions.assertTrue(this.scheduler.canSchedule(tryWith), "An event has been found");
        assertTrue("The item did not fit into the schedule.", this.scheduler.canSchedule(tryWith));
    }

    @Test
    @DisplayName("SimpleScheduler | Overlap | Should conflict")
    public void testOverlapFailure() {

        Plannifiable tryWith = Plannifiable.of(BASE_DATETIME, DURATION);
        assertFalse("The item fit into the schedule.", this.scheduler.canSchedule(tryWith));
    }

}
