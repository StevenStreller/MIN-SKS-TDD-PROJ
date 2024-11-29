package de.hsh;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void testMainMethodOutput() {
        // Umleitung von System.out auf ein ByteArrayOutputStream
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Aufruf der main-Methode
        Main.main(new String[]{});

        // Vergleichen des Ausgabewerts mit dem erwarteten Text
        assertEquals("Hello world!" + System.lineSeparator(), outContent.toString());
    }
}