package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.entity.Item;
import com.example.KaizenStream_BE.enums.StatusItem;
import com.example.KaizenStream_BE.repository.ItemRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class ItemService {
    ItemRepository itemRepository;
    CloudinaryService cloudinaryService;
    // 1️⃣ Thêm một Item
    public Item addItem(String name, String description, String price, String status, MultipartFile imageFile) throws IOException {
        String imageUrl = cloudinaryService.uploadImage(imageFile); // Upload ảnh lên Cloudinary
        // Tạo Item mới
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setStatus(StatusItem.valueOf(status.toUpperCase()));
        item.setImage(imageUrl); // Lưu link ảnh vào database
        return itemRepository.save(item);
    }


    public Item getItemById(String id) {
        return itemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item không tồn tại với ID: " + id));
    }
    // Lấy tất cả Item
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item updateItem(String id, String name, String description, String price, String status, MultipartFile imageFile) throws IOException {
        String imageUrl = cloudinaryService.uploadImage(imageFile);
        return itemRepository.findById(id).map(item -> {
            item.setName(name);
            item.setDescription(description);
            item.setPrice(price);
            item.setStatus(StatusItem.valueOf(status.toUpperCase()));
            item.setImage(imageUrl);
            return itemRepository.save(item);
        }).orElseThrow(() -> new RuntimeException("Item không tồn tại với ID: " + id));
    }

    // Cập nhật status item
    public Item updateItemStatus(String id, String status) {
        // Tìm item theo ID
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item không tồn tại với ID: " + id));
        // Cập nhật trạng thái (Active hoặc Inactive)
        StatusItem newStatus = StatusItem.valueOf(status.toUpperCase());
        item.setStatus(newStatus);

        // Lưu thay đổi vào database
        return itemRepository.save(item);
    }

    // Lấy các item active
    public List<Item> getItemsByStatus(StatusItem status) {
        return itemRepository.findByStatus(status);
    }

    //Xóa Item khỏi database
    public void deleteItem(String id) {
        if (!itemRepository.existsById(id)) {
            throw new RuntimeException("Item not found");
        }
        itemRepository.deleteById(id);
    }
}
