package tracker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void validateEmail() {
        assertTrue(Main.validateEmail("a@b.c"));
        assertTrue(Main.validateEmail("a-a.a@b-b.bb.cc"));
    }

    @Test
    void validateName() {
        assertTrue(Main.validateName("Hello"));
        assertTrue(Main.validateName("O'Neill"));
        assertTrue(Main.validateName("Hai-Hai"));
        assertFalse(Main.validateName("-asd"));
        assertFalse(Main.validateName("asd'"));
        assertFalse(Main.validateName("tür"));
        assertFalse(Main.validateName("He''llo"));
        assertFalse(Main.validateName("A"));
    }
}