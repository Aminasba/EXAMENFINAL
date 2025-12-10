package agenda;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList; 
import java.util.List;

/**
 * Description : An agenda that stores events
 */
public class Agenda {
    
    // Ajouté : La liste interne pour stocker les événements (#events dans l'UML)
    private final List<Event> events = new ArrayList<>();

    /**
     * Adds an event to this agenda
     *
     * @param e the event to add
     */
    public void addEvent(Event e) {
        // TODO : implémenter cette méthode
        events.add(e);
    }

    /**
     * Computes the events that occur on a given day
     *
     * @param day the day toi test
     * @return a list of events that occur on that day
     */
    public List<Event> eventsInDay(LocalDate day) {
        // TODO : implémenter cette méthode
        List<Event> result = new ArrayList<>();
        
        for (Event e : events) {
            if (e.isInDay(day)) {
                result.add(e);
            }
        }
        return result;
    }

    // Ajouté : Un getter simple pour les tests unitaires AgendaTest.setUp() (non UML)
    public List<Event> getEvents() {
        return events;
    }
    
    // --- Implémentation des Questions complémentaires ---
    
    /**
     * Trouver les événements de l'agenda en fonction de leur titre
     * @param title le titre à rechercher
     * @return les événements qui ont le même titre
     */
    public List<Event> findByTitle(String title) {
        // TODO : implémenter cette méthode
        List<Event> foundEvents = new ArrayList<>();
        
        for (Event e : events) {
            if (e.getTitle().equals(title)) {
                foundEvents.add(e);
            }
        }
        return foundEvents;
    }
        
    /**
     * Déterminer s’il y a de la place dans l'agenda pour un événement (aucun autre événement au même moment)
     * @param newEvent L'événement à tester (on se limitera aux événements sans répétition)
     * @return vrai s’il y a de la place dans l'agenda pour cet événement
     */
    public boolean isFreeFor(Event newEvent) {
        // TODO : implémenter cette méthode
        
        LocalDateTime newStart = newEvent.getStart();
        Duration newDuration = newEvent.getDuration();
        LocalDateTime newEnd = newStart.plus(newDuration);
        
        for (Event existingEvent : events) {
            
            LocalDateTime existingStart = existingEvent.getStart();
            Duration existingDuration = existingEvent.getDuration();
            LocalDateTime existingEnd = existingStart.plus(existingDuration);
            
            boolean isOverlapping = newStart.isBefore(existingEnd) && existingStart.isBefore(newEnd);
            
            if (isOverlapping) {
                return false;
            }
        }
        
        return true;
    }
}