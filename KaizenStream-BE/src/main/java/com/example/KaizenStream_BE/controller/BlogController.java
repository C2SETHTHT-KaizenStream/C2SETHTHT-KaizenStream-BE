package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.dto.request.blog.BlogCreateRequest;
import com.example.KaizenStream_BE.dto.request.blog.BlogUpdateRequest;
import com.example.KaizenStream_BE.dto.respone.BlogResponse;
import com.example.KaizenStream_BE.dto.respone.blogLike.BlogLikeResponse;
import com.example.KaizenStream_BE.entity.Blog;
import com.example.KaizenStream_BE.mapper.BlogMapper;
import com.example.KaizenStream_BE.service.BlogService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/blogs")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlogController {
     BlogService blogService;
     BlogMapper blogMapper;


//     @CrossOrigin(origins = "http://localhost:5173")
//    @GetMapping
//    public ResponseEntity<List<BlogResponse>> getAllBlogs() {
//        List<BlogResponse> blogs = blogService.getAllBlogs();
//        return ResponseEntity.ok(blogs);
//    }

    @GetMapping
    public ResponseEntity<Page<BlogResponse>> getAllBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {
        Page<BlogResponse> blogs = blogService.getAllBlogsPaginated(page, size);
        return ResponseEntity.ok(blogs);
    }


    @GetMapping("/{id}")
    public ResponseEntity<BlogResponse> getBlogById(@PathVariable String id) {
//        Blog blog = blogService.getBlogById(id);
//        return ResponseEntity.ok(new BlogResponse(blog));
        BlogResponse blog = blogService.getBlogById(id);
        return ResponseEntity.ok(blog);
    }



//    @PostMapping(value = "/create", consumes = {"multipart/form-data", "application/json"})
//    public ResponseEntity<BlogResponse> createBlog(
//            @RequestPart(value = "blog", required = false) BlogCreateRequest blogRequestJson,
//            @RequestBody(required = false) BlogCreateRequest blogRequestBody,
//            @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {
//
//        BlogCreateRequest blogRequest = blogRequestJson != null ? blogRequestJson : blogRequestBody;
//
//        if (blogRequest == null) {
//            throw new IllegalArgumentException("Blog request is required");
//        }
//
//        Blog blog = blogService.createBlogWithImage(blogRequest, image);
//        BlogResponse blogResponse = blogMapper.toBlogResponse(blog);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(blogResponse);
//    }


    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<BlogResponse> createBlog(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("userId") String userId,
            @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {

        BlogCreateRequest blogRequest = new BlogCreateRequest();
        blogRequest.setUserId(userId);
        blogRequest.setTitle(title);
        blogRequest.setContent(content);

        Blog blog = blogService.createBlogWithImage(blogRequest, image);
        BlogResponse blogResponse = blogMapper.toBlogResponse(blog);

        return ResponseEntity.status(HttpStatus.CREATED).body(blogResponse);
    }


    @PutMapping("/{id}")
//    public ResponseEntity<Blog> updateBlog(@PathVariable String id, @RequestBody Blog blogDetails, @RequestParam(value = "image", required = false) MultipartFile image) throws Exception {
//        Blog updatedBlog = blogService.updateBlog(id, blogDetails, image );
//        return ResponseEntity.ok(updatedBlog);
//    }
    public ResponseEntity<BlogResponse> updateBlog(
            @PathVariable String id,
            @RequestBody BlogUpdateRequest blogRequest,
            @RequestParam(value = "image", required = false) MultipartFile image) throws Exception {
        Blog updatedBlog = blogService.updateBlog(id, blogRequest, image);
        BlogResponse blogResponse = blogMapper.toBlogResponse(updatedBlog);
        return ResponseEntity.ok(blogResponse);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable String id) {
        blogService.deleteBlog(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<List<BlogResponse>> getBlogsByCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();

        List<BlogResponse> blogs = blogService.getBlogsByUserId(userId);
        return ResponseEntity.ok(blogs);
    }


    @PutMapping("/{id}/like")
    public ResponseEntity<BlogLikeResponse> toggleLikeBlog(@PathVariable String id, @RequestParam String userId) {
        return ResponseEntity.ok(blogService.toggleLikeBlog(id, userId));
    }





//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
//    }
}