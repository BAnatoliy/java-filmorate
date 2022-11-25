
package ValidTests;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserBuilder;

import javax.validation.Validation;
import java.time.LocalDate;

import static ValidTests.ValidatorTestUtil.valueHasErrorMessage;
import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {
    @Test
    void createUserWithEmptyLogin() {
        User user = UserBuilder.builder()
                .login(null)
                .name("Name")
                .email("qw@yandex.ru")
                .birthday(LocalDate.of(1995, 12, 27))
                .build();
        User user2 = UserBuilder.builder()
                .login("")
                .name("Name")
                .email("qw@yandex.ru")
                .birthday(LocalDate.of(1995, 12, 27))
                .build();
        User user3 = UserBuilder.builder()
                .login("    ")
                .name("Name")
                .email("qw@yandex.ru")
                .birthday(LocalDate.of(1995, 12, 27))
                .build();

        assertTrue(valueHasErrorMessage(user, "Не корректный login."));
        assertTrue(valueHasErrorMessage(user2, "Не корректный login."));
        assertTrue(valueHasErrorMessage(user3, "Не корректный login."));
    }

    @Test
    void createUserWithWrongEmail() {
        User user = UserBuilder.builder()
                .login("Login")
                .name("Name")
                .email("qw@yandex.ru")
                .birthday(LocalDate.of(1995, 12, 27))
                .build();
        User user2 = UserBuilder.builder()
                .login("Login")
                .name("Name")
                .email("qwyandex.ru")
                .birthday(LocalDate.of(1995, 12, 27))
                .build();
        User user3 = UserBuilder.builder()
                .login("Login")
                .name("Name")
                .email("qwyandex.ru@")
                .birthday(LocalDate.of(1995, 12, 27))
                .build();

        assertAll(
                () -> {
                    assertTrue(Validation.buildDefaultValidatorFactory().getValidator().validate(user).isEmpty());
                    assertFalse(Validation.buildDefaultValidatorFactory().getValidator().validate(user2).isEmpty());
                    assertFalse(Validation.buildDefaultValidatorFactory().getValidator().validate(user3).isEmpty());
                }
        );
    }
    @Test
    void createUserWithFeatureBirthday() {
        User user = UserBuilder.builder()
                .login("Login")
                .name("Name")
                .email("qw@yandex.ru")
                .birthday(LocalDate.now().minusDays(1))
                .build();
        User user2 = UserBuilder.builder()
                .login("Login")
                .name("Name")
                .email("qw@yandex.ru")
                .birthday(LocalDate.now())
                .build();
        User user3 = UserBuilder.builder()
                .login("Login")
                .name("Name")
                .email("qw@yandex.ru")
                .birthday(LocalDate.now().plusDays(1))
                .build();

        assertFalse(valueHasErrorMessage(user, "Дата рождения не может быть в будущем."));
        assertFalse(valueHasErrorMessage(user2, "Дата рождения не может быть в будущем."));
        assertTrue(valueHasErrorMessage(user3, "Дата рождения не может быть в будущем."));
    }
}