package net.synedra.validatorfx;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static net.synedra.validatorfx.DefaultCheckMessages.CurrentCheckMessagesFactory.getDefaultCheckMessagesFactory;
import static net.synedra.validatorfx.DefaultCheckMessages.CurrentCheckMessagesFactory.setDefaultCheckMessagesFactory;

public class DefaultCheckMessagesTest {
    private final DefaultCheckMessages factory = getDefaultCheckMessagesFactory();

    @Test
    void testSetFactory() {
        Assertions.assertThrows(NullPointerException.class, () -> setDefaultCheckMessagesFactory(null));
        DefaultCheckMessages newFactory = new DefaultCheckMessages() {
        };
        setDefaultCheckMessagesFactory(newFactory);
        Assertions.assertEquals(newFactory, getDefaultCheckMessagesFactory());
    }

    @Test
    void testNotNullMessage() {
        Assertions.assertEquals("Test mustn't be null", factory.notNullMessage("Test"));
    }

    @Test
    void testNotBlankMessage() {
        Assertions.assertEquals("Test is required to not be blank", factory.notBlankMessage("Test"));
    }

    @Test
    void testMinimumLengthMessage() {
        Assertions.assertEquals("Test with value of ['tst'] should be at least 3 characters long", factory.minimumLengthMessage("Test", "tst", 3));
    }

    @Test
    void testMaximumLengthMessage() {
        Assertions.assertEquals("Test with value of ['tst'] should be at most 3 characters long", factory.maximumLengthMessage("Test", "tst", 3));
    }

    @Test
    void testIsAssignableMessage() {
        Assertions.assertEquals("Test is not assignable", factory.isAssignableMessage("Test"));
    }

    @Test
    void testIsNumberMessage() {
        Assertions.assertEquals("Test is not a number", factory.isNumberMessage("Test"));
    }

    @Test
    void testIsNumberWithinBoundsMessage() {
        Assertions.assertEquals("Test['tst'] is not between 3.00 and 5.00", factory.isNumberWithinBoundsMessage("Test", "tst", 3.0, 5.0));
    }

    @Test
    void testMatchesRegexMessage() {
        Assertions.assertEquals("Test['tst'] does not match the regex ['.*']", factory.matchesRegexMessage("Test", "tst", ".*"));
    }
}
