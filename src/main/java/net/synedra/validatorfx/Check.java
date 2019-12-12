package net.synedra.validatorfx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.controlsfx.control.decoration.Decoration;
import org.controlsfx.control.decoration.Decorator;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

/** A check represents a check for validity in a form.
 * @author r.lichtenberger@synedra.com
 */
public class Check {
	
	private Map<String, ObservableValue<? extends Object>> dependencies = new HashMap<>(1);
	private Consumer<Check> checkMethod;
    private ReadOnlyObjectWrapper<ValidationResult> validationResultProperty = new ReadOnlyObjectWrapper<>();
	private ValidationResult nextValidationResult = new ValidationResult();
	private List<Node> targets = new ArrayList<>(1);
	private List<Decoration> decorations = new ArrayList<>();
	private Function<ValidationMessage, Decoration> decorationFactory;
	private ChangeListener<? super Object> dependencyListener;
	
	public Check() {
		validationResultProperty.set(new ValidationResult());
		decorationFactory = DefaultDecoration.getFactory();
		dependencyListener = (obs, oldv, newv) -> recheck();
	}
		
	public Check withMethod(Consumer<Check> checkMethod) {
		this.checkMethod = checkMethod;
		return this;
	}
	
	public Check dependsOn(String key, ObservableValue<? extends Object> dependency) {
		dependencies.put(key, dependency);
		return this;
	}
	
	public Check decorates(Node target) {
		targets.add(target);
		return this;
	}
	
	public Check decoratingWith(Function<ValidationMessage, Decoration> decorationFactory) {
		this.decorationFactory = decorationFactory;
		return this;
	}
	
	/** Sets this check to be immediately evaluated if one of its dependencies changes. 
	 * This method must be called last. 
	 */
	public Check immediate() {
		for (ObservableValue<? extends Object> dependency : dependencies.values()) {
			dependency.addListener(dependencyListener);
		}
		Platform.runLater(() -> recheck());	// to circumvent problems with decoration pane vs. dialog
		return this;
	}
	
	/** Evaluate all dependencies and apply decorations of this check. You should not normally need to call this method directly. */
	public void recheck() {
		nextValidationResult = new ValidationResult();
		checkMethod.accept(this);
		for (Node target : targets) {
			for (Decoration decoration : decorations) {
				Decorator.removeDecoration(target, decoration);				
			}
		}
		decorations.clear();
		for (Node target : targets) {
			for (ValidationMessage validationMessage : nextValidationResult.getMessages()) {
				Decoration decoration = decorationFactory.apply(validationMessage);
				decorations.add(decoration);
				Decorator.addDecoration(target, decoration);				
			}
		}
		if (!nextValidationResult.getMessages().equals(getValidationResult().getMessages())) {
			validationResultProperty.set(nextValidationResult);
		}
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
		
	/** Get the current value of a dependency.
	 * @param <T> The type the value should be casted into
	 * @param key The key the dependency has been given
	 * @return The current value of the given depency
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) dependencies.get(key).getValue();
	}

	// TODO :: move this method to a separate class to be passed into check methods
	public void warn(String message) {
		nextValidationResult.addWarning(message);
	}
	
	public void error(String message) {
		nextValidationResult.addError(message);
	}	
}
