package org.apache.syncope.core.spring.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Sanity check per verificare che l'ambiente di test sia configurato correttamente.
 * - JUnit 5 funziona
 * - La classe DefaultPasswordGenerator è accessibile
 * - Il metodo protected merge() è visibile (stesso package)
 */
public class SanityCheckTest {

    @Test
    void testJUnitWorks() {
        assertTrue(true, "JUnit 5 funziona!");
    }

    @Test
    void testDefaultPasswordGeneratorAccessible() {
        // Verifica che la classe sia accessibile e istanziabile
        DefaultPasswordGenerator generator = new DefaultPasswordGenerator();
        assertNotNull(generator, "DefaultPasswordGenerator istanziato correttamente");
    }

    @Test
    void testMergeMethodVisible() {
        // Verifica che il metodo protected merge() sia visibile nello stesso package
        DefaultPasswordGenerator generator = new DefaultPasswordGenerator();

        // Non importa cosa passiamo, l'importante è che il metodo sia visibile
        // e che la chiamata compili. Passiamo null per testare solo la visibilità.
        // Nota: se il metodo lancia eccezione per null, va bene – stiamo testando la visibilità, non la logica.
        try {
            generator.merge(null);
        } catch (Exception e) {
            // Se lancia eccezione, va bene – significa che il metodo è visibile!
            System.out.println("merge() è visibile (ha lanciato: " + e.getClass().getSimpleName() + ")");
        }
    }
}