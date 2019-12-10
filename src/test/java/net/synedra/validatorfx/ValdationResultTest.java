package net.synedra.validatorfx;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ValdationResultTest {

	@Test
	public void testSetup() {
		ValidationResult result = new ValidationResult();
		Assert.assertTrue(result.getMessages().isEmpty());
		result.addWarning("warn");
		Assert.assertFalse(result.getMessages().isEmpty());
		result.addError("err");
		Assert.assertEquals(result.getMessages().get(0).getSeverity(), Severity.WARNING);
		Assert.assertEquals(result.getMessages().get(0).getText(), "warn");
		Assert.assertEquals(result.getMessages().get(1).getSeverity(), Severity.ERROR);
		Assert.assertEquals(result.getMessages().get(1).getText(), "err");
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
		Assert.assertEquals(result.getMessages().get(0).getSeverity(), Severity.WARNING);
		Assert.assertEquals(result.getMessages().get(0).getText(), "warn");
		Assert.assertEquals(result.getMessages().get(1).getSeverity(), Severity.ERROR);
		Assert.assertEquals(result.getMessages().get(1).getText(), "err");
	}
	
}
