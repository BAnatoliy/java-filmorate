package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public void deleteUser(long id) {
        userStorage.deleteUser(id);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
     }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    public void addFriend(long userId, long friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriends(long userId) {
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        return userStorage.getCommonFriends(userId, otherUserId);
    }
}
