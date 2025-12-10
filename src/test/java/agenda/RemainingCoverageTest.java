package agenda;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

public class RemainingCoverageTest {

    @Test
    public void testIsInDay_OverlapsWithRepetitionException() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 10, 10, 0);
        Event ev = new Event("e", start, Duration.ofHours(1));
        ev.setRepetition(ChronoUnit.DAYS);
        // mark the base day as exception
        ev.addException(start.toLocalDate());

        // overlaps the base day, but exception => should be false
        assertFalse(ev.isInDay(start.toLocalDate()));
    }

    @Test
    public void testIsInDay_AfterLastOccurrenceWithTerminationByCount() {
        LocalDateTime start = LocalDateTime.of(2025, 2, 1, 9, 0);
        Event ev = new Event("e2", start, Duration.ofHours(2));
        ev.setRepetition(ChronoUnit.DAYS);
        // 3 occurrences: 1st (2025-02-01), 2nd (2025-02-02), 3rd (2025-02-03)
        ev.setTermination(3L);

        LocalDate afterLast = start.toLocalDate().plusDays(3); // 2025-02-04
        assertFalse(ev.isInDay(afterLast));
    }

    @Test
    public void testTermination_numberOfOccurrences_whenTerminationBeforeStart_returnsZero() {
        LocalDate start = LocalDate.of(2025, 3, 10);
        // termination before start
        LocalDate term = LocalDate.of(2025, 3, 1);
        Termination t = new Termination(start, ChronoUnit.DAYS, term);

        assertEquals(0L, t.numberOfOccurrences());
    }

    @Test
    public void testTermination_terminationDateFromOccurrences_supportedFrequencies() {
        LocalDate start = LocalDate.of(2025, 4, 1);
        Termination tDays = new Termination(start, ChronoUnit.DAYS, 4L);
        assertEquals(start.plusDays(3), tDays.terminationDateInclusive());

        Termination tWeeks = new Termination(start, ChronoUnit.WEEKS, 2L);
        assertEquals(start.plusWeeks(1), tWeeks.terminationDateInclusive());

        Termination tMonths = new Termination(start, ChronoUnit.MONTHS, 3L);
        assertEquals(start.plusMonths(2), tMonths.terminationDateInclusive());
    }
}
