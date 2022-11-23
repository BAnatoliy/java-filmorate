
package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserBuilder;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;

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
        userController = new UserController(new UserService(new InMemoryUserStorage()));
    }

    @Test
    void should_List_Of_Users_Be_Empty_When_Users_Not_Created() {
        ArrayList<User> listOfUser = new ArrayList<>(userController.findAll());
        assertTrue(listOfUser.isEmpty());
    }

    @Test
    void should_Create_Three_Valid_Users_And_Find_All_Users(){
        User user1 = UserBuilder.builder()
                .login("QW")
                .name("Ann")
                .email("qw@mail.ru")
                .birthday(LocalDate.of(1995, 12, 27))
                .build();

        User user2 = UserBuilder.builder()
                .login("AS")
                .name("Billy")
                .email("as@yandex.ru")
                .birthday(LocalDate.of(2000, 1, 15))
                .build();

        User user3 = UserBuilder.builder()
                .login("ZX")
                .name("John")
                .email("zx@google.com")
                .birthday(LocalDate.of(1997, 7, 25))
                .build();

        userController.create(user1);
        userController.create(user2);
        userController.create(user3);

        List<User> users = new ArrayList<>(userController.findAll()).stream()
                .sorted(Comparator.comparingLong(User::getId))
                .collect(Collectors.toList());

        assertAll(() -> {
            assertEquals(user1, users.get(0));
            assertEquals(user2, users.get(1));
            assertEquals(user3, users.get(2));
            assertEquals(3, users.size());
        });
    }

    @Test
    void should_Set_User_Name_Like_Login_When_Name_Is_Empty_Or_Null(){
        User user1 = UserBuilder.builder()
                .login("QW")
                .name("Ann")
                .email("qw@mail.ru")
                .birthday(LocalDate.of(1995, 12, 27))
                .build();

        User user2 = UserBuilder.builder()
                .login("AS")
                .name("")
                .email("as@yandex.ru")
                .birthday(LocalDate.of(2000, 1, 15))
                .build();

        User user3 = UserBuilder.builder()
                .login("ZX")
                .name(null)
                .email("zx@google.com")
                .birthday(LocalDate.of(1997, 7, 25))
                .build();

        userController.create(user1);
        userController.create(user2);
        userController.create(user3);

        List<User> users = new ArrayList<>(userController.findAll()).stream()
                .sorted(Comparator.comparingLong(User::getId))
                .collect(Collectors.toList());

        assertAll(() -> {
            assertEquals("Ann", users.get(0).getName());
            assertEquals("AS", users.get(1).getName());
            assertEquals("ZX", users.get(2).getName());
            assertEquals(3, users.size());
        });
    }

    @Test
    void should_Create_Two_Valid_User_And_Not_Create_User_When_This_User_Already_Created(){
        User user1 = UserBuilder.builder()
                .login("QW")
                .name("Ann")
                .email("qw@mail.ru")
                .birthday(LocalDate.of(1995, 12, 27))
                .build();

        User user2 = UserBuilder.builder()
                .login("AS")
                .name("Billy")
                .email("as@yandex.ru")
                .birthday(LocalDate.of(2000, 1, 15))
                .build();

        userController.create(user1);
        userController.create(user2);

        User user3 = UserBuilder.builder()
                .login("AS")
                .name("Billy")
                .email("as@yandex.ru")
                .birthday(LocalDate.of(2000, 1, 15))
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.create(user3)
                );
        String expectMassageException = "User already created";
        String resultMassageException = exception.getMessage();
        assertEquals(expectMassageException, resultMassageException);

        List<User> users = new ArrayList<>(userController.findAll());
        assertEquals(2, users.size());
    }

    @Test
    void should_Not_Update_User_When_This_User_Not_Created() {
        User user1 = UserBuilder.builder()
                .login("QW")
                .name("Ann")
                .email("qw@mail.ru")
                .birthday(LocalDate.of(1995, 12, 27))
                .build();

        User user2 = UserBuilder.builder()
                .login("AS")
                .name("Billy")
                .email("as@yandex.ru")
                .birthday(LocalDate.of(2000, 1, 15))
                .build();

        User newUser = UserBuilder.builder()
                .id(111)
                .login("ZX")
                .name("John")
                .email("zx@google.com")
                .birthday(LocalDate.of(1997, 7, 25))
                .build();

        userController.create(user1);
        userController.create(user2);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userController.update(newUser)
        );
        String expectMassageException = "User not found!";
        String resultMassageException = exception.getMessage();
        assertEquals(expectMassageException, resultMassageException);

        List<User> users = new ArrayList<>(userController.findAll()).stream()
                .sorted(Comparator.comparingLong(User::getId))
                .collect(Collectors.toList());

        assertAll(() -> {
            assertEquals(user1, users.get(0));
            assertEquals(user2, users.get(1));
            assertEquals(2, users.size());
        });
    }

    @Test
    void should_Update_User_When_This_User_Created() {
        User user1 = UserBuilder.builder()
                .login("QW")
                .name("Ann")
                .email("qw@mail.ru")
                .birthday(LocalDate.of(1995, 12, 27))
                .build();

        User user2 = UserBuilder.builder()
                .login("AS")
                .name("Billy")
                .email("as@yandex.ru")
                .birthday(LocalDate.of(2000, 1, 15))
                .build();

        userController.create(user1);
        userController.create(user2);

        User newUser = UserBuilder.builder()
                .id(2)
                .login("ZX")
                .name("John")
                .email("zx@google.com")
                .birthday(LocalDate.of(1997, 7, 25))
                .build();

        userController.update(newUser);

        List<User> users = new ArrayList<>(userController.findAll()).stream()
                .sorted(Comparator.comparingLong(User::getId))
                .collect(Collectors.toList());

        assertAll(() -> {
            assertEquals(user1, users.get(0));
            assertEquals(newUser, users.get(1));
            assertEquals(2, users.size());
        });
    }
}
