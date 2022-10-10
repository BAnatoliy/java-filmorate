package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @GetMapping
    public Collection<User> findAll() {
        log.debug("Текущее количество фильмов: {}", users.size());
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        checkValidation(user);
        if (users.containsValue(user)) {
            throw new ValidationException("Пользователь уже существует.");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(id);
        users.put(user.getId(), user);
        log.debug("Добавлен пользователь: {}", user);
        generationId();
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        checkValidation(user);
        if (users.containsKey(user.getId())) {
            if (user.getName() == null || user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            log.debug("Обновлен пользователь: {}", user);
            users.put(user.getId(), user);
            return user;
        } else {
            throw new ValidationException("Такого пользователя нет.");
        }
    }

    private void generationId() {
        id++;
    }

    private void checkValidation(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Не корректный e-mail.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Не корректный login.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }
}
