package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Map<Long, User> users = new HashMap<>();
    private long userId = 1;

    @Override
    public User addUser(User user) {
        if (users.containsValue(user)) {
            throw new ValidationException("User already created");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(userId);
        users.put(user.getId(), user);
        generationId();
        log.debug("User created: {}", user);
        return user;
    }

    @Override
    public void deleteUser(long id) {
        if(!users.containsKey(id)) {
            throw new EntityNotFoundException("User not found!");
        }
        users.remove(id);
        log.debug("User with id: {} deleted", id);
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            if (user.getName() == null || user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            log.debug("Update user: {}", user);
            users.put(user.getId(), user);
            return user;
        } else {
            throw new EntityNotFoundException("User not found!");
        }
    }

    @Override
    public List<User> getUsers() {
        log.debug("Count of users: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long id) {
        if(!users.containsKey(id)) {
            throw new EntityNotFoundException("User not found!");
        }
        User user = users.get(id);
        log.debug("Get user: {}", user);
        return user;
    }

    private void generationId() {
        userId++;
    }
}
