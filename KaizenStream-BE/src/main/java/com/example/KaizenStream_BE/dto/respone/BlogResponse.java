package com.example.KaizenStream_BE.dto.respone;

import com.example.KaizenStream_BE.entity.Blog;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BlogResponse {
    private String blogId;
    private String title;
    private String content;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private int likeCount;
    private String userId;
    private String userName;
    private int CommentCount;

    public BlogResponse(Blog blog) {
        this.blogId = blog.getBlogId();
        this.title = blog.getTitle();
        this.content = blog.getContent();
        this.createAt = blog.getCreateAt();
        this.updateAt = blog.getUpdateAt();
        this.likeCount = blog.getLikeCount();
        this.userId = blog.getUser().getUserId();
        this.userName = blog.getUser().getUserName();
        this.CommentCount = blog.getComments().size();
    }
}
