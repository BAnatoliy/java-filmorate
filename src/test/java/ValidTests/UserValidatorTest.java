package ValidTests;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Validation;
import java.time.LocalDate;

import static ValidTests.ValidatorTestUtil.valueHasErrorMessage;
import static org.junit.jupiter.api.Assertions.*;

public class UserValidatorTest {
    @Test
    public void createUserWithEmptyLogin() {
        User user = new User(null, "Name", "qw@yandex.ru", LocalDate.of(1995, 12, 27));
        User user2 = new User("", "Name", "qw@yandex.ru", LocalDate.of(1995, 12, 27));
        User user3 = new User("    ", "Name", "qw@yandex.ru", LocalDate.of(1995, 12, 27));

        assertTrue(valueHasErrorMessage(user, "Не корректный login."));
        assertTrue(valueHasErrorMessage(user2, "Не корректный login."));
        assertTrue(valueHasErrorMessage(user3, "Не корректный login."));
    }

    @Test
    public void createUserWithWrongEmail() {
        User user = new User("Login", "Name", "qw@yandex.ru", LocalDate.of(1995, 12, 27));
        User user2 = new User("Login", "Name", "qwyandex.ru", LocalDate.of(1995, 12, 27));
        User user3 = new User("Login", "Name", "qwyandex.ru@", LocalDate.of(1995, 12, 27));

        assertAll(
                () -> {
                    assertTrue(Validation.buildDefaultValidatorFactory().getValidator().validate(user).isEmpty());
                    assertFalse(Validation.buildDefaultValidatorFactory().getValidator().validate(user2).isEmpty());
                    assertFalse(Validation.buildDefaultValidatorFactory().getValidator().validate(user3).isEmpty());
                }
        );
    }
    @Test
    public void createUserWithFeatureBirthday() {
        User user = new User("Login", "Name", "qw@yandex.ru", LocalDate.now().minusDays(1));
        User user2 = new User("Login", "Name", "qw@yandex.ru", LocalDate.now());
        User user3 = new User("Login", "Name", "qw@yandex.ru", LocalDate.now().plusDays(1));

        assertFalse(valueHasErrorMessage(user, "Дата рождения не может быть в будущем."));
        assertFalse(valueHasErrorMessage(user2, "Дата рождения не может быть в будущем."));
        assertTrue(valueHasErrorMessage(user3, "Дата рождения не может быть в будущем."));
    }
}
