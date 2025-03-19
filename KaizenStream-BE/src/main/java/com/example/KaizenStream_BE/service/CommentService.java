package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.respone.CommentRespone;
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
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {
    CommentRepository commentRepository;
    BlogRepository blogRepository;
    UserRepository userRepository;


    @Transactional
    public CommentRespone createComment(String blogId, Comment comment) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy blog với ID: " + blogId));
        User user = userRepository.findById(comment.getUser().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user với ID: " + comment.getUser().getUserId()));

        comment.setBlog(blog);
        comment.setUser(user);

        Comment savedComment = commentRepository.save(comment);

        CommentRespone response = new CommentRespone(
                savedComment.getCommentId(),
                savedComment.getContent(),
                savedComment.getCreateAt().toString(),
                user.getUserName()
        );

        return response;
    }




    public List<CommentRespone> getCommentsByBlogId(String blogId) {
        List<Comment> comments = commentRepository.findByBlog_BlogId(blogId);

        List<CommentRespone> response = comments.stream()
                .map(comment -> new CommentRespone(
                        comment.getCommentId(),
                        comment.getContent(),
                        comment.getCreateAt().toString(),
                        comment.getUser().getUserName()
                ))
                .collect(Collectors.toList());

        return response;
    }


    @Transactional
    public void deleteComment(String commentId) {
        commentRepository.deleteById(commentId);
    }

}
