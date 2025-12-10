package agenda;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

public class Termination {
    
    // Attributs pour stocker les informations nécessaires au calcul
    private final LocalDate myStart;
    private final ChronoUnit myFrequency;
    
    // Les deux valeurs possibles de terminaison, une seule sera définie
    private LocalDate myTerminationDateInclusive = null;
    private long myNumberOfOccurrences = -1;

    // --- Getters ---

    public LocalDate terminationDateInclusive() {
        // TODO : implémenter cette méthode
        if (myTerminationDateInclusive == null && myNumberOfOccurrences > 0) {
            
            return calculateTerminationDateFromOccurrences();
        }
        return myTerminationDateInclusive;
    }

    public long numberOfOccurrences() {
        // TODO : implémenter cette méthode
        if (myNumberOfOccurrences == -1 && myTerminationDateInclusive != null) {
            
            return calculateNumberOfOccurrencesFromDate();
        }
        return myNumberOfOccurrences;
    }

    

    /**
     * Constructs a  termination at a given date
     * @param start the start time of this event
     * @param frequency one of :
     * <UL>
     * <LI>ChronoUnit.DAYS for daily repetitions</LI>
     * <LI>ChronoUnit.WEEKS for weekly repetitions</LI>
     * <LI>ChronoUnit.MONTHS for monthly repetitions</LI>
     * </UL>
     * @param terminationInclusive the date when this event ends
     * @see ChronoUnit#between(Temporal, Temporal)
     */
    public Termination(LocalDate start, ChronoUnit frequency, LocalDate terminationInclusive) {
        // TODO : implémenter cette méthode
        this.myStart = start;
        this.myFrequency = frequency;
        this.myTerminationDateInclusive = terminationInclusive;
        
    }

    /**
     * Constructs a fixed termination event ending after a number of iterations
     * @param start the start time of this event
     * @param frequency one of :
     * <UL>
     * <LI>ChronoUnit.DAYS for daily repetitions</LI>
     * <LI>ChronoUnit.WEEKS for weekly repetitions</LI>
     * <LI>ChronoUnit.MONTHS for monthly repetitions</LI>
     * </UL>
     * @param numberOfOccurrences the number of occurrences of this repetitive event
     */
    public Termination(LocalDate start, ChronoUnit frequency, long numberOfOccurrences) {
        // TODO : implémenter cette méthode
        this.myStart = start;
        this.myFrequency = frequency;
        this.myNumberOfOccurrences = numberOfOccurrences;
        
    }
    
    

    /**
     * Calcule la date de fin à partir du nombre d'occurrences.
     * mais pour que le test passe, il faut trouver la N-ième occurrence.
     * Pour simplifier, on ignore les jours exacts dans le cas MONTHS/WEEKS, 
     * on se contente d'avancer le temps.
     */
    private LocalDate calculateTerminationDateFromOccurrences() {
    
        long repetitions = myNumberOfOccurrences - 1;
        
        if (myFrequency == ChronoUnit.DAYS) {
             // Dans ce cas simple, on ajoute juste N-1 jours au début
            return myStart.plusDays(repetitions);
        } else if (myFrequency == ChronoUnit.WEEKS) {
            // On ajoute N-1 semaines
            return myStart.plusWeeks(repetitions);
        } else if (myFrequency == ChronoUnit.MONTHS) {
            // On ajoute N-1 mois
            return myStart.plusMonths(repetitions);
        }
        
        return null;
    }

    /**
     * Calcule le nombre d'occurrences à partir de la date de fin.
     */
    private long calculateNumberOfOccurrencesFromDate() {
        if (myTerminationDateInclusive.isBefore(myStart)) {
            return 0;
        }

        
        long difference = myFrequency.between(myStart, myTerminationDateInclusive);
        
        return difference + 1;
    }
}