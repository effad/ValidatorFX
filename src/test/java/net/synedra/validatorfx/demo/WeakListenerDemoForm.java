package net.synedra.validatorfx.demo;

import javafx.beans.value.ObservableIntegerValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import net.synedra.validatorfx.Validator;

/** This class simulates a (very simple) form that contains a validator and a check.
 * The expectation is that if no reference to a WeakListenerDemoForm instance exists, it will be garbage collected.
 */
class WeakListenerDemoForm {
	private Validator validator = new Validator();		
	private TextField textField = new TextField();
	WeakListenerDemoForm(ObservableIntegerValue allowedModulus) {
		VBox.setMargin(textField, new Insets(5));
		validator.createCheck()
			.dependsOn("username", textField.textProperty())
			.dependsOn("allowedModulus", allowedModulus)
			.withMethod(c -> {
				String userName = c.get("username");
				int allowed = c.get("allowedModulus");
				if (userName.length() % 2 != allowed) {
					c.error("Please use " + (allowed == 0 ? "even" : "odd") + " number of letters.");
				}
			})
			.decorates(textField)
			.immediate();
		;
	}

	Node getPresentation() {
		return textField;
	}
}