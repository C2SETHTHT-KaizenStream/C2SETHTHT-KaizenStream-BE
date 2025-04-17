package com.example.KaizenStream_BE.mapper;

import com.example.KaizenStream_BE.dto.respone.CommentRespone;
import com.example.KaizenStream_BE.entity.Comment;
import com.example.KaizenStream_BE.entity.User;
import java.time.format.DateTimeFormatter;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
@Component
public class CommentMapperImpl implements CommentMapper {

    @Override
    public CommentRespone toResponse(Comment comment) {
        if ( comment == null ) {
            return null;
        }

        String username = null;
        String commentId = null;
        String content = null;
        String createAt = null;

        username = commentUserUserName( comment );
        commentId = comment.getCommentId();
        content = comment.getContent();
        if ( comment.getCreateAt() != null ) {
            createAt = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format( comment.getCreateAt() );
        }

        CommentRespone commentRespone = new CommentRespone( commentId, content, createAt, username );

        return commentRespone;
    }

    private String commentUserUserName(Comment comment) {
        if ( comment == null ) {
            return null;
        }
        User user = comment.getUser();
        if ( user == null ) {
            return null;
        }
        String userName = user.getUserName();
        if ( userName == null ) {
            return null;
        }
        return userName;
    }
}
