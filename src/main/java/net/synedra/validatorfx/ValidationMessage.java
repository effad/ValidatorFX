package net.synedra.validatorfx;

public class ValidationMessage {
	private String text;
	private Severity severity;
	
	public ValidationMessage(Severity severity, String text) {
		super();
		this.severity = severity;
		this.text = text;
	}

	public Severity getSeverity() {
		return severity;
	}

	public String getText() {
		return text;
	}
}
