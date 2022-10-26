package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserService {
    private InMemoryUserStorage inMemoryUserStorage;
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public User addUser(User user) {
        inMemoryUserStorage.addUser(user);
        return user;
    }

    public void deleteUser(long id) {
        inMemoryUserStorage.deleteUser(id);
    }

    public User updateUser(User user) {
        inMemoryUserStorage.updateUser(user);
        return user;
    }

    public List<User> getUsers() {
        return inMemoryUserStorage.getUsers();
    }

    public User getUserById(long id) {
        return inMemoryUserStorage.getUserById(id);
    }

    public void addFriend(long userId, long friendId) {
        User user = inMemoryUserStorage.getUserById(userId);
        User friend = inMemoryUserStorage.getUserById(friendId);
        user.addFriend(friend.getId());
        friend.addFriend(user.getId());
        log.debug("User {} and User {} are friends now!", user, friend);
    }

    public void deleteFriend(long userId, long friendId) {
        User user = inMemoryUserStorage.getUserById(userId);
        User friend = inMemoryUserStorage.getUserById(friendId);
        user.deleteFriend(friendId);
        friend.deleteFriend(userId);
        log.debug("User {} and User {} are not friends anymore!", user, friend);
    }

    public List<User> getFriends(long userId) {
        User user = inMemoryUserStorage.getUserById(userId);
        List<Long> userIdList = new ArrayList<>(user.getFriendsId());
        log.debug("Get friends by User: {}", user);
        return userIdList.stream().map(id -> inMemoryUserStorage.getUserById(id)).collect(Collectors.toList());
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
}
