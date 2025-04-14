package com.example.KaizenStream_BE.mapper;

import com.example.KaizenStream_BE.dto.request.blog.BlogCreateRequest;
import com.example.KaizenStream_BE.dto.request.blog.BlogUpdateRequest;
import com.example.KaizenStream_BE.dto.respone.BlogResponse;
import com.example.KaizenStream_BE.entity.Blog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BlogMapper {

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.userName", target = "userName")
    @Mapping(expression = "java(blog.getComments().size())", target = "commentCount")
   BlogResponse toBlogResponse(Blog blog);
    default BlogResponse toBlogResponseSafe(Blog blog) {
        if (blog == null) {
            return null;
        }
        return toBlogResponse(blog);
    }
    @Mapping(target = "user", ignore = true) // Bỏ qua trường user
    @Mapping(target = "blogId", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Blog toBlog(BlogCreateRequest request);

    void updateBlogFromRequest(BlogUpdateRequest request, @MappingTarget Blog blog);

}
