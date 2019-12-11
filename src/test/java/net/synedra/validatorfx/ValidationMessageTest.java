package net.synedra.validatorfx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
	
	@Test
	public void testHashAndEquals() {
		ValidationMessage msg1 = new ValidationMessage(Severity.WARNING, "Test1");
		ValidationMessage msg2 = new ValidationMessage(Severity.WARNING, "Test1");
		ValidationMessage msg3 = new ValidationMessage(Severity.ERROR, "Test1");
		ValidationMessage msg4 = new ValidationMessage(Severity.WARNING, "Test2");
		ValidationMessage msg5 = new ValidationMessage(Severity.ERROR, "Test3");
		
		assertEquals(msg1, msg2);
		assertNotEquals(msg1, msg3);
		assertNotEquals(msg1, msg4);
		assertNotEquals(msg1, msg5);
		
		assertEquals(msg1.hashCode(), msg2.hashCode());
		assertNotEquals(msg1.hashCode(), msg3.hashCode());
		assertNotEquals(msg1.hashCode(), msg4.hashCode());
		assertNotEquals(msg1.hashCode(), msg5.hashCode());
	}
}
