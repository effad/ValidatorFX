package net.synedra.validatorfx;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/** CheckTest tests Check.
 * @author r.lichtenberger@synedra.com
 */
public class CheckTest {
	
	private static String WARNING = "This is a warning.";
	
	@Test
	public void testCheckMethod() {
		Check c = new Check()
			.withMethod(this::check1);		
		c.recheck();
		List<ValidationMessage> messages = c.getValidationResult().getMessages();
		Assert.assertEquals(messages.size(), 1);
		Assert.assertEquals(messages.get(0).getText(), WARNING);
		Assert.assertEquals(messages.get(0).getSeverity(), Severity.WARNING);
	}
	
	private void check1(Check c) {
		c.warn(WARNING);
	}
	
//	@Test
	public void testDependsOn() {
		StringProperty text = new SimpleStringProperty("foo"); 
		Check c = new Check()
			.withMethod(this::check2)
			.dependsOn("content", text)
			.install();
		
		List<ValidationMessage> messages = c.getValidationResult().getMessages();
		Assert.assertEquals(messages.size(), 1);
		Assert.assertEquals(messages.get(0).getText(), "Must not be foo");
		Assert.assertEquals(messages.get(0).getSeverity(), Severity.ERROR);
		
		text.set("bar");
		messages = c.getValidationResult().getMessages();
		Assert.assertEquals(messages.size(), 1);
		Assert.assertEquals(messages.get(0).getText(), "Must not be bar");
		Assert.assertEquals(messages.get(0).getSeverity(), Severity.ERROR);		
	}
	
	private void check2(Check c) {
		c.error("Must not be " + c.get("content"));
	}
	
	
}
