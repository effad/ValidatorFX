package net.synedra.validatorfx;

import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

import static net.synedra.validatorfx.DefaultCheckMessages.CurrentCheckMessagesFactory.getDefaultCheckMessagesFactory;

/**
 * Default checks available to setup standard validation methods easily. The messages for failed validation will be created by calling the respective methods in {@link DefaultCheckMessages}
 */
public class DefaultChecks {
    private final Validator validator;

    DefaultChecks(Validator validator) {
        this.validator = validator;
    }

    /**
     * Create a check that fails validation when the value of the passed property is null
     *
     * @param property  whose value shouldn't be null
     * @param severity  of the validation
     * @param decorated the node that should be decorated by this check
     * @param <T>       of the passed property
     * @return self for methodChaining
     */
    public <T> Check createNonNullCheck(ReadOnlyProperty<T> property, Severity severity, Node decorated) {
        Function<T, String> messageCreator = t -> getDefaultCheckMessagesFactory().notNullMessage(property.getName());
        Predicate<T> notNull = Objects::isNull;
        return createCheck(property, severity, decorated, messageCreator, notNull);
    }

    /**
     * Create a check that fails validation when the value of the passed property is null
     *
     * @param property whose value shouldn't be null
     * @param severity of the validation
     * @param <T>      of the passed property
     * @return self for methodChaining
     */
    public <T> Check createNonNullCheck(ReadOnlyProperty<T> property, Severity severity) {
        return createNonNullCheck(property, severity, null);
    }

    /**
     * Create a check that fails validation when the string-value of the passed property is blank
     *
     * @param property  whose value shouldn't be blank
     * @param severity  of the validation
     * @param decorated the node that should be decorated by this check
     * @return self for methodChaining
     */
    public Check createNonBlankCheck(StringProperty property, Severity severity, Node decorated) {
        Function<String, String> messageCreator = string -> getDefaultCheckMessagesFactory().notBlankMessage(property.getName());
        Predicate<String> isBlank = String::isBlank;
        return createCheck(property, severity, decorated, messageCreator, isBlank);
    }

    /**
     * Create a check that fails validation when the string-value of the passed property is blank
     *
     * @param property whose value shouldn't be blank
     * @param severity of the validation
     * @return self for methodChaining
     */
    public Check createNonBlankCheck(StringProperty property, Severity severity) {
        return createNonBlankCheck(property, severity, null);
    }

    /**
     * Create a check that fails validation when the string-value of the passed property is shorter than the required minimum length
     *
     * @param property      whose value shouldn't be shorter than the minimum length
     * @param severity      of the validation
     * @param decorated     the node that should be decorated by this check
     * @param minimumLength the required minimum length of the string
     * @return self for methodChaining
     */
    public Check createMinimumLengthCheck(StringProperty property, Severity severity, Node decorated, int minimumLength) {
        Function<String, String> messageCreator = string -> getDefaultCheckMessagesFactory().minimumLengthMessage(property.getName(), property.get(), minimumLength);
        Predicate<String> lengthCheck = string -> string != null && string.length() < minimumLength;
        return createCheck(property, severity, decorated, messageCreator, lengthCheck);
    }

    /**
     * Create a check that fails validation when the string-value of the passed property is shorter than the required minimum length
     *
     * @param property      whose value shouldn't be shorter than the minimum length
     * @param severity      of the validation
     * @param minimumLength the required minimum length of the string
     * @return self for methodChaining
     */
    public Check createMinimumLengthCheck(StringProperty property, Severity severity, int minimumLength) {
        return createMinimumLengthCheck(property, severity, null, minimumLength);
    }

    /**
     * Create a check that fails validation when the string-value of the passed property is longer than the required maximum length
     *
     * @param property      whose value shouldn't be longer than the maximum length
     * @param severity      of the validation
     * @param decorated     the node that should be decorated by this check
     * @param maximumLength the required maximum length of the string
     * @return self for methodChaining
     */
    public Check createMaximumLengthCheck(StringProperty property, Severity severity, Node decorated, int maximumLength) {
        Function<String, String> messageCreator = string -> getDefaultCheckMessagesFactory().maximumLengthMessage(property.getName(), property.get(), maximumLength);
        Predicate<String> lengthCheck = string -> string != null && string.length() > maximumLength;
        return createCheck(property, severity, decorated, messageCreator, lengthCheck);
    }

    /**
     * Create a check that fails validation when the string-value of the passed property is longer than the required maximum length
     *
     * @param property      whose value shouldn't be longer than the maximum length
     * @param severity      of the validation
     * @param maximumLength the required maximum length of the string
     * @return self for methodChaining
     */
    public Check createMaximumLengthCheck(StringProperty property, Severity severity, int maximumLength) {
        return createMaximumLengthCheck(property, severity, null, maximumLength);
    }

    /**
     * Create a check that fails validation when the string-value of the passed property cannot be assigned to a value of T
     *
     * @param property       whose value should be assigned to t
     * @param severity       of the validation
     * @param decorated      the node that should be decorated by this check
     * @param mapper         the function that assigns a string value to an Optional<T>
     * @param messageCreator the function that provides the message for a failed validation
     * @param <T>            type to assign the value of property to
     * @return self for methodChaining
     */
    public <T> Check createIsAssignableToCheck(StringProperty property, Severity severity, Node decorated, Function<String, Optional<T>> mapper, Function<String, String> messageCreator) {
        Function<String, String> alternativeMessageCreator = string -> getDefaultCheckMessagesFactory().isAssignableMessage(property.getName());
        Predicate<String> instanceCheck = string -> mapper.apply(string).isEmpty();
        return createCheck(property, severity, decorated, messageCreator == null ? alternativeMessageCreator : messageCreator, instanceCheck);
    }

