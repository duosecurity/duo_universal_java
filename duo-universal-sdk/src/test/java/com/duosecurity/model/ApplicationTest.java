package com.duosecurity.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ApplicationTest {

    @Test
    void applications_with_different_destination_names_are_not_equal() {
        Application application = new Application("key", "name");
        application.setDestination_name("Acme Intranet");
        Application other = new Application("key", "name");
        other.setDestination_name("Acme Payroll");

        assertNotEquals(application, other);
    }

    @Test
    void applications_with_the_same_destination_name_are_equal() {
        Application application = new Application("key", "name");
        application.setDestination_name("Acme Intranet");
        Application other = new Application("key", "name");
        other.setDestination_name("Acme Intranet");

        assertEquals(application, other);
        // Equal objects are required to agree on hashCode; unequal ones are not required to
        // disagree, so there is no matching assertion in the test above.
        assertEquals(application.hashCode(), other.hashCode());
    }
}
