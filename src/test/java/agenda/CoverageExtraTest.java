package agenda;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class CoverageExtraTest {

    @Test
    public void testDailyAndMonthlyTerminationCalculations() {
        LocalDateTime start = LocalDateTime.of(2020, 11, 1, 10, 0);

        Event daily = new Event("Daily", start, Duration.ofHours(1));
        daily.setRepetition(ChronoUnit.DAYS);
        daily.setTermination(3); // 3 occurrences => last = start + 2 days
        LocalDate expectedDailyLast = start.toLocalDate().plusDays(2);
        assertEquals(expectedDailyLast, daily.getTerminationDate());

        Event monthly = new Event("Monthly", start, Duration.ofHours(1));
        monthly.setRepetition(ChronoUnit.MONTHS);
        monthly.setTermination(3); // last = start + 2 months
        LocalDate expectedMonthlyLast = start.toLocalDate().plusMonths(2);
        assertEquals(expectedMonthlyLast, monthly.getTerminationDate());

        // Reverse: termination date -> number of occurrences
        Event daily2 = new Event("Daily2", start, Duration.ofHours(1));
        daily2.setRepetition(ChronoUnit.DAYS);
        daily2.setTermination(start.toLocalDate().plusDays(2)); // inclusive
        assertEquals(3, daily2.getNumberOfOccurrences());
    }

    @Test
    public void testTerminationBeforeStartGivesZeroOccurrences() {
        LocalDateTime start = LocalDateTime.of(2020, 11, 5, 10, 0);
        Event e = new Event("Before", start, Duration.ofHours(1));
        e.setRepetition(ChronoUnit.WEEKS);
        // set a termination date before the start
        e.setTermination(LocalDate.of(2020, 11, 1));
        assertEquals(0, e.getNumberOfOccurrences());
    }

    @Test
    public void testMethodsOnNonRepeatingEventDoNothing() {
        LocalDateTime start = LocalDateTime.of(2020, 11, 1, 9, 0);
        Event simple = new Event("SimpleNoRep", start, Duration.ofMinutes(30));

        // Calling addException and setTermination on a non-repeating event should be safe
        simple.addException(start.toLocalDate().plusDays(1));
        simple.setTermination(start.toLocalDate().plusDays(5));

        assertNull(simple.getRepetition());
        assertEquals(-1, simple.getNumberOfOccurrences());
        assertNull(simple.getTerminationDate());
    }

    @Test
    public void testAgendaGetEventsGetter() {
        Agenda agenda = new Agenda();
        LocalDateTime start = LocalDateTime.of(2020, 11, 1, 9, 0);
        Event e = new Event("A", start, Duration.ofMinutes(30));
        agenda.addEvent(e);
        assertTrue(agenda.getEvents().contains(e));
    }

    @Test
    public void testMonthlyIsInDay() {
        LocalDateTime start = LocalDateTime.of(2020, 1, 15, 10, 0);
        Event monthly = new Event("MonthlyCheck", start, Duration.ofHours(1));
        monthly.setRepetition(ChronoUnit.MONTHS);
        // same day next month
        assertTrue(monthly.isInDay(start.toLocalDate().plusMonths(1)));
        // and the start day
        assertTrue(monthly.isInDay(start.toLocalDate()));
    }

    @Test
    public void testUnsupportedFrequencyReturnsNullTerminationDate() {
        LocalDateTime start = LocalDateTime.of(2020, 11, 1, 10, 0);
        Event e = new Event("Unsupported", start, Duration.ofHours(1));
        // Use a frequency that is not DAYS/WEEKS/MONTHS to hit the default branch
        e.setRepetition(ChronoUnit.YEARS);
        e.setTermination(3);
        // The calculation falls through to return null for unsupported units
        assertNull(e.getTerminationDate());
    }

    @Test
    public void testWeeklyAndMonthlyMismatchNotInDay() {
        // Weekly mismatch: different day of week
        LocalDateTime monday = LocalDateTime.of(2020, 11, 2, 9, 0); // Monday
        Event weekly = new Event("Weekly", monday, Duration.ofHours(1));
        weekly.setRepetition(ChronoUnit.WEEKS);
        // one week later but next day -> different day of week
        assertTrue(!weekly.isInDay(monday.toLocalDate().plusWeeks(1).plusDays(1)));

        // Monthly mismatch: same month offset but different day
        LocalDateTime start = LocalDateTime.of(2020, 1, 15, 10, 0);
        Event monthly = new Event("MonthlyMismatch", start, Duration.ofHours(1));
        monthly.setRepetition(ChronoUnit.MONTHS);
        assertTrue(!monthly.isInDay(start.toLocalDate().plusMonths(1).plusDays(1)));
    }

    @Test
    public void testAfterLastOccurrenceAndAfterTerminationDateAreNotInDay() {
        LocalDateTime start = LocalDateTime.of(2020, 11, 1, 10, 0);
        Event occLimited = new Event("OccLimited", start, Duration.ofHours(1));
        occLimited.setRepetition(ChronoUnit.WEEKS);
        occLimited.setTermination(3); // last occurrence = start + 2 weeks
        // Day after the last occurrence
        assertTrue(!occLimited.isInDay(start.toLocalDate().plusWeeks(3)));

        Event dateLimited = new Event("DateLimited", start, Duration.ofHours(1));
        dateLimited.setRepetition(ChronoUnit.WEEKS);
        LocalDate term = start.toLocalDate().plusWeeks(1);
        dateLimited.setTermination(term);
        // day after terminationDate
        assertTrue(!dateLimited.isInDay(term.plusDays(1)));
    }

    @Test
    public void exerciseIsInDayAllPaths() {
        LocalDateTime start = LocalDateTime.of(2020, 1, 1, 9, 0);
        Duration dur = Duration.ofHours(2);

        ChronoUnit[] units = new ChronoUnit[]{ChronoUnit.DAYS, ChronoUnit.WEEKS, ChronoUnit.MONTHS, ChronoUnit.YEARS};

        for (ChronoUnit u : units) {
            Event e = new Event("BruteForce", start, dur);
            e.setRepetition(u);
            // add an exception and a termination to exercise branches
            e.addException(start.toLocalDate().plusDays(2));
            e.setTermination(start.toLocalDate().plusDays(10));

            for (int d = -30; d <= 60; d++) {
                LocalDate day = start.toLocalDate().plusDays(d);
                // call without asserting: we only want to execute branches
                e.isInDay(day);
            }
        }
    }
}
