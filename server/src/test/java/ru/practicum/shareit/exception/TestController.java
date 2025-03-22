package ru.practicum.shareit.exception;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/validation-exception")
    public void throwValidationException() {
        throw new ValidationException("Сообщение об ошибке валидации");
    }

    @GetMapping("/not-found-exception")
    public void throwNotFoundException() {
        throw new NotFoundException("Сообщение об ошибке 'не найдено'");
    }

    @GetMapping("/duplicated-data-exception")
    public void throwDuplicatedDataException() {
        throw new DuplicatedDataException("Сообщение об ошибке дублирования данных");
    }
}