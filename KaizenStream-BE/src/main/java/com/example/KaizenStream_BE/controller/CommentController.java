package com.example.KaizenStream_BE.controller;


import com.example.KaizenStream_BE.dto.request.comment.CommentRequest;
import com.example.KaizenStream_BE.dto.respone.CommentRespone;
import com.example.KaizenStream_BE.entity.Comment;
import com.example.KaizenStream_BE.service.CommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;


    @PostMapping(value = "/blog/{blogId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentRespone> createComment(
            @PathVariable String blogId,
            @RequestBody CommentRequest commentRequest) {
        System.out.println("Request Comment: " + commentRequest);
        CommentRespone createdCommentRespone = commentService.createComment(blogId, commentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCommentRespone);
    }



    @GetMapping("/blog/{blogId}")
    public ResponseEntity<List<CommentRespone>> getCommentsByBlogId(@PathVariable String blogId) {
        List<CommentRespone> comments = commentService.getCommentsByBlogId(blogId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable String commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

//    @DeleteMapping("/{commentId}")
//    public ResponseEntity<Void> deleteComment(@PathVariable String commentId, @RequestParam String userId) {
//        commentService.deleteComment(commentId, userId);
//        return ResponseEntity.noContent().build();
//    }


}
