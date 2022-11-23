package ru.yandex.practicum.filmorate.storage.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
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

    public void addFriend(long userId, long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.addFriend(friend.getId());
        friend.addFriend(user.getId());
        log.debug("User {} and User {} are friends now!", user, friend);
    }

    public void deleteFriend(long userId, long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.deleteFriend(friendId);
        friend.deleteFriend(userId);
        log.debug("User {} and User {} are not friends anymore!", user, friend);
    }

    public List<User> getFriends(long userId) {
        User user = getUserById(userId);
        if(user.getFriendsId() == null) {
            return new ArrayList<>();
        }
        List<Long> userIdList = new ArrayList<>(user.getFriendsId());
        log.debug("Get friends by User: {}", user);
        return userIdList.stream().map(this::getUserById).collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        List<User> friendsUsers = getFriends(userId);
        friendsUsers.addAll(getFriends(otherUserId));
        log.debug("Get common friends User with id: {} and User with id: {}", userId, otherUserId);
        return friendsUsers.stream()
                .collect(Collectors.groupingBy(Function.identity()))
                .entrySet()
                .stream()
                .filter(u -> u.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private void generationId() {
        userId++;
    }
}
