package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.mapToUserDto(repository.findAll());
    }

    @Override
    public UserDto getUser(Long userId) {
        return UserMapper.mapToUserDto(repository.getUser(userId));
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        User savedUser = repository.save(user);
        return UserMapper.mapToUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User user = UserMapper.mapToUser(userDto);
        User patchedUser = repository.updateUser(user, userId);
        return UserMapper.mapToUserDto(patchedUser);
    }

    @Override
    public UserDto delete(Long id) {
        User user = repository.delete(id);
        return UserMapper.mapToUserDto(user);
    }
}