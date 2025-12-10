package agenda;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

public class Repetition {
    
    private Set<LocalDate> myExceptions = new HashSet<>();
    private Termination myTermination = null;
    
    public ChronoUnit getFrequency() {
        return myFrequency;
    }

    /**
     * Stores the frequency of this repetition, one of :
     * <UL>
     * <LI>ChronoUnit.DAYS for daily repetitions</LI>
     * <LI>ChronoUnit.WEEKS for weekly repetitions</LI>
     * <LI>ChronoUnit.MONTHS for monthly repetitions</LI>
     * </UL>
     */
    private final ChronoUnit myFrequency;

    public Repetition(ChronoUnit myFrequency) {
        this.myFrequency = myFrequency;
    }

    /**
     * Les exceptions à la répétition
     * @param date un date à laquelle l'événement ne doit pas se répéter
     */
    public void addException(LocalDate date) {
        // TODO : implémenter cette méthode
        myExceptions.add(date);
    }
    
    /**
     * Vérifie si le jour est une exception. (Ajouté pour la méthode isInDay d'Event)
     * @param day La date à vérifier.
     * @return true si c'est une exception.
     */
    public boolean isException(LocalDate day) {
        return myExceptions.contains(day);
    }
    
    /**
     * Getter pour la terminaison (Ajouté pour la méthode isInDay d'Event)
     * @return L'objet Termination associé.
     */
    public Termination getTermination() {
        return myTermination;
    }

    /**
     * La terminaison d'une répétition (optionnelle)
     * @param termination la terminaison de la répétition
     */
    public void setTermination(Termination termination) {
        // TODO : implémenter cette méthode
        this.myTermination = termination;
    }
}