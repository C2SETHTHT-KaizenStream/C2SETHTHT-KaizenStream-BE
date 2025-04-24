package com.example.KaizenStream_BE.dto.respone;

import lombok.Data;

@Data
public class CommentRespone {
    private String commentId;
    private String content;
    private String createAt;
    private String username;
    private String userId;

    public CommentRespone(String commentId, String content, String createAt, String username, String userId) {
        this.commentId = commentId;
        this.content = content;
        this.createAt = createAt;
        this.username = username;
        this.userId = userId;
    }
}
