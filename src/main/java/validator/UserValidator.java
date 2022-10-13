package validator;

import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class UserValidator implements ConstraintValidator<UserValid, User> {
    private static final String ERROR_FOR_LOGIN = "Не корректный login.";
    private static final String ERROR_FOR_BIRTHDAY = "Дата рождения не может быть в будущем.";

    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ERROR_FOR_LOGIN).addConstraintViolation();
            return false;
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ERROR_FOR_BIRTHDAY).addConstraintViolation();
            return false;
        }
        return true;
    }
}
