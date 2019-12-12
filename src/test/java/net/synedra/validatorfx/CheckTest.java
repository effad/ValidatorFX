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
public class CheckTest {
	
	private static String WARNING = "This is a warning.";
	
	private TextField textfield;

	@Start
	private void setupScene(Stage stage) {
        textfield = new TextField();
        stage.setScene(new Scene(new StackPane(textfield), 100, 100));
        stage.show();		
	}

	@Test
	public void testCheckMethod() {
		Check c = new Check()
			.withMethod(this::check1);		
		c.recheck();
		
		checkMessage(c, Severity.WARNING, WARNING);
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
			.immediate();
		
		WaitForAsyncUtils.waitForFxEvents(); // .install() will call the initial update delayed, so we have to wait 
		
		checkMessage(c, Severity.ERROR, "Must not be foo");
		
		text.set("bar");
		checkMessage(c, Severity.ERROR, "Must not be bar");
	}
	
	private void check2(Check c) {
		c.error("Must not be " + c.get("content"));
	}
	
	@Test
	public void testTextFieldMaxLength(FxRobot robot) {
		Check c = new Check()
			.withMethod(this::check3)
			.dependsOn("content", textfield.textProperty())
			.decorates(textfield)
			.immediate()
		;
		WaitForAsyncUtils.waitForFxEvents(); // .install() will call the initial update delayed, so we have to wait 
		assertEquals(0, c.getValidationResult().getMessages().size());
		
		robot.clickOn(".text-field");
		robot.type(KeyCode.A, 4);
		checkMessage(c, Severity.WARNING, "Pretty long");

		robot.type(KeyCode.A, 2);
		checkMessage(c, Severity.ERROR, "Too long");
	}
	
	private void check3(Check c) {
		String text = c.get("content");
		if (text.length() > 5) {
			c.error("Too long");
		} else if (text.length() > 3) {
			c.warn("Pretty long");
		}
	}		
	
	private void checkMessage(Check c, Severity severity, String text) {
		List<ValidationMessage> messages = c.getValidationResult().getMessages();
		assertEquals(1, messages.size());
		assertEquals(text, messages.get(0).getText());
		assertEquals(severity, messages.get(0).getSeverity());		
	}
}
