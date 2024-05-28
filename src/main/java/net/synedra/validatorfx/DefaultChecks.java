package net.synedra.validatorfx;

import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

public class DefaultChecks {
    private final Validator validator;

    DefaultChecks(Validator validator) {
        this.validator = validator;
    }

    public <T> Check createNonNullCheck(Property<T> property, Severity severity, Node decorated) {
        Function<T, String> messageCreator = t -> String.format("%s mustn't be null", property.getName());
        Predicate<T> notNull = Objects::nonNull;
        return createCheck(property, severity, decorated, messageCreator, notNull);
    }

    public <T> Check createNonNullCheck(Property<T> property, Severity severity) {
        return createNonNullCheck(property, severity, null);
    }

    public Check createNonBlankCheck(StringProperty property, Severity severity, Node decorated) {
        Function<String, String> messageCreator = string -> String.format("%s is required to not be blank", property.getName());
        Predicate<String> isBlank = String::isBlank;
        return createCheck(property, severity, decorated, messageCreator, isBlank);
    }

    public Check createNonBlankCheck(StringProperty property, Severity severity) {
        return createNonBlankCheck(property, severity, null);
    }

    public Check createMinimumLengthCheck(StringProperty property, Severity severity, Node decorated, int minimumLength) {
        Function<String, String> messageCreator = string -> String.format("%s with value of %s should be at least %d characters long", property.getName(), property.get(), minimumLength);
        Predicate<String> lengthCheck = string -> string != null && string.length() >= minimumLength;
        return createCheck(property, severity, decorated, messageCreator, lengthCheck);
    }

    public Check createMinimumLengthCheck(StringProperty property, Severity severity, int minimumLength) {
        return createMinimumLengthCheck(property, severity, null, minimumLength);
    }

    public Check createMaximumLengthCheck(StringProperty property, Severity severity, Node decorated, int maximumLength) {
        Function<String, String> messageCreator = string -> String.format("%s with value of %s should be at most %d characters long", property.getName(), property.get(), maximumLength);
        Predicate<String> lengthCheck = string -> string != null && string.length() <= maximumLength;
        return createCheck(property, severity, decorated, messageCreator, lengthCheck);
    }

    public Check createMaximumLengthCheck(StringProperty property, Severity severity, int maximumLength) {
        return createMaximumLengthCheck(property, severity, null, maximumLength);
    }

    public <T> Check createIsAssignableToCheck(StringProperty property, Severity severity, Node decorated, Function<String, Optional<T>> mapper, Function<String, String> messageCreator) {
        Function<String, String> alternativeMessageCreator = string -> String.format("%s is not assignable to %s", property.getName(), mapper.apply(string));
        Predicate<String> instanceCheck = string -> mapper.apply(string).isPresent();
        return createCheck(property, severity, decorated, messageCreator == null ? alternativeMessageCreator : messageCreator, instanceCheck);
    }

    public <T> Check createIsAssignableToCheck(StringProperty property, Severity severity, Function<String, Optional<T>> mapper, Function<String, String> messageCreator) {
        return createIsAssignableToCheck(property, severity, null, mapper, messageCreator);
    }

    public <T> Check createIsAssignableToCheck(StringProperty property, Severity severity, Function<String, Optional<T>> mapper) {
        return createIsAssignableToCheck(property, severity, mapper, null);
    }

    public Check createIsNumberCheck(StringProperty property, Severity severity, Node decorated) {
        Function<String, String> messageCreator = string -> String.format("%s isn't a number", property.getName());
        Function<String, Optional<Double>> isNumberCheck = string -> Optional.ofNullable(asNumber(string));
        return createIsAssignableToCheck(property, severity, decorated, isNumberCheck, messageCreator);
    }

    public Check createIsNumberCheck(StringProperty property, Severity severity) {
        return createIsNumberCheck(property, severity, null);
    }

    public Check createIsNumberWithinBoundsCheck(StringProperty property, Severity severity, Node decorated, double minimum, double maximum) {
        Function<String, Optional<Double>> isNumberWithinBoundsCheck = string -> Optional.ofNullable(asNumber(string)).filter(d -> d >= minimum && d <= maximum);
        Function<String, String> messageCreator = string -> String.format("%s[%s] is not between %f and %f", property.getName(), isNumberWithinBoundsCheck.apply(string).map(String::valueOf).orElse("NAN"), minimum, maximum);
        return createIsAssignableToCheck(property, severity, decorated, isNumberWithinBoundsCheck, messageCreator);
    }

    public Check createIsNumberWithinBoundsCheck(StringProperty property, Severity severity, double minimum, double maximum) {
        return createIsNumberWithinBoundsCheck(property, severity, null, minimum, maximum);
    }

    public Check matchesRegexCheck(StringProperty property, Severity severity, Node decorated, String regex) {
        Function<String, String> messageCreator = string -> String.format("%s[%s] does not match the regex %s", property.getName(), string, regex);
        Predicate<String> matchCheck = string -> string.matches(regex);
        return createCheck(property, severity, decorated, messageCreator, matchCheck);
    }

    public Check matchesRegexCheck(StringProperty property, Severity severity, String regex) {
        return matchesRegexCheck(property, severity, null, regex);
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

    private <T> Check createCheck(Property<T> property, Severity severity, Node decorated, Function<T, String> messageCreator, Predicate<T> shouldActivate) {
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