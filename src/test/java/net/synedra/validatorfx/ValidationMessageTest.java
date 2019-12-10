package net.synedra.validatorfx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ValidationMessageTest {

	@Test
	public void testMessage() {
		ValidationMessage msg = new ValidationMessage(Severity.WARNING, "Test1");
		assertEquals(Severity.WARNING, msg.getSeverity());
		assertEquals("Test1", msg.getText());
		
		msg = new ValidationMessage(Severity.ERROR, "Test2");
		assertEquals(Severity.ERROR, msg.getSeverity());
		assertEquals("Test2", msg.getText());		
	}
}
