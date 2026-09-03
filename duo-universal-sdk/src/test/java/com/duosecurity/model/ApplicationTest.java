package com.duosecurity.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationTest {

    @Test
    void applications_with_different_destination_names_are_not_equal() {
        Application application = new Application("key", "name");
        application.setDestination_name("Acme Intranet");
        Application other = new Application("key", "name");
        other.setDestination_name("Acme Payroll");

        assertNotEquals(application, other);
        assertNotEquals(application.hashCode(), other.hashCode());
    }

    @Test
    void applications_with_the_same_destination_name_are_equal() {
        Application application = new Application("key", "name");
        application.setDestination_name("Acme Intranet");
        Application other = new Application("key", "name");
        other.setDestination_name("Acme Intranet");

        assertEquals(application, other);
        assertEquals(application.hashCode(), other.hashCode());
    }

    @Test
    void toString_includes_destination_name() {
        Application application = new Application("key", "name");
        application.setDestination_name("Acme Intranet");

        assertTrue(application.toString().contains("destination_name=Acme Intranet"));
    }
}
