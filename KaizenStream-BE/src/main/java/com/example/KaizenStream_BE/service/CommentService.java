package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.entity.Blog;
import com.example.KaizenStream_BE.entity.Comment;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.repository.BlogRepository;
import com.example.KaizenStream_BE.repository.CommentRepository;
import com.example.KaizenStream_BE.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {
    CommentRepository commentRepository;
    BlogRepository blogRepository;
    UserRepository userRepository;

    @Transactional
   public Comment createComment (String blogId, Comment comment){
       Blog blog = blogRepository.findById(blogId)
               .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy blog với ID: " + blogId));
       User user = userRepository.findById(comment.getUser().getUserId())
               .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user với ID: " + comment.getUser().getUserId()));
       comment.setBlog(blog);
       comment.setUser(user);

       return commentRepository.save(comment);
   }

    public List<Comment> getCommentsByBlogId(String blogId) {
        return commentRepository.findByBlog_BlogId(blogId);
    }

    @Transactional
    public void deleteComment(String commentId) {
        commentRepository.deleteById(commentId);
    }

}
