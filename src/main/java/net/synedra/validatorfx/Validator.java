package net.synedra.validatorfx;

import java.util.LinkedHashMap;
import java.util.Map;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;

/** A Validator collects several checks and sums up their ValidationResults.
 * @author r.lichtenberger@synedra.com
 */
public class Validator {

    private Map<Check, ChangeListener<ValidationResult>> checks = new LinkedHashMap<>();
    private ReadOnlyObjectWrapper<ValidationResult> validationResultProperty = new ReadOnlyObjectWrapper<>(new ValidationResult());
    private ReadOnlyBooleanWrapper containsWarningsProperty = new ReadOnlyBooleanWrapper();
    private ReadOnlyBooleanWrapper containsErrorsProperty = new ReadOnlyBooleanWrapper();
	
    /** Create a check that lives within this checker's domain.
     * @return A check object whose dependsOn, decorates, etc. methods can be called
     */
    public Check createCheck() {
    	Check check = new Check();
    	add(check);
    	return check;
    }
    
    /** Add another check to the checker. Changes in the check's validationResultProperty will be reflected in the checker.
     * @param check The check to add.
     */
	public void add(Check check) {		
		ChangeListener<ValidationResult> listener = (obs, oldv, newv) -> refreshProperties();
		checks.put(check, listener);
		check.validationResultProperty().addListener(listener);
	}
	
	public void remove(Check check) {
		ChangeListener<ValidationResult> listener = checks.remove(check);
		if (listener != null) {
			check.validationResultProperty().removeListener(listener);
		}
		refreshProperties();
	}	
		
	/** Retrieves current validation result
	 * @return validation result
	 */
	public ValidationResult getValidationResult() {
	    return validationResultProperty.get();
	}

	/** Can be used to track validation result changes 
	 * @return The Validation result property.
	 */
	public ReadOnlyObjectProperty<ValidationResult> validationResultProperty() {
	    return validationResultProperty.getReadOnlyProperty();
	}
	
	public ReadOnlyBooleanProperty containsWarningsProperty() {
		return containsWarningsProperty.getReadOnlyProperty();
	}
	
	public boolean containsWarnings() {
		return containsWarningsProperty().get();
	}
	
	public ReadOnlyBooleanProperty containsErrorsProperty() {
		return containsErrorsProperty.getReadOnlyProperty();
	}
	
	public boolean containsErrors() {
		return containsErrorsProperty().get();
	}

	private void refreshProperties() {
		ValidationResult nextResult = new ValidationResult();
		for (Check check : checks.keySet()) {
			nextResult.addAll(check.getValidationResult().getMessages());
		}
		validationResultProperty.set(nextResult);
		boolean hasWarnings = false;
		boolean hasErrors = false;
		for (ValidationMessage msg : nextResult.getMessages()) {
			hasWarnings = hasWarnings || msg.getSeverity() == Severity.WARNING; 
			hasErrors = hasErrors || msg.getSeverity() == Severity.ERROR; 
		}
		containsWarningsProperty.set(hasWarnings);
		containsErrorsProperty.set(hasErrors);
	}

}
