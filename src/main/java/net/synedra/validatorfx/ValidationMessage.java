package net.synedra.validatorfx;

/** A validation message represents the description of a single problem.
 * @author r.lichtenberger@synedra.com
 */
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((severity == null) ? 0 : severity.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValidationMessage other = (ValidationMessage) obj;
		if (severity != other.severity)
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}

	
}
