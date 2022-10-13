package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;

    @BeforeEach
    public void setUp() {
        userController = new UserController();
    }

    @Test
    public void should_List_Of_Users_Be_Empty_When_Users_Not_Created() {
        ArrayList<User> listOfUser = new ArrayList<>(userController.findAll());
        assertTrue(listOfUser.isEmpty());
    }

    @Test
    public void should_Create_Three_Valid_Users_And_Find_All_Users(){
        User user1 = new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27));
        User user2 = new User("AS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15));
        User user3 = new User( "ZX", "John", "zx@google.com", LocalDate.of(1997, 7, 25));

        userController.create(user1);
        userController.create(user2);
        userController.create(user3);

        List<User> users = new ArrayList<>(userController.findAll()).stream()
                .sorted(Comparator.comparingInt(User::getId))
                .collect(Collectors.toList());

        assertAll(() -> {
            assertEquals(user1, users.get(0));
            assertEquals(user2, users.get(1));
            assertEquals(user3, users.get(2));
            assertEquals(3, users.size());
        });
    }

    @Test
    public void should_Set_User_Name_Like_Login_When_Name_Is_Empty_Or_Null(){
        User user1 = new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27));
        User user2 = new User("AS", "", "as@yandex.ru", LocalDate.of(2000, 1, 15));
        User user3 = new User( "ZX", null, "zx@google.com", LocalDate.of(1997, 7, 25));

        userController.create(user1);
        userController.create(user2);
        userController.create(user3);

        List<User> users = new ArrayList<>(userController.findAll()).stream()
                .sorted(Comparator.comparingInt(User::getId))
                .collect(Collectors.toList());

        assertAll(() -> {
            assertEquals("Ann", users.get(0).getName());
            assertEquals("AS", users.get(1).getName());
            assertEquals("ZX", users.get(2).getName());
            assertEquals(3, users.size());
        });
    }

    @Test
    public void should_Create_Two_Valid_User_And_Not_Create_User_When_This_User_Already_Created(){
        User user1 = new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27));
        User user2 = new User("AS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15));

        userController.create(user1);
        userController.create(user2);

        User user3 = new User( "AS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.create(user3)
                );
        String expectMassageException = "Пользователь уже существует.";
        String resultMassageException = exception.getMessage();
        assertEquals(expectMassageException, resultMassageException);

        List<User> users = new ArrayList<>(userController.findAll());
        assertEquals(2, users.size());
    }

    @Test
    public void should_Create_One_Valid_User_And_Not_Create_User_When_Email_Is_Null_Or_Not_Contain_Symbol_Of_Email(){
        User user1 = new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27));
        User user2 = new User("AS", "Billy", "as_yandex.ru", LocalDate.of(2000, 1, 15));
        User user3 = new User( "ZX", "John", null, LocalDate.of(1997, 7, 25));

        userController.create(user1);
        ValidationException exception1 = assertThrows(ValidationException.class,
                () -> userController.create(user2)
        );
        ValidationException exception2 = assertThrows(ValidationException.class,
                () -> userController.create(user3)
        );
        String expectMassageException = "Не корректный e-mail.";
        String resultMassageException1 = exception1.getMessage();
        String resultMassageException2 = exception2.getMessage();
        assertEquals(expectMassageException, resultMassageException1);
        assertEquals(expectMassageException, resultMassageException2);

        List<User> users = new ArrayList<>(userController.findAll());
        assertEquals(1, users.size());
    }

    @Test
    public void should_Create_One_Valid_User_And_Not_Create_User_When_Login_Is_Null_Or_Empty(){
        User user1 = new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27));
        User user2 = new User(null, "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15));
        User user3 = new User( "", "John", "zx@google.com", LocalDate.of(1997, 7, 25));
        User user4 = new User( "   ", "Roy", "ty@bk.ru", LocalDate.of(2001, 5, 9));

        userController.create(user1);
        ValidationException exception1 = assertThrows(ValidationException.class,
                () -> userController.create(user2)
        );
        ValidationException exception2 = assertThrows(ValidationException.class,
                () -> userController.create(user3)
        );
        ValidationException exception3 = assertThrows(ValidationException.class,
                () -> userController.create(user4)
        );
        String expectMassageException = "Не корректный login.";
        String resultMassageException1 = exception1.getMessage();
        String resultMassageException2 = exception2.getMessage();
        String resultMassageException3 = exception3.getMessage();
        assertEquals(expectMassageException, resultMassageException1);
        assertEquals(expectMassageException, resultMassageException2);
        assertEquals(expectMassageException, resultMassageException3);

        List<User> users = new ArrayList<>(userController.findAll());
        assertEquals(1, users.size());
    }

    @Test
    public void should_Create_Two_Valid_User_And_Not_Create_User_When_Birthday_After_Today(){
        User user1 = new User("QW", "Ann", "qw@mail.ru", LocalDate.now().minusDays(1));
        User user2 = new User("AS", "Billy", "as@yandex.ru", LocalDate.now());
        User user3 = new User( "ZX", "John", "zx@google.com", LocalDate.now().plusDays(1));

        userController.create(user1);
        userController.create(user2);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.create(user3)
        );
        String expectMassageException = "Дата рождения не может быть в будущем.";
        String resultMassageException = exception.getMessage();
        assertEquals(expectMassageException, resultMassageException);

        List<User> users = new ArrayList<>(userController.findAll());
        assertEquals(2, users.size());
    }

    @Test
    public void should_Not_Update_User_When_This_User_Not_Created() {
        User user1 = new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27));
        User user2 = new User("AS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15));
        User newUser = new User( "ZX", "John", "zx@google.com", LocalDate.of(1997, 7, 25));
        userController.create(user1);
        userController.create(user2);

        newUser.setId(111);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.update(newUser)
        );
        String expectMassageException = "Такого пользователя нет.";
        String resultMassageException = exception.getMessage();
        assertEquals(expectMassageException, resultMassageException);

        List<User> users = new ArrayList<>(userController.findAll()).stream()
                .sorted(Comparator.comparingInt(User::getId))
                .collect(Collectors.toList());

        assertAll(() -> {
            assertEquals(user1, users.get(0));
            assertEquals(user2, users.get(1));
            assertEquals(2, users.size());
        });
    }

    @Test
    public void should_Update_User_When_This_User_Created() {
        User user1 = new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27));
        User user2 = new User("AS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15));
        User newUser = new User( "ZX", "John", "zx@google.com", LocalDate.of(1997, 7, 25));
        userController.create(user1);
        userController.create(user2);

        newUser.setId(2);
        userController.update(newUser);

        List<User> users = new ArrayList<>(userController.findAll()).stream()
                .sorted(Comparator.comparingInt(User::getId))
                .collect(Collectors.toList());

        assertAll(() -> {
            assertEquals(user1, users.get(0));
            assertEquals(newUser, users.get(1));
            assertEquals(2, users.size());
        });
    }
}