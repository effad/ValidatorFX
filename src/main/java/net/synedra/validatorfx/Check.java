package net.synedra.validatorfx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

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
	private List<Consumer<Context>> checkMethods = new ArrayList<>();
    private ReadOnlyObjectWrapper<ValidationResult> validationResultProperty = new ReadOnlyObjectWrapper<>();
	private ValidationResult nextValidationResult = new ValidationResult();
	private List<Node> targets = new ArrayList<>(1);
	private List<Decoration> decorations = new ArrayList<>();
	private Function<ValidationMessage, Decoration> decorationFactory;
	private ChangeListener<? super Object> immediateListener;
	private ChangeListener<? super Object> immediateClearListener;
	
	public class Context {
		
		private Context() { }
		
		/** Get the current value of a dependency.
		 * @param <T> The type the value should be casted into
		 * @param key The key the dependency has been given
		 * @return The current value of the given depency
		 */
		@SuppressWarnings("unchecked")
		public <T> T get(String key) {
			return (T) dependencies.get(key).getValue();
		}

		public Iterable<String> keys() {
			return dependencies.keySet();
		}

		/** Emit a warning.
		 * @param message The text to be presented to the user as warning message.
		 */
		public void warn(String message) {
			nextValidationResult.addWarning(message);
		}
		
		/** Emit an error.
		 * @param message The text to be presented to the user as error message.
		 */
		public void error(String message) {
			nextValidationResult.addError(message);
		}			
	}
	
	public Check() {
		validationResultProperty.set(new ValidationResult());
		decorationFactory = DefaultDecoration.getFactory();
	}
	
	/** Add a method to be called in order to perform this check.
	 * If called multiple times, multiple check methods can be registered.
	 * @param checkMethod The code to be called in order to check the validity of this Check.
	 */
	public Check withMethod(Consumer<Context> checkMethod) {
		checkMethods.add(checkMethod);
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
	 * This method must be called after setting dependencies.
	 */
	public Check immediate() {
		removeAllListeners();
		immediateListener = (obs, oldv, newv) -> recheck();
		for (ObservableValue<? extends Object> dependency : dependencies.values()) {
			dependency.addListener(immediateListener);
		}
		Platform.runLater(this::recheck);	// to circumvent problems with decoration pane vs. dialog
		return this;
	}
	
	
	/** Sets this check to be immediately cleared (but not rechecked) if one of its dependencies changes. 
	 * This method must be called after setting dependencies.
	 */
	public Check immediateClear() {
		removeAllListeners();
		immediateClearListener = (obs, oldv, newv) -> clear();
		for (ObservableValue<? extends Object> dependency : dependencies.values()) {
			dependency.addListener(immediateClearListener);
		}
		return this;
	}
	
	/** Sets this check to not be reevaluated automatically (i.e. recheck must be called explicetly).
	 * This mode is the default.
	 * This method must be called after setting dependencies.
	 */
	public Check explicit() {
		removeAllListeners();
		return this;
	}
	
	private void removeAllListeners() {
		removeListener(immediateListener);
		removeListener(immediateClearListener);
	}
	
	private void removeListener(ChangeListener<? super Object> listener) {
		if (listener != null) {
			for (ObservableValue<? extends Object> dependency : dependencies.values()) {
				dependency.removeListener(listener);
			}			
		}
	}
	
	/** Evaluate all dependencies and apply decorations of this check. You should not normally need to call this method directly. */
	public void recheck() {
		nextValidationResult = new ValidationResult();
		Context context = new Context();
		checkMethods.forEach(checkMethod -> checkMethod.accept(context));
		removeDecorations();
		for (Node target : targets) {
			for (ValidationMessage validationMessage : nextValidationResult.getMessages()) {
				Decoration decoration = decorationFactory.apply(validationMessage);
				decorations.add(decoration);
				decoration.add(target);				
			}
		}
		setNextValidationResult();
	}
	
	/** Clear this check, i.e. remove its decorations and set empty validation result. */
	public void clear() {
		removeDecorations();
		nextValidationResult = new ValidationResult();
		setNextValidationResult();
	}
	
	private void removeDecorations() {
		for (Node target : targets) {
			for (Decoration decoration : decorations) {
				decoration.remove(target);				
			}
		}
		decorations.clear();		
	}
	
	private void setNextValidationResult() {
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
}
