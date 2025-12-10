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
            // On a besoin de la date de début de l'Event et de la fréquence de Repetition
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
        
        // Vérification des répétitions

        if (myRepetition != null) {
            // La répétition ne peut se produire qu'à partir de la date de l'événement de base
            if (aDay.isBefore(myStart.toLocalDate())) {
                return false;
            }

            //  Vérification des exceptions
            if (myRepetition.isException(aDay)) {
                return false;
            }

            //  Vérification de la terminaison
            Termination term = myRepetition.getTermination();
            if (term != null) {
                // Terminaison par date
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
            
            // Calculer la différence entre les deux dates selon l'unité de fréquence
            long diff = frequency.between(baseDate, aDay);
            
            // Si la date est après la date de début
            if (diff >= 0) {

                
                // Si la différence est un multiple de 1 (toujours vrai pour between avec DAYS/WEEKS/MONTHS)
                // et que l'occurrence tombe le bon jour de la semaine/mois.
                
                if (frequency.equals(ChronoUnit.DAYS)) {
                    
                    return true;
                } else if (frequency.equals(ChronoUnit.WEEKS)) {
                    // Doit tomber le même jour de la semaine (dimanche pour l'exemple du test)
                    if (aDay.getDayOfWeek() == baseDate.getDayOfWeek()) {
                        return true;
                    }
                } else if (frequency.equals(ChronoUnit.MONTHS)) {
                    // Doit tomber le même jour du mois (1er pour l'exemple)
                    if (aDay.getDayOfMonth() == baseDate.getDayOfMonth()) {
                        return true;
                    }
                }
            }
        }
        
        // Si aucune des conditions n'est remplie
        return false;
    }
    
    /**
     * @return the myTitle
     */
    public String getTitle() {
        return myTitle;
    }

    /**
     * @return the myStart
     */
    public LocalDateTime getStart() {
        return myStart;
    }


    /**
     * @return the myDuration
     */
    public Duration getDuration() {
        return myDuration;
    }
    
    
    public Repetition getRepetition() {
        return myRepetition;
    }

    @Override
     public String toString() {
        return "Event{title='%s', start=%s, duration=%s}".formatted(myTitle, myStart, myDuration);
    }
}