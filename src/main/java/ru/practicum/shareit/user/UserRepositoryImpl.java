package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private List<User> users = new ArrayList<>();
    private Long id = 0L;

    public List<User> findAll() {
        return users;
    }

    public User save(User user) {
        if (users.stream().anyMatch(user1 -> user1.getEmail().equals(user.getEmail()))) {
            throw new DuplicatedDataException("Такой email уже зарегестрирован");
        }
        user.setId(id);
        users.add(user);
        id += 1;
        return user;
    }

    @Override
    public User updateUser(User newUser, Long userId) {
        Optional<User> userOptional = users.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst();
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Не нашелся user с таким ID");
        }
        if (users.stream().anyMatch(user1 -> user1.getEmail().equals(newUser.getEmail()))) {
            throw new DuplicatedDataException("Такой email уже зарегестрирован");
        }
        if (newUser.getEmail() != null) {
            userOptional.ifPresent(user -> user.setEmail(newUser.getEmail()));
        }
        if (newUser.getName() != null) {
            userOptional.ifPresent(user -> user.setName(newUser.getName()));
        }

        return userOptional.get();
    }

    @Override
    public User getUser(Long userId) {
        Optional<User> userOptional = users.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst();
        if (userOptional.isEmpty()) {
            throw new NotFoundException("Не нашелся user с таким ID");
        }
        return userOptional.get();
    }

    @Override
    public User delete(Long id) {
        Optional<User> deletedUser = users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
        if (deletedUser.isEmpty()) {
            throw new NotFoundException("Не нашелся user с таким ID");
        }
        users.remove(deletedUser.get());
        return deletedUser.get();
    }

    public boolean checkUserExists(Long id) {
        return users.stream()
                .noneMatch(user -> user.getId().equals(id));
    }
}