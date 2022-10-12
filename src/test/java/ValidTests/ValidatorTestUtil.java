package ValidTests;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class ValidatorTestUtil {
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private ValidatorTestUtil() {
    }

    public static <T> boolean valueHasErrorMessage(T value, @NotNull String message) {
        Set<ConstraintViolation<T>> errors = VALIDATOR.validate(value);
        return errors.stream().map(ConstraintViolation::getMessage).anyMatch(message::equals);
    }
}
