package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @GetMapping()
    public ResponseEntity<Object> findAll() {
        return userClient.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findUser(@PathVariable Long id) {
        return userClient.findById(id);
    }

    @PostMapping()
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        return userClient.create(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@Valid @RequestBody UserDto userDto, @PathVariable Long id) {
        return userClient.update(userDto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        return userClient.delete(id);
    }
}