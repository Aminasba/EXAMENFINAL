package agenda;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AgendaTest {
    Agenda agenda;

    // November 1st, 2020 (Dimanche)
    LocalDate nov_1_2020 = LocalDate.of(2020, 11, 1);

    // January 5, 2021
    LocalDate jan_5_2021 = LocalDate.of(2021, 1, 5);

    // November 1st, 2020, 22:30
    LocalDateTime nov_1_2020_22_30 = LocalDateTime.of(2020, 11, 1, 22, 30);
    
    // Heure de début pour les tests de collision (Lundi 2 Nov, 9h00)
    LocalDateTime BASE_TIME_COLLISION = LocalDateTime.of(2020, 11, 2, 9, 0); 

    // 120 minutes
    Duration min_120 = Duration.ofMinutes(120);

    // Événements de base
    Event simple;
    Event simple2; // 9h00-10h00
    Event simple3; // 11h30-12h00
    Event fixedTermination;
    Event fixedRepetitions;
    Event neverEnding;

    @BeforeEach
    public void setUp() {
        simple = new Event("Simple event", nov_1_2020_22_30, min_120);
        
        // Ajout d'événements pour les tests complémentaires (Lundi 2 Nov):
        simple2 = new Event("Réunion Lundi", BASE_TIME_COLLISION, Duration.ofMinutes(60)); 
        // On place cet événement à 11h30 (9:00 + 2h30) pour laisser un créneau libre entre 10:00 et 11:30
        simple3 = new Event("Tache Lundi", BASE_TIME_COLLISION.plusHours(2).plusMinutes(30), Duration.ofMinutes(30)); 

        fixedTermination = new Event("Fixed termination weekly", nov_1_2020_22_30, min_120);
        fixedTermination.setRepetition(ChronoUnit.WEEKS);
        fixedTermination.setTermination(jan_5_2021);

        fixedRepetitions = new Event("Fixed termination weekly", nov_1_2020_22_30, min_120);
        fixedRepetitions.setRepetition(ChronoUnit.WEEKS);
        fixedRepetitions.setTermination(10);

        neverEnding = new Event("Never Ending", nov_1_2020_22_30, min_120);
        neverEnding.setRepetition(ChronoUnit.DAYS);

        agenda = new Agenda();
        agenda.addEvent(simple);
        agenda.addEvent(fixedTermination);
        agenda.addEvent(fixedRepetitions);
        agenda.addEvent(neverEnding);
        agenda.addEvent(simple2);
        agenda.addEvent(simple3);
    }

    @Test
    public void testMultipleEventsInDay() {
        assertEquals(4, agenda.eventsInDay(nov_1_2020).size(),
            "Il y a 4 événements ce jour là");
        assertTrue(agenda.eventsInDay(nov_1_2020).contains(neverEnding));
        assertTrue(agenda.eventsInDay(nov_1_2020).contains(simple));
        assertTrue(agenda.eventsInDay(nov_1_2020).contains(fixedTermination));
        assertTrue(agenda.eventsInDay(nov_1_2020).contains(fixedRepetitions));
    }
    
    // NOUVEAUX TESTS POUR LES QUESTIONS COMPLÉMENTAIRES
    
    @Test
    public void testFindByTitle_MultipleResults() {
        Event reunion2 = new Event("Réunion Lundi", BASE_TIME_COLLISION.plusHours(10), Duration.ofMinutes(30));
        agenda.addEvent(reunion2);
        
        List<Event> results = agenda.findByTitle("Réunion Lundi");
        assertEquals(2, results.size(), "Doit trouver les deux événements avec le titre 'Réunion Lundi'.");
        assertTrue(results.contains(simple2));
        assertTrue(results.contains(reunion2));
    }
    
    @Test
    public void testIsFreeFor_Collision() {
        // simple2: 9h00-10h00, simple3: 11h30-12h00. Le trou est 10h00-11h30.
        
        // 1. Collision (9h15-9h45 chevauche simple2)
        Event colliding = new Event("Collision", BASE_TIME_COLLISION.plusMinutes(15), Duration.ofMinutes(30));
        assertFalse(agenda.isFreeFor(colliding), "1. Ne doit pas être libre car chevauche l'événement de 9h00.");

        // 2. Espace libre (10h00 - 10h30, touche la fin de simple2 à 10h00)
        Event freeTouching = new Event("Libre", BASE_TIME_COLLISION.plusHours(1), Duration.ofMinutes(30));
        assertTrue(agenda.isFreeFor(freeTouching), "2. Doit être libre car commence exactement à la fin (10h00).");
        
        // 3. Espace libre (10h30 - 11h30 est libre, touche simple3 à 11h30)
        Event freeSpace = new Event("Libre", BASE_TIME_COLLISION.plusMinutes(90), Duration.ofMinutes(60));
        assertTrue(agenda.isFreeFor(freeSpace), "3. Doit être libre dans l'espace vide entre 10h00 et 11h30.");
        
        // 4. Collision sur la limite (11h29-11h59 chevauche simple3 à 11h30)
        Event marginalCollision = new Event("Marginal", BASE_TIME_COLLISION.plusMinutes(149), Duration.ofMinutes(30));
        assertFalse(agenda.isFreeFor(marginalCollision), "4. Ne doit pas être libre car chevauche l'événement de 11h30 (simple3).");
    }
}