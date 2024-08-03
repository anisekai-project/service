package me.anisekai.api.plannifier;

import me.anisekai.api.plannifier.data.T_WatchParty;
import me.anisekai.api.plannifier.data.T_WatchPartyManager;
import me.anisekai.api.plannifier.interfaces.Plannifiable;
import me.anisekai.api.plannifier.interfaces.Scheduler;
import me.anisekai.api.plannifier.interfaces.WatchParty;
import org.junit.jupiter.api.*;

import java.time.ZonedDateTime;

import static me.anisekai.api.plannifier.PlannifierTestData.*;
import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@DisplayName("AdvancedScheduler")
@Tag("slow-test")
public class AdvancedSchedulerTests {

    private static final long STATIC_ID = 4L;

    private PlannifierTestData                        data;
    private Scheduler<WatchParty<Long>, T_WatchParty> scheduler;
    private T_WatchPartyManager                       manager;

    @BeforeEach
    public void setup() {

        this.data      = new PlannifierTestData();
        this.manager   = new T_WatchPartyManager();
        this.scheduler = new AdvancedScheduler<>(this.data.advancedSchedulerData(), this.manager);
    }

    @Test
    @DisplayName("AdvancedScheduler | Check Planning | Should find something before")
    public void testFindPreviousSuccess() {

        ZonedDateTime time = delay(1);
        assertTrue("An item should has been found", this.scheduler.findPrevious(time).isPresent());

    }

    @Test
    @DisplayName("AdvancedScheduler | Check Planning | Should not find something before")
    public void testFindPreviousFail() {

        ZonedDateTime time = delay(-1);
        assertTrue("No item should has been found", this.scheduler.findPrevious(time).isEmpty());
    }

    @Test
    @DisplayName("AdvancedScheduler | Check Planning | Should find something after")
    public void testFindNextSuccess() {

        ZonedDateTime time = delay(1);
        assertTrue("An item should has been found", this.scheduler.findNext(time).isPresent());
    }

    @Test
    @DisplayName("AdvancedScheduler | Check Planning | Should not find something after")
    public void testFindNextFail() {

        ZonedDateTime time = delay(5);
        assertTrue("No item should has been found", this.scheduler.findNext(time).isEmpty());
    }

    @Test
    @DisplayName("AdvancedScheduler | Overlap | Should fit in between")
    public void testOverlapSafe() {

        T_WatchParty tryWith = T_WatchPartyManager.of(STATIC_ID, delay(1), 3L, 1L);
        assertTrue("The item did not fit into the schedule.", this.scheduler.canSchedule(tryWith));
    }

    @Test
    @DisplayName("AdvancedScheduler | Overlap | Should conflict")
    public void testOverlapFailure() {

        T_WatchParty tryWith = T_WatchPartyManager.of(STATIC_ID, BASE_DATETIME, 3L, 1L);
        assertFalse("The item fit into the schedule.", this.scheduler.canSchedule(tryWith));
    }

    @Test
    @DisplayName("AdvancedScheduler | Overlap | Should merge with previous")
    public void testMergeWithPrevious() {

        T_WatchParty tryWith = T_WatchPartyManager.of(STATIC_ID, delay(1), 1L, 1L);
        Assertions.assertDoesNotThrow(() -> this.scheduler.schedule(tryWith), "Unable to schedule event");
        Assertions.assertEquals(2700, this.data.watchPartyOne.getDuration().getSeconds(), "Event was not merged");
        Assertions.assertEquals(
                1440,
                this.data.watchPartyTwo.getDuration().getSeconds(),
                "Event was merged with the wrong event"
        );
        Assertions.assertEquals(
                1440,
                this.data.watchPartyThree.getDuration().getSeconds(),
                "Event was merged with the wrong event"
        );

        // Ensure our manager is called
        Assertions.assertEquals(1, this.manager.requestUpdateCount);
        Assertions.assertEquals(0, this.manager.requestCreateCount);
        Assertions.assertEquals(0, this.manager.requestDeleteCount);
    }

    @Test
    @DisplayName("AdvancedScheduler | Overlap | Should merge with next")
    public void testMergeWithNext() {

        T_WatchParty tryWith = T_WatchPartyManager.of(STATIC_ID, delay(1), 2L, 1L);
        Assertions.assertDoesNotThrow(() -> this.scheduler.schedule(tryWith), "Unable to schedule event");
        Assertions.assertEquals(
                1440,
                this.data.watchPartyOne.getDuration().getSeconds(),
                "Event was merged with the wrong event"
        );
        Assertions.assertEquals(2700, this.data.watchPartyTwo.getDuration().getSeconds(), "Event was not merged");
        Assertions.assertEquals(
                1440,
                this.data.watchPartyThree.getDuration().getSeconds(),
                "Event was merged with the wrong event"
        );

        // Ensure our manager is called
        Assertions.assertEquals(1, this.manager.requestUpdateCount);
        Assertions.assertEquals(0, this.manager.requestCreateCount);
        Assertions.assertEquals(0, this.manager.requestDeleteCount);
    }

    @Test
    @DisplayName("AdvancedScheduler | Overlap | Should merge with both")
    public void testMergeWithBoth() {

        T_WatchParty tryWith = T_WatchPartyManager.of(STATIC_ID, delay(3), 2L, 1L);
        Assertions.assertDoesNotThrow(() -> this.scheduler.schedule(tryWith), "Unable to schedule event");
        Assertions.assertEquals(
                1440,
                this.data.watchPartyOne.getDuration().getSeconds(),
                "Event was merged with the wrong event"
        );
        Assertions.assertEquals(3960, this.data.watchPartyTwo.getDuration().getSeconds(), "Event was not merged");
        Assertions.assertTrue(this.data.watchPartyThree.isTestTagDeleted(), "Event deletion was not requested");

        // Ensure our manager is called
        Assertions.assertEquals(1, this.manager.requestUpdateCount);
        Assertions.assertEquals(0, this.manager.requestCreateCount);
        Assertions.assertEquals(1, this.manager.requestDeleteCount);
    }

}
