package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    Long id;
    String text;
    Long itemId;
    Long authorId;
    String authorName;
    LocalDateTime created = LocalDateTime.now();
}
