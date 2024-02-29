package net.synedra.validatorfx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/** CheckTest tests Check.
 * @author r.lichtenberger@synedra.com
 */
@ExtendWith(ApplicationExtension.class)
class CheckTest {
	
	private static final String WARNING = "This is a warning.";

	private TextField textfield;

	@Start
	private void setupScene(Stage stage) {
        textfield = new TextField();
        stage.setScene(new Scene(new StackPane(textfield), 100, 100));
        stage.show();
	}

	@Test
	void testCheckMethod() {
		Check c = new Check()
			.withMethod(this::check1);		
		c.recheck();
		
		checkMessage(c, Severity.WARNING, WARNING);
	}
	
	private void check1(Check.Context c) {
		c.warn(WARNING);
	}
	
	@Test
	void testDependsOn() {
		StringProperty text = new SimpleStringProperty("foo"); 
		Check c = new Check()
			.withMethod(this::check2)
			.dependsOn("content", text)
			.immediate();
		
		WaitForAsyncUtils.waitForFxEvents(); // .immediate() will call the initial update delayed, so we have to wait 
		
		checkMessage(c, Severity.ERROR, "Must not be foo");
		
		text.set("bar");
		checkMessage(c, Severity.ERROR, "Must not be bar");
	}
	
	@Test
	void testImmediateClear() {
		StringProperty text = new SimpleStringProperty("foo"); 
		Check c = new Check()
			.withMethod(this::check2)
			.dependsOn("content", text)
			.immediateClear();
				
		checkNoMessage(c);
		
		c.recheck();
		checkMessage(c, Severity.ERROR, "Must not be foo");
		
		text.set("bar");
		checkNoMessage(c);
		
		c.recheck();
		checkMessage(c, Severity.ERROR, "Must not be bar");
	}

	private void check2(Check.Context c) {
		c.error("Must not be " + c.get("content"));
	}

	@Test
	void testMultipleMethods() {
		StringProperty text = new SimpleStringProperty("foo");
		Check c = new Check()
				.withMethod(this::mustNotBeEmpty)
				.withMethod(this::checkLength)
				.dependsOn("text", text);

		checkNoMessage(c);
		text.set("  ");
		c.recheck();
		checkMessage(c, Severity.ERROR, "Cannot be empty");
		text.set("12345678901");
		c.recheck();
		checkMessage(c, Severity.ERROR, "Too long");
		text.set("           ");
		c.recheck();
		checkMessage(c, Severity.ERROR, "Cannot be empty", "Too long");
	}

	private void mustNotBeEmpty(Check.Context c) {
		String text = c.get("text");
		if (text.trim().isEmpty()) {
			c.error("Cannot be empty");
		}
	}

	private void checkLength(Check.Context c) {
		String text = c.get("text");
		if (text.length() > 10) {
			c.error("Too long");
		}
	}

	@Test
	void testTextFieldMaxLength(FxRobot robot) {
		Check c = new Check()
			.withMethod(this::check3)
			.dependsOn("content", textfield.textProperty())
			.decorates(textfield)
			.immediate()
		;
		WaitForAsyncUtils.waitForFxEvents(); // .immediate() will call the initial update delayed, so we have to wait 
		assertEquals(0, c.getValidationResult().getMessages().size());
		
		robot.clickOn(".text-field");
		robot.type(KeyCode.A, 4);
		checkMessage(c, Severity.WARNING, "Pretty long");

		robot.type(KeyCode.A, 2);
		checkMessage(c, Severity.ERROR, "Too long");
	}
	
	private void check3(Check.Context c) {
		String text = c.get("content");
		if (text.length() > 5) {
			c.error("Too long");
		} else if (text.length() > 3) {
			c.warn("Pretty long");
		}
	}		
	
	private void checkMessage(Check c, Severity severity, String ... texts) {
		List<ValidationMessage> messages = c.getValidationResult().getMessages();
		assertEquals(texts.length, messages.size());
		for (int i = 0; i < texts.length; i++) {
			assertEquals(texts[i], messages.get(i).getText());
			assertEquals(severity, messages.get(i).getSeverity());

		}
	}
	
	private void checkNoMessage(Check c) {
		List<ValidationMessage> messages = c.getValidationResult().getMessages();
		assertEquals(0, messages.size());
	}
	
}
