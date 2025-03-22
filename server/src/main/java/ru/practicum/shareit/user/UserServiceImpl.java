package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.mapToUserDto(repository.findAll());
    }

    @Override
    public UserDto getUser(Long userId) {
        return UserMapper.mapToUserDto(repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("id - " + userId + " не найден")));
    }

    @Transactional
    @Override
    public UserDto saveUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        checkEmail(userDto);
        User savedUser = repository.save(user);
        return UserMapper.mapToUserDto(savedUser);
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User existingUser = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("id - " + userId + " не найден"));

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            checkEmail(userDto);
            existingUser.setEmail(userDto.getEmail());
        }


        repository.save(existingUser);

        return UserMapper.mapToUserDto(existingUser);
    }

    @Transactional
    @Override
    public UserDto delete(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("id - " + userId + " не найден"));
        repository.deleteById(userId);
        return UserMapper.mapToUserDto(user);
    }

    private void checkEmail(UserDto user) {
        if (repository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicatedDataException(String.format("Этот email - %s уже используется", user.getEmail()));
        }
    }
}