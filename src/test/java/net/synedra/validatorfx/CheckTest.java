package net.synedra.validatorfx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/** CheckTest tests Check.
 * @author r.lichtenberger@synedra.com
 */
@ExtendWith(ApplicationExtension.class)
public class CheckTest {
	
	private static String WARNING = "This is a warning.";
	
	@Test
	public void testCheckMethod() {
		Check c = new Check()
			.withMethod(this::check1);		
		c.recheck();
		List<ValidationMessage> messages = c.getValidationResult().getMessages();
		assertEquals(1, messages.size());
		assertEquals(WARNING, messages.get(0).getText());
		assertEquals(Severity.WARNING, messages.get(0).getSeverity());
	}
	
	private void check1(Check c) {
		c.warn(WARNING);
	}
	
	@Test
	public void testDependsOn() {
		StringProperty text = new SimpleStringProperty("foo"); 
		Check c = new Check()
			.withMethod(this::check2)
			.dependsOn("content", text)
			.install();
		
		WaitForAsyncUtils.waitForFxEvents(); // .install() will call the initial update delayed, so we have to wait 
		
		List<ValidationMessage> messages = c.getValidationResult().getMessages();
		assertEquals(1, messages.size());
		assertEquals("Must not be foo", messages.get(0).getText());
		assertEquals(Severity.ERROR, messages.get(0).getSeverity());
		
		text.set("bar");
		messages = c.getValidationResult().getMessages();
		assertEquals(1, messages.size());
		assertEquals("Must not be bar", messages.get(0).getText());
		assertEquals(Severity.ERROR, messages.get(0).getSeverity());
	}
	
	private void check2(Check c) {
		c.error("Must not be " + c.get("content"));
	}
	
	
}
