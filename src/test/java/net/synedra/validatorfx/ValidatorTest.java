package net.synedra.validatorfx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** ValidatorTest tests Validator.
 * @author r.lichtenberger@synedra.com
 */
@ExtendWith(ApplicationExtension.class)
public class ValidatorTest extends TestBase {
	
	private VBox root;

	@Start
	private void setupScene(Stage stage) {
		root = new VBox();
        stage.setScene(new Scene(root, 640, 400));
        stage.show();		
	}

	@BeforeEach
	public void resetRoot() {
		fx(root.getChildren()::clear);
	}
	
	@Test
	public void testTextFieldWithTwoChecks(FxRobot robot) {
		TextField textfield = new TextField();
		fx(() -> root.getChildren().add(textfield));
		
		Validator validator = new Validator();
		Check c1 = validator.createCheck()
			.withMethod(this::maxSize)
			.dependsOn("content", textfield.textProperty())
			.decorates(textfield)
			.install()
		;
		Check c2 = validator.createCheck()
			.withMethod(this::noVowels)
			.dependsOn("content", textfield.textProperty())
			.decorates(textfield)
			.install()
		;
		
		WaitForAsyncUtils.waitForFxEvents(); // .install() will call the initial update delayed, so we have to wait 
		assertEquals(0, validator.getValidationResult().getMessages().size());
		
		robot.clickOn(".text-field");
		robot.type(KeyCode.A, 1);
		checkMessage(validator, Severity.ERROR, "Txt cntns vwls");

		robot.type(KeyCode.A, 6);
		checkMessage(validator, Severity.ERROR, "Too long", Severity.ERROR, "Txt cntns vwls");
		
		validator.remove(c1);
		checkMessage(validator, Severity.ERROR, "Txt cntns vwls");
		
		validator.remove(c2);
		assertEquals(0, validator.getValidationResult().getMessages().size());		
	}
	
	private void maxSize(Check c) {
		String text = c.get("content");
		if (text.length() > 5) {
			c.error("Too long");
		}
	}		
	
	private void noVowels(Check c) {
		String text = c.get("content");
		if (text.matches(".*[aeiouAEIOU].*")) {
			c.error("Txt cntns vwls");
		}
	}		
	
	private void checkMessage(Validator validator, Severity severity, String text) {
		List<ValidationMessage> messages = validator.getValidationResult().getMessages();
		assertEquals(1, messages.size());
		assertEquals(text, messages.get(0).getText());
		assertEquals(severity, messages.get(0).getSeverity());		
	}
	
	private void checkMessage(Validator validator, Severity severity, String text, Severity severity2, String text2) {
		List<ValidationMessage> messages = validator.getValidationResult().getMessages();
		assertEquals(2, messages.size());
		assertEquals(text, messages.get(0).getText());
		assertEquals(severity, messages.get(0).getSeverity());		
		assertEquals(text2, messages.get(1).getText());
		assertEquals(severity2, messages.get(1).getSeverity());		
	}
	
}
