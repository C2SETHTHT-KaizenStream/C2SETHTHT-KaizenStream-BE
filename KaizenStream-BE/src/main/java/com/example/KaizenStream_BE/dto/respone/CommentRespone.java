package com.example.KaizenStream_BE.dto.respone;

import lombok.Data;

@Data
public class CommentRespone {
    private String commentId;
    private String content;
    private String createAt;
    private String username;

    public CommentRespone(String commentId, String content, String createAt, String username) {
        this.commentId = commentId;
        this.content = content;
        this.createAt = createAt;
        this.username = username;
    }
}
