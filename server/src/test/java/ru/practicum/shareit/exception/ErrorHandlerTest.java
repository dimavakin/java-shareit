package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class ErrorHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHandleValidationException() throws Exception {
        mockMvc.perform(get("/test/validation-exception")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()) // Ожидаем статус 400
                .andExpect(jsonPath("$.error", is("Ошибка валидации"))) // Проверяем поле error
                .andExpect(jsonPath("$.message", is("Сообщение об ошибке валидации"))); // Проверяем поле message
    }

    @Test
    public void testHandleNotFoundException() throws Exception {
        mockMvc.perform(get("/test/not-found-exception")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Ожидаем статус 404
                .andExpect(jsonPath("$.error", is("Не найдено"))) // Проверяем поле error
                .andExpect(jsonPath("$.message", is("Сообщение об ошибке 'не найдено'"))); // Проверяем поле message
    }

    @Test
    public void testHandleDuplicatedDataException() throws Exception {
        mockMvc.perform(get("/test/duplicated-data-exception")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict()) // Ожидаем статус 409
                .andExpect(jsonPath("$.error", is("Дублирование данных"))) // Проверяем поле error
                .andExpect(jsonPath("$.message", is("Сообщение об ошибке дублирования данных"))); // Проверяем поле message
    }
}