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

    @GetMapping
    public ResponseEntity<Page<BlogResponse>> getAllBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {
        Page<BlogResponse> blogs = blogService.getAllBlogsPaginated(page, size);
        return ResponseEntity.ok(blogs);
    }


    @GetMapping("/{id}")
    public ResponseEntity<BlogResponse> getBlogById(@PathVariable String id) {
        BlogResponse blog = blogService.getBlogById(id);
        return ResponseEntity.ok(blog);
    }






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
    public ResponseEntity<Void> deleteBlogOwner(@PathVariable String id, @RequestParam String userId) {
        blogService.deleteBlogOwner(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<List<BlogResponse>> getBlogsByCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();

        List<BlogResponse> blogs = blogService.getBlogsByUserId(userId);
        return ResponseEntity.ok(blogs);
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BlogResponse>> getBlogsByUserId(@PathVariable String userId) {
        List<BlogResponse> blogs = blogService.getBlogsByUserId(userId);
        return ResponseEntity.ok(blogs);
    }

    @PostMapping("/{id}/like/{userId}")
    public ResponseEntity<BlogLikeResponse> likeBlog(@PathVariable String id, @PathVariable String userId) {
        blogService.likeBlog(id, userId);

        BlogResponse blog = blogService.getBlogById(id);
        BlogLikeResponse response = new BlogLikeResponse("Liked", true, blog.getLikeCount());

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}/unlike/{userId}")
    public ResponseEntity<BlogLikeResponse> unlikeBlog(@PathVariable String id, @PathVariable String userId) {
        System.out.println("Received userId: " + userId); // Debugging
        blogService.unlikeBlog(id, userId);

        BlogResponse blog = blogService.getBlogById(id);
        BlogLikeResponse response = new BlogLikeResponse("Unliked", false, blog.getLikeCount());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/likes")
    public ResponseEntity<List<BlogLikeResponse>> getLikes(@PathVariable String id) {
        List<BlogLikeResponse> likes = blogService.getLikes(id);
        return ResponseEntity.ok(likes);
    }












}