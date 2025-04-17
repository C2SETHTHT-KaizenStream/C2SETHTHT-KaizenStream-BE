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
    private int commentCount;
    private String imageUrl;

//    public BlogResponse(Blog blog) {

//        this.blogId = blog.getBlogId();
//        this.title = blog.getTitle();
//        this.content = blog.getContent();
//        this.createAt = blog.getCreateAt();
//        this.updateAt = blog.getUpdateAt();
//        this.likeCount = blog.getLikeCount();
//        this.userId = blog.getUser().getUserId();
//        this.userName = blog.getUser().getUserName();
//        this.commentCount = blog.getComments().size();
//        this.imageUrl = blog.getImageUrl();
//    }

    public BlogResponse(Blog blog) {
        if (blog == null) {
            return; // Hoặc gán giá trị mặc định tùy yêu cầu
        }
        this.blogId = blog.getBlogId();
        this.title = blog.getTitle();
        this.content = blog.getContent();
        this.createAt = blog.getCreateAt();
        this.updateAt = blog.getUpdateAt();
        this.likeCount = blog.getLikeCount();
        this.userId = blog.getUser() != null ? blog.getUser().getUserId() : null;
        this.userName = blog.getUser() != null ? blog.getUser().getUserName() : null;
        this.commentCount = blog.getComments() != null ? blog.getComments().size() : 0;
        this.imageUrl = blog.getImageUrl();
    }
}
