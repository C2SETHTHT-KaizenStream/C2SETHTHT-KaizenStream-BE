package com.example.KaizenStream_BE.service;

import com.cloudinary.utils.ObjectUtils;
import com.example.KaizenStream_BE.dto.request.blog.BlogCreateRequest;
import com.example.KaizenStream_BE.dto.request.blog.BlogUpdateRequest;
import com.example.KaizenStream_BE.dto.respone.BlogResponse;
import com.example.KaizenStream_BE.dto.respone.blogLike.BlogLikeResponse;
import com.example.KaizenStream_BE.entity.Blog;
import com.example.KaizenStream_BE.entity.BlogLike;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.enums.ErrorCode;
import com.example.KaizenStream_BE.exception.AppException;
import com.example.KaizenStream_BE.mapper.BlogMapper;
import com.example.KaizenStream_BE.repository.BlogLikeRepository;
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
import java.time.LocalDateTime;
import java.util.*;
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
    BlogLikeRepository blogLikeRepository;

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
    public void deleteBlogOwner(String id, String userId) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));

        if (!Objects.equals(blog.getUser().getUserId(), userId)) {
            throw new AppException(ErrorCode.BLOG_NOT_OWNER);
        }

        blogLikeRepository.deleteByBlogId(id);
        commentRepository.deleteByBlog_BlogId(id);

        blogRepository.delete(blog);
    }

    public List <BlogResponse> getBlogsByUserId(String userId) {
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






    public Page<BlogResponse> searchBlogs(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Blog> blogPage = blogRepository.findByTitleContainingIgnoreCase(query, pageable);
        if (blogPage.isEmpty()) {
            blogPage = blogRepository.findByContentContainingIgnoreCase(query, pageable);
        }

        return blogPage.map(blogMapper::toBlogResponse);
    }



//    @Transactional
//    public void likeBlog(String blogId, String userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
//
//        // Kiểm tra blog
//        Blog blog = blogRepository.findById(blogId)
//                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));
//
//        // Kiểm tra xem người dùng đã thích blog này chưa
//        Optional<BlogLike> existingLike = blogLikeRepository.findByUserAndBlog(user, blog);
//        if (existingLike.isPresent()) {
//            throw new AppException(ErrorCode.BLOG_ALREADY_LIKED); // Nếu đã like rồi
//        }
//
//        // Tạo đối tượng BlogLike mới
//        BlogLike blogLike = new BlogLike();
//        blogLike.setUserId(userId);  // Gán userId
//        blogLike.setBlogId(blogId);  // Gán blogId
//        blogLike.setLikedAt(LocalDateTime.now());  // Gán thời gian
//
//        blogLikeRepository.save(blogLike);  // Lưu BlogLike vào cơ sở dữ liệu
//
//        // Cập nhật lại số lượng like của blog
//        blog.setLikeCount(blog.getLikeCount() + 1);
//        blogRepository.save(blog);
//    }
//
//
//
//    // Unlike một blog
//    @Transactional
//    public void unlikeBlog(String blogId, String userId) {
//        Blog blog = blogRepository.findById(blogId)
//                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
//
//        BlogLike blogLike = blogLikeRepository.findByUserAndBlog(user, blog)
//                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_LIKED));
//
//        blogLikeRepository.delete(blogLike);
//
//        blog.setLikeCount(blog.getLikeCount() - 1);
//        blogRepository.save(blog);
//    }

    @Transactional
    public void likeBlog(String blogId, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));

        Optional<BlogLike> existingLike = blogLikeRepository.findByUserAndBlog(user, blog);
        if (existingLike.isPresent()) {
            throw new AppException(ErrorCode.BLOG_ALREADY_LIKED);
        }

        // Thực hiện like
        BlogLike blogLike = new BlogLike();
        blogLike.setUserId(userId);
        blogLike.setBlogId(blogId);
        blogLike.setLikedAt(LocalDateTime.now());

        blogLikeRepository.save(blogLike);
        blog.setLikeCount(blog.getLikeCount() + 1);
        blogRepository.save(blog);
    }

    @Transactional
    public void unlikeBlog(String blogId, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));

        BlogLike blogLike = blogLikeRepository.findByUserAndBlog(user, blog)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_LIKED));

        blogLikeRepository.delete(blogLike);
        blog.setLikeCount(blog.getLikeCount() - 1);
        blogRepository.save(blog);
    }


    public List<BlogLikeResponse> getLikes(String blogId) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));

        List<BlogLike> blogLikes = blogLikeRepository.findByBlog(blog);
        return blogLikes.stream()
                .map(blogLike -> new BlogLikeResponse("Liked", true, blog.getLikeCount()))
                .collect(Collectors.toList());
    }




}

