package net.synedra.validatorfx;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
	
	private List<ValidationMessage> messages = new ArrayList<>();

	public List<ValidationMessage> getMessages() {
		return messages;
	}
	
	public void addWarning(String text) {
		messages.add(new ValidationMessage(Severity.WARNING, text));
	}

	public void addError(String text) {
		messages.add(new ValidationMessage(Severity.ERROR, text));
	}

	public void addAll(List<ValidationMessage> messages) {
		this.messages.addAll(messages);
	}
}
