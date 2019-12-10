package net.synedra.validatorfx;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ValidationMessageTest {

	@Test
	public void testMessage() {
		ValidationMessage msg = new ValidationMessage(Severity.WARNING, "Test1");
		Assert.assertEquals(msg.getSeverity(), Severity.WARNING);
		Assert.assertEquals(msg.getText(), "Test1");
		
		msg = new ValidationMessage(Severity.ERROR, "Test2");
		Assert.assertEquals(msg.getSeverity(), Severity.ERROR);
		Assert.assertEquals(msg.getText(), "Test2");		
	}
}
