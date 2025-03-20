package net.synedra.validatorfx;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ApplicationExtension.class)
public class DefaultChecksTest extends TestBase {
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
    void testCreateNonNullCheck() {
        ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableList(List.of("Test1", "Test2", "Test3")));
        Validator validator = new Validator();
        validator.defaultChecks().createNonNullCheck(comboBox.getSelectionModel().selectedItemProperty(), "selectedItem", Severity.ERROR).explicit();
        validator.validate();
        checkMessage(validator, "selectedItem mustn't be null");
        comboBox.getSelectionModel().select(0);
        validator.validate();
        checkNoMessages(validator);
    }

    @Test
    void testCreateNonBlankCheck() {
        TextField textfield = new TextField();
        Validator validator = new Validator();
        validator.defaultChecks().createNonBlankCheck(textfield.textProperty(), "text", Severity.ERROR).explicit();
        validator.validate();
        checkMessage(validator, "text is required to not be blank");
        textfield.setText("Test");
        validator.validate();
        checkNoMessages(validator);
    }

    @Test
    void testCreateMinimumLengthCheck() {
        TextField textfield = new TextField();
        Validator validator = new Validator();
        validator.defaultChecks().createMinimumLengthCheck(textfield.textProperty(), "text", Severity.ERROR, 3).explicit();
        validator.validate();
        checkMessage(validator, "text with value of [''] should be at least 3 characters long");
        textfield.setText("Test");
        validator.validate();
        checkNoMessages(validator);
    }

    @Test
    void testCreateMaximumLengthCheck() {
        TextField textfield = new TextField();
        Validator validator = new Validator();
        validator.defaultChecks().createMaximumLengthCheck(textfield.textProperty(), "text", Severity.ERROR, 3).explicit();
        validator.validate();
        checkNoMessages(validator);
        textfield.setText("Test");
        validator.validate();
        checkMessage(validator, "text with value of ['Test'] should be at most 3 characters long");
    }

    @Test
    void testCreateIsMappableToCheck() {
        TextField textField = new TextField();
        Validator validator = new Validator();
        validator.defaultChecks().createIsMappableToCheck(textField.textProperty(), "text", Severity.ERROR, string -> {
            try {
                return Optional.of(Integer.parseInt(string));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }).explicit();
        validator.validate();
        checkMessage(validator, "text is not assignable");
        textField.setText("12");
        validator.validate();
        checkNoMessages(validator);
    }

    @Test
    void testCreateIsNumberCheck() {
        TextField textField = new TextField();
        Validator validator = new Validator();
        validator.defaultChecks().createIsNumberCheck(textField.textProperty(), "text", Severity.ERROR).explicit();
        validator.validate();
        checkMessage(validator, "text is not a number");
        textField.setText("Test");
        validator.validate();
        checkMessage(validator, "text is not a number");
        textField.setText("12.1");
        validator.validate();
        checkNoMessages(validator);
    }

    @Test
    void testCreateIsNumberWithinBoundsCheck() {
        TextField textField = new TextField();
        Validator validator = new Validator();
        validator.defaultChecks().createIsNumberWithinBoundsCheck(textField.textProperty(), "text", Severity.ERROR, 12.0, 20.0).explicit();
        validator.validate();
        checkMessage(validator, "text['NAN'] is not between 12.00 and 20.00");
        textField.setText("Test");
        validator.validate();
        checkMessage(validator, "text['NAN'] is not between 12.00 and 20.00");
        textField.setText("12.1");
        validator.validate();
        checkNoMessages(validator);
        textField.setText("20");
        validator.validate();
        checkNoMessages(validator);
        textField.setText("10");
        validator.validate();
        checkMessage(validator, "text['10.0'] is not between 12.00 and 20.00");
        textField.setText("22");
        validator.validate();
        checkMessage(validator, "text['22.0'] is not between 12.00 and 20.00");
    }

    @Test
    void testCreateMatchesRegexCheck() {
        TextField textField = new TextField();
        Validator validator = new Validator();
        validator.defaultChecks().createMatchesRegexCheck(textField.textProperty(), "text", Severity.ERROR, "[abc]{2}").explicit();
        validator.validate();
        checkMessage(validator, "text[''] does not match the regex ['[abc]{2}']");
        textField.setText("Test");
        validator.validate();
        checkMessage(validator, "text['Test'] does not match the regex ['[abc]{2}']");
        textField.setText("ab");
        validator.validate();
        checkNoMessages(validator);
    }

    private void checkMessage(Validator validator, String text) {
        List<ValidationMessage> messages = validator.getValidationResult().getMessages();
        assertEquals(1, messages.size());
        assertEquals(text, messages.get(0).getText());
        assertEquals(Severity.ERROR, messages.get(0).getSeverity());
    }

    private void checkNoMessages(Validator validator) {
        Assertions.assertEquals(0, validator.getValidationResult().getMessages().size());
    }
}
