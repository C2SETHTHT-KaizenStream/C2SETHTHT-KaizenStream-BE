package com.example.KaizenStream_BE.mapper;

import com.example.KaizenStream_BE.dto.request.blog.BlogCreateRequest;
import com.example.KaizenStream_BE.dto.request.blog.BlogUpdateRequest;
import com.example.KaizenStream_BE.dto.respone.BlogResponse;
import com.example.KaizenStream_BE.entity.Blog;
import com.example.KaizenStream_BE.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
@Component
public class BlogMapperImpl implements BlogMapper {

    @Override
    public BlogResponse toBlogResponse(Blog blog) {
        if ( blog == null ) {
            return null;
        }

        Blog blog1 = null;

        BlogResponse blogResponse = new BlogResponse( blog1 );

        blogResponse.setUserId( blogUserUserId( blog ) );
        blogResponse.setUserName( blogUserUserName( blog ) );
        blogResponse.setBlogId( blog.getBlogId() );
        blogResponse.setTitle( blog.getTitle() );
        blogResponse.setContent( blog.getContent() );
        blogResponse.setCreateAt( blog.getCreateAt() );
        blogResponse.setUpdateAt( blog.getUpdateAt() );
        blogResponse.setLikeCount( blog.getLikeCount() );
        blogResponse.setImageUrl( blog.getImageUrl() );

        blogResponse.setCommentCount( blog.getComments().size() );

        return blogResponse;
    }

    @Override
    public Blog toBlog(BlogCreateRequest request) {
        if ( request == null ) {
            return null;
        }

        Blog blog = new Blog();

        blog.setTitle( request.getTitle() );
        blog.setContent( request.getContent() );

        return blog;
    }

    @Override
    public void updateBlogFromRequest(BlogUpdateRequest request, Blog blog) {
        if ( request == null ) {
            return;
        }

        blog.setTitle( request.getTitle() );
        blog.setContent( request.getContent() );
    }

    private String blogUserUserId(Blog blog) {
        if ( blog == null ) {
            return null;
        }
        User user = blog.getUser();
        if ( user == null ) {
            return null;
        }
        String userId = user.getUserId();
        if ( userId == null ) {
            return null;
        }
        return userId;
    }

    private String blogUserUserName(Blog blog) {
        if ( blog == null ) {
            return null;
        }
        User user = blog.getUser();
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
