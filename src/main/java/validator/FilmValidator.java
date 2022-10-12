package validator;

import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FilmValidator implements ConstraintValidator<FilmValid, Film> {
    private static final String ERROR_FOR_NAME = "Не корректное название фильма.";
    private static final String ERROR_FOR_DESCRIPTION = "Слишком длинное описание фильма.";
    private static final String ERROR_FOR_RELEASE_DATE = "Дата выхода фильма не может быть раньше 28 декабря 1895 г.";
    private static final String ERROR_FOR_DURATION = "Продолжительность фильма должна быть положительной.";

    @Override
    public boolean isValid(Film film, ConstraintValidatorContext context) {
        if (film.getName() == null || film.getName().isBlank()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ERROR_FOR_NAME).addConstraintViolation();
            return false;
        }
        if (film.getDescription().length() > 200) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ERROR_FOR_DESCRIPTION).addConstraintViolation();
            return false;
        }
        if (film.getDuration() < 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ERROR_FOR_DURATION).addConstraintViolation();
            return false;
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ERROR_FOR_RELEASE_DATE).addConstraintViolation();
            return false;
        }
        return true;
    }
}
