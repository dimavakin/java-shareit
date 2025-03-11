package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

public class CommentMapper {
    public static Comment mapToCommentFromCreate(CommentDto dto, User commentator, Item item) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setCreated(dto.getCreated());
        comment.setUser(commentator);
        comment.setItem(item);
        return comment;
    }

    public static CommentDto mapToCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setAuthorId(comment.getUser().getId());
        dto.setAuthorName(comment.getUser().getName());
        dto.setItemId(comment.getItem().getId());
        dto.setCreated(comment.getCreated());
        dto.setText(comment.getText());
        return dto;
    }
}
