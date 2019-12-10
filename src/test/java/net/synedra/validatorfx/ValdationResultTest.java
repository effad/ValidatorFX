package net.synedra.validatorfx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ValdationResultTest {

	@Test
	public void testSetup() {
		ValidationResult result = new ValidationResult();
		assertTrue(result.getMessages().isEmpty());
		result.addWarning("warn");
		assertFalse(result.getMessages().isEmpty());
		result.addError("err");
		assertEquals(Severity.WARNING, result.getMessages().get(0).getSeverity());
		assertEquals("warn", result.getMessages().get(0).getText());
		assertEquals(Severity.ERROR, result.getMessages().get(1).getSeverity());
		assertEquals("err", result.getMessages().get(1).getText());
	}
	
	@Test
	public void testAddAll() {
		ValidationResult result1 = new ValidationResult();
		ValidationResult result2 = new ValidationResult();
		result1.addWarning("warn");
		result2.addError("err");
		ValidationResult result = new ValidationResult();
		result.addAll(result1.getMessages());
		result.addAll(result2.getMessages());
		assertEquals(Severity.WARNING, result.getMessages().get(0).getSeverity());
		assertEquals("warn", result.getMessages().get(0).getText());
		assertEquals(Severity.ERROR, result.getMessages().get(1).getSeverity());
		assertEquals("err", result.getMessages().get(1).getText());
	}
	
}
