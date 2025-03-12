package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.respone.BlogResponse;
import com.example.KaizenStream_BE.entity.Blog;
import com.example.KaizenStream_BE.repository.BlogRepository;
import com.example.KaizenStream_BE.repository.CommentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlogService {
    BlogRepository blogRepository;
    CommentRepository commentRepository;


    public List<BlogResponse> getAllBlogs() {
        return blogRepository.findAll()
                .stream()
                .map(BlogResponse::new)
                .toList();
    }

    public Page<BlogResponse> getAllBlogsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Blog> blogPage = blogRepository.findAll(pageable);
        return blogPage.map(BlogResponse::new);
    }

    public Blog getBlogById(String id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy blog với ID: " + id));
    }

    @Transactional
    public Blog createBlog(Blog blog) {
        if (blog.getTitle() == null || blog.getTitle().isBlank()) {
            throw new IllegalArgumentException("Tiêu đề blog không được để trống");
        }
//        if (blog.getUser() == null) {
//            throw new IllegalArgumentException("Blog phải có người tạo");
//        }
        return blogRepository.save(blog);
    }




    @Transactional
    public Blog updateBlog(String blogId, Blog blogDetails) {
        Blog existingBlog = blogRepository.findById(blogId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy blog với ID: " + blogId));

        if (blogDetails.getTitle() != null && !blogDetails.getTitle().isBlank()) {
            existingBlog.setTitle(blogDetails.getTitle());
        }
        if (blogDetails.getContent() != null) {
            existingBlog.setContent(blogDetails.getContent());
        }

        return blogRepository.save(existingBlog);
    }


    @Transactional
    public void deleteBlog(String id) {
        if (!blogRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy blog với ID: " + id);
        }
        blogRepository.deleteById(id);
    }
}