    /**
     * Create a check that fails validation when the string-value of the passed property cannot be assigned to a value of T
     *
     * @param property       whose value should be assigned to t
     * @param severity       of the validation
     * @param mapper         the function that assigns a string value to an Optional<T>
     * @param messageCreator the function that provides the message for a failed validation
     * @param <T>            type to assign the value of property to
     * @return self for methodChaining
     */
    public <T> Check createIsAssignableToCheck(StringProperty property, Severity severity, Function<String, Optional<T>> mapper, Function<String, String> messageCreator) {
        return createIsAssignableToCheck(property, severity, null, mapper, messageCreator);
    }

    /**
     * Create a check that fails validation when the string-value of the passed property cannot be assigned to a value of T.
     * Use the default message creator
     *
     * @param property whose value should be assigned to t
     * @param severity of the validation
     * @param mapper   the function that assigns a string value to an Optional<T>
     * @param <T>      type to assign the value of property to
     * @return self for methodChaining
     */
    public <T> Check createIsAssignableToCheck(StringProperty property, Severity severity, Function<String, Optional<T>> mapper) {
        return createIsAssignableToCheck(property, severity, mapper, null);
    }

    /**
     * Create a check that fails validation when the string value of the passed property cannot be cast to a number.
     *
     * @param property  whose value should be cast to a number
     * @param severity  of the validation
     * @param decorated the node that should be decorated by this check
     * @return self for methodChaining
     */
    public Check createIsNumberCheck(StringProperty property, Severity severity, Node decorated) {
        Function<String, String> messageCreator = string -> getDefaultCheckMessagesFactory().isNumberMessage(property.getName());
        Function<String, Optional<Double>> isNumberCheck = string -> Optional.ofNullable(asNumber(string));
        return createIsAssignableToCheck(property, severity, decorated, isNumberCheck, messageCreator);
    }

    /**
     * Create a check that fails validation when the string value of the passed property cannot be cast to a number.
     *
     * @param property whose value should be cast to a number
     * @param severity of the validation
     * @return self for methodChaining
     */
    public Check createIsNumberCheck(StringProperty property, Severity severity) {
        return createIsNumberCheck(property, severity, null);
    }

    /**
     * Create a check that fails validation when the number value of the passed property is not between two bounds.
     *
     * @param property  whose value should be between the bounds
     * @param severity  of the validation
     * @param decorated the node that should be decorated by this check
     * @param minimum   the lower bound of the validation (inclusive)
     * @param maximum   the upper bound of the validation (inclusive)
     * @return self for methodChaining
     */
    public Check createIsNumberWithinBoundsCheck(StringProperty property, Severity severity, Node decorated, double minimum, double maximum) {
        Function<String, Optional<Double>> isNumberWithinBoundsCheck = string -> Optional.ofNullable(asNumber(string)).filter(d -> d >= minimum && d <= maximum);
        Function<String, String> messageCreator = string -> getDefaultCheckMessagesFactory().isNumberWithinBoundsMessage(property.getName(), Optional.ofNullable(asNumber(string)).map(String::valueOf).orElse("NAN"), minimum, maximum);
        return createIsAssignableToCheck(property, severity, decorated, isNumberWithinBoundsCheck, messageCreator);
    }

    /**
     * Create a check that fails validation when the number value of the passed property is not between two bounds.
     *
     * @param property whose value should be between the bounds
     * @param severity of the validation
     * @param minimum  the lower bound of the validation (inclusive)
     * @param maximum  the upper bound of the validation (inclusive)
     * @return self for methodChaining
     */
    public Check createIsNumberWithinBoundsCheck(StringProperty property, Severity severity, double minimum, double maximum) {
        return createIsNumberWithinBoundsCheck(property, severity, null, minimum, maximum);
    }

    /**
     * Create a check that fails validation when the string value of the passed property does not match a regex.
     *
     * @param property  whose value should match the regex
     * @param severity  of the validation
     * @param decorated the node that should be decorated by this check
     * @param regex     that the property-value should match
     * @return self for methodChaining
     */
    public Check createMatchesRegexCheck(StringProperty property, Severity severity, Node decorated, String regex) {
        Function<String, String> messageCreator = string -> getDefaultCheckMessagesFactory().matchesRegexMessage(property.getName(), string, regex);
        Predicate<String> matchCheck = string -> !string.matches(regex);
        return createCheck(property, severity, decorated, messageCreator, matchCheck);
    }

    /**
     * Create a check that fails validation when the string value of the passed property does not match a regex.
     *
     * @param property whose value should match the regex
     * @param severity of the validation
     * @param regex    that the property-value should match
     * @return self for methodChaining
     */
    public Check createMatchesRegexCheck(StringProperty property, Severity severity, String regex) {
        return createMatchesRegexCheck(property, severity, null, regex);
    }

    private Double asNumber(String string) {
        if (string == null) {
            return null;
        }
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private <T> Check createCheck(ReadOnlyProperty<T> property, Severity severity, Node decorated, Function<T, String> messageCreator, Predicate<T> shouldActivate) {
        Check check = validator.createCheck();
        String key = UUID.randomUUID().toString();
        check.dependsOn(key, property)
                .withMethod(context -> validateWithContext(key, severity, context, messageCreator, shouldActivate));
        if (decorated != null) {
            check.decorates(decorated);
        }
        return check;
    }

    private <T> void validateWithContext(String key, Severity severity, Check.Context context, Function<T, String> messageCreator, Predicate<T> shouldActivate) {
        T retrieved = context.get(key);
        if (shouldActivate.test(retrieved)) {
            String message = messageCreator.apply(retrieved);
            if (severity == Severity.ERROR) {
                context.error(message);
            } else if (severity == Severity.WARNING) {
                context.warn(message);
            }
        }
    }
}