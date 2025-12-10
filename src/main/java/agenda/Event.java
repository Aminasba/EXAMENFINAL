package agenda;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Event {

    /**
     * The myTitle of this event
     */
    private String myTitle;
    
    /**
     * The starting time of the event
     */
    private LocalDateTime myStart;

    /**
     * The durarion of the event 
     */
    private Duration myDuration;

    // Ajouté : L'association 0..1 avec Repetition
    private Repetition myRepetition = null;


    /**
     * Constructs an event
     *
     * @param title the title of this event
     * @param start the start time of this event
     * @param duration the duration of this event
     */
    public Event(String title, LocalDateTime start, Duration duration) {
        this.myTitle = title;
        this.myStart = start;
        this.myDuration = duration;
    }

    public void setRepetition(ChronoUnit frequency) {
        // TODO : implémenter cette méthode
        this.myRepetition = new Repetition(frequency);
    }

    public void addException(LocalDate date) {
        // TODO : implémenter cette méthode
        if (myRepetition != null) {
            myRepetition.addException(date);
        }
    }

    public void setTermination(LocalDate terminationInclusive) {
        // TODO : implémenter cette méthode
        if (myRepetition != null) {
            Repetition rep = myRepetition;
            Termination term = new Termination(myStart.toLocalDate(), rep.getFrequency(), terminationInclusive);
            rep.setTermination(term);
        }
    }

    public void setTermination(long numberOfOccurrences) {
        // TODO : implémenter cette méthode
        if (myRepetition != null) {
            Repetition rep = myRepetition;
            Termination term = new Termination(myStart.toLocalDate(), rep.getFrequency(), numberOfOccurrences);
            rep.setTermination(term);
        }
    }

    public int getNumberOfOccurrences() {
        // TODO : implémenter cette méthode
        if (myRepetition != null && myRepetition.getTermination() != null) {
            return (int) myRepetition.getTermination().numberOfOccurrences();
        }
        return -1;
    }

    public LocalDate getTerminationDate() {
        // TODO : implémenter cette méthode
        if (myRepetition != null && myRepetition.getTermination() != null) {
            return myRepetition.getTermination().terminationDateInclusive();
        }
        return null;
    }

    /**
     * Tests if an event occurs on a given day
     *
     * @param aDay the day to test
     * @return true if the event occurs on that day, false otherwise
     */
    public boolean isInDay(LocalDate aDay) {
        // TODO : implémenter cette méthode
        LocalDateTime eventEnd = myStart.plus(myDuration);
        
        LocalDateTime dayStart = aDay.atStartOfDay();
        LocalDateTime dayEnd = aDay.plusDays(1).atStartOfDay();

        boolean overlapsBaseDay = myStart.isBefore(dayEnd) && eventEnd.isAfter(dayStart);

        if (overlapsBaseDay) {
            if (myRepetition == null) {
                return true;
            }
            if (myRepetition.isException(aDay)) {
                return false;
            }
            return true;
        }
        
        if (myRepetition != null) {
            if (aDay.isBefore(myStart.toLocalDate())) {
                return false;
            }

            if (myRepetition.isException(aDay)) {
                return false;
            }

            Termination term = myRepetition.getTermination();
            if (term != null) {
                LocalDate termDate = term.terminationDateInclusive();
                if (termDate != null && aDay.isAfter(termDate)) {
                    return false;
                }
                
                long maxOccurrences = term.numberOfOccurrences();
                if (maxOccurrences > 0) {
                    LocalDate lastOccurrenceDate = term.terminationDateInclusive(); 
                    if (lastOccurrenceDate != null && aDay.isAfter(lastOccurrenceDate)) {
                        return false;
                    }
                }
            }
            
            LocalDate baseDate = myStart.toLocalDate();
            ChronoUnit frequency = myRepetition.getFrequency();
            
            long diff = frequency.between(baseDate, aDay);
            
            if (diff >= 0) {
                if (frequency.equals(ChronoUnit.DAYS)) {
                    return true;
                } else if (frequency.equals(ChronoUnit.WEEKS)) {
                    if (aDay.getDayOfWeek() == baseDate.getDayOfWeek()) {
                        return true;
                    }
                } else if (frequency.equals(ChronoUnit.MONTHS)) {
                    if (aDay.getDayOfMonth() == baseDate.getDayOfMonth()) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    public String getTitle() { return myTitle; }
    public LocalDateTime getStart() { return myStart; }
    public Duration getDuration() { return myDuration; }
    public Repetition getRepetition() { return myRepetition; }

    @Override
    public String toString() {
        return "Event{title='%s', start=%s, duration=%s}".formatted(myTitle, myStart, myDuration);
    }
}