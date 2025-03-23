package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.base.BaseSpringBootTest;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceImplIntegrationTest extends BaseSpringBootTest {
    private User user;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        user = new User(null, "John Doe", "john.doe@example.com");
        userDto = new UserDto(1L, "John Doe1", "john.doe@example.com1");

        userRepository.save(user);
    }

    @Test
    public void testFindAllWhenUsersExistThenReturnListOfUsers() {
        List<UserDto> users = userService.getAllUsers();
        assertThat(users).hasSize(1);
        assertThat(users.getFirst().getName()).isEqualTo(user.getName());
        assertThat(users.getFirst().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void testFindByIdWhenUserExistsThenReturnUser() {
        UserDto foundUser = userService.getUser(user.getId());
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getName()).isEqualTo(user.getName());
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void testFindByIdWhenUserDoesNotExistThenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> userService.getUser(111L));
    }

    @Test
    public void testCreateWhenUserIsValidThenReturnCreatedUser() {
        UserDto createdUser = userService.saveUser(userDto);
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getName()).isEqualTo(userDto.getName());
        assertThat(createdUser.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    public void testCreateWhenEmailIsDuplicatedThenThrowDuplicatedDataException() {
        userDto.setEmail(user.getEmail());
        assertThrows(DuplicatedDataException.class, () -> userService.saveUser(userDto));
    }

    @Test
    public void testUpdateWhenUserIsValidThenReturnUpdatedUser() {
        UserDto updatedUser = userService.updateUser(userDto, user.getId());
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo(userDto.getName());
        assertThat(updatedUser.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    public void testUpdateWhenUserDoesNotExistThenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> userService.updateUser(userDto, 999L));
    }

    @Test
    public void testDeleteWhenUserExistsThenReturnDeletedUser() {
        UserDto deletedUser = userService.delete(user.getId());
        assertThat(deletedUser).isNotNull();
        assertThat(deletedUser.getName()).isEqualTo(user.getName());
        assertThat(deletedUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void testDeleteWhenUserDoesNotExistThenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> userService.delete(999L));
    }

}
