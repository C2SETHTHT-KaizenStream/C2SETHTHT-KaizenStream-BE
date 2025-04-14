package com.example.KaizenStream_BE.service;

import com.cloudinary.utils.ObjectUtils;
import com.example.KaizenStream_BE.dto.request.blog.BlogCreateRequest;
import com.example.KaizenStream_BE.dto.request.blog.BlogUpdateRequest;
import com.example.KaizenStream_BE.dto.respone.BlogResponse;
import com.example.KaizenStream_BE.entity.Blog;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.enums.ErrorCode;
import com.example.KaizenStream_BE.exception.AppException;
import com.example.KaizenStream_BE.mapper.BlogMapper;
import com.example.KaizenStream_BE.repository.BlogRepository;
import com.example.KaizenStream_BE.repository.CommentRepository;
import com.example.KaizenStream_BE.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlogService {
    BlogRepository blogRepository;
    CommentRepository commentRepository;
    UserRepository userRepository;
    Cloudinary cloudinary;
    BlogMapper blogMapper;


//    public List<BlogResponse> getAllBlogs() {
//        return blogRepository.findAll()
//                .stream()
//                .map(blogMapper::toBlogResponse)
//                .toList();
//    }

//    public Page<BlogResponse> getAllBlogsPaginated(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        return blogRepository.findAll(pageable).map(blogMapper::toBlogResponse);
//    }

    public Page<BlogResponse> getAllBlogsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Blog> blogPage = blogRepository.findAll(pageable);
        return blogRepository.findAll(pageable).map(blogMapper::toBlogResponse);
    }



    public BlogResponse getBlogById(String id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));
        return blogMapper.toBlogResponse(blog);
    }



//    @Transactional
//    public Blog createBlogWithImage(Blog blog, MultipartFile image) throws Exception {
//        if (blog.getTitle() == null || blog.getTitle().isBlank()) {
//            throw new AppException(ErrorCode.BLOG_REQUIRED_TITLE);
//        }
//        if (image != null && !image.isEmpty()) {
//            String contentType = image.getContentType();
//            if (contentType == null || !contentType.startsWith("image/")) {
//                throw new IllegalArgumentException("File phải là định dạng ảnh (JPEG, PNG, v.v.)");
//            }
//
//            try {
//                Map<String, Object> uploadParams = new HashMap<>();
//                uploadParams.put("resource_type", "image");
//
//                Map uploadResult = cloudinary.uploader().upload(image.getBytes(), uploadParams);
//                String imageUrl = (String) uploadResult.get("url");
//                blog.setImageUrl(imageUrl);
//            } catch (IOException e) {
//                throw new RuntimeException("Lỗi khi upload ảnh lên Cloudinary: " + e.getMessage(), e);
//            }
//        }
//        return blogRepository.save(blog);
//    }

    @Transactional
    public Blog createBlogWithImage (BlogCreateRequest blogCreateRequest, MultipartFile image) throws Exception
    {
        User user = userRepository.findById(blogCreateRequest.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        Blog blog = blogMapper.toBlog(blogCreateRequest);
        blog.setUser(user);

        if (image != null && !image.isEmpty()) {
            String imageUrl = uploadImage(image);
            blog.setImageUrl(imageUrl);
        }

        return blogRepository.save(blog);
    }




//    public Blog updateBlog(String blogId, Blog blogDetails, MultipartFile image) throws Exception {
//        Blog existingBlog = blogRepository.findById(blogId)
//                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy blog với ID: " + blogId));
//
//        if (blogDetails.getTitle() != null && !blogDetails.getTitle().isBlank()) {
//            existingBlog.setTitle(blogDetails.getTitle());
//        }
//        if (blogDetails.getContent() != null) {
//            existingBlog.setContent(blogDetails.getContent());
//        }
//
//        return blogRepository.save(existingBlog);
//    }
    @Transactional
    public Blog updateBlog(String blogId, BlogUpdateRequest blogUpdateRequest, MultipartFile image) throws Exception {
        Blog existingBlog = blogRepository.findById(blogId)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));

        // Cập nhật thông tin blog từ request
        blogMapper.updateBlogFromRequest(blogUpdateRequest, existingBlog);

        if (image != null && !image.isEmpty()) {
            String imageUrl = uploadImage(image);
            existingBlog.setImageUrl(imageUrl);
        }

        return blogRepository.save(existingBlog);
    }


    @Transactional
    public void deleteBlog(String id) {
        if (!blogRepository.existsById(id)) {
            throw new AppException(ErrorCode.BLOG_NOT_FOUND);
        }
        blogRepository.deleteById(id);
    }

    public List <BlogResponse> getBlogsByUserId(String userId) {
//        return blogRepository.findByUser_UserId(userId)
//                .stream()
//                .map(BlogResponse::new)
//                .toList();
        List<Blog> blogs = blogRepository.findByUser_UserId(userId);
        return blogs.stream().map(blogMapper::toBlogResponse).collect(Collectors.toList());
    }

    private String uploadImage(MultipartFile image) throws Exception {
        // Upload ảnh lên Cloudinary
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new AppException(ErrorCode.IMAGE_NOT_FOUND);
        }

        Map<String, Object> uploadParams = new HashMap<>();
        uploadParams.put("resource_type", "image");

        Map uploadResult = cloudinary.uploader().upload(image.getBytes(), uploadParams);
        return (String) uploadResult.get("url");
    }

    }

