package agenda;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Termination {
    
    // Attributs ajoutés pour stocker les informations nécessaires au calcul
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

    // --- Constructeurs ---

    /**
     * Constructs a  termination at a given date
     */
    public Termination(LocalDate start, ChronoUnit frequency, LocalDate terminationInclusive) {
        // TODO : implémenter cette méthode
        this.myStart = start;
        this.myFrequency = frequency;
        this.myTerminationDateInclusive = terminationInclusive;
    }

    /**
     * Constructs a fixed termination event ending after a number of iterations
     */
    public Termination(LocalDate start, ChronoUnit frequency, long numberOfOccurrences) {
        // TODO : implémenter cette méthode
        this.myStart = start;
        this.myFrequency = frequency;
        this.myNumberOfOccurrences = numberOfOccurrences;
    }
    
    // --- Logique de calcul (privée) ---

    private LocalDate calculateTerminationDateFromOccurrences() {
        long repetitions = myNumberOfOccurrences - 1;
        
        if (myFrequency == ChronoUnit.DAYS) {
            return myStart.plusDays(repetitions);
        } else if (myFrequency == ChronoUnit.WEEKS) {
            return myStart.plusWeeks(repetitions);
        } else if (myFrequency == ChronoUnit.MONTHS) {
            return myStart.plusMonths(repetitions);
        }
        return null;
    }

    private long calculateNumberOfOccurrencesFromDate() {
        if (myTerminationDateInclusive.isBefore(myStart)) {
            return 0;
        }

        long difference = myFrequency.between(myStart, myTerminationDateInclusive);
        
        return difference + 1;
    }
}