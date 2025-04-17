package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.request.comment.CommentRequest;
import com.example.KaizenStream_BE.dto.respone.CommentRespone;
import com.example.KaizenStream_BE.entity.Blog;
import com.example.KaizenStream_BE.entity.Comment;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.enums.ErrorCode;
import com.example.KaizenStream_BE.exception.AppException;
import com.example.KaizenStream_BE.mapper.CommentMapper;
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
    CommentMapper commentMapper;

    @Transactional
    public CommentRespone createComment(String blogId, CommentRequest request) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        Comment comment = new Comment();
        comment.setBlog(blog);
        comment.setUser(user);
        comment.setContent(request.getContent());

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toResponse(savedComment);
    }

    public List<CommentRespone> getCommentsByBlogId(String blogId) {
        List<Comment> comments = commentRepository.findByBlog_BlogId(blogId);
        return comments.stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(String commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new AppException(ErrorCode.COMMENT_NOT_FOUND);
        }
        commentRepository.deleteById(commentId);
    }
}

