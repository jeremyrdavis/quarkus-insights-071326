package io.arrogantprogrammer.quarkusinsights.cfp.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmailAddressTest {

    @Test
    void testValidEmail() {
        EmailAddress email = new EmailAddress("test@example.com");
        assertEquals("test@example.com", email.address());
    }

    @Test
    void testInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> new EmailAddress("invalid-email"));
    }

    @Test
    void testNullEmail() {
        assertThrows(NullPointerException.class, () -> new EmailAddress(null));
    }

    @Test
    void testBlankEmail() {
        assertThrows(IllegalArgumentException.class, () -> new EmailAddress("   "));
    }

    @Test
    void testEquality() {
        EmailAddress email1 = new EmailAddress("test@example.com");
        EmailAddress email2 = new EmailAddress("test@example.com");
        assertEquals(email1, email2);
    }
}
