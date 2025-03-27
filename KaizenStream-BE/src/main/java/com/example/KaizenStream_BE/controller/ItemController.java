package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.dto.request.status.UpdateStatusRequest;
import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.entity.Item;
import com.example.KaizenStream_BE.entity.Report;
import com.example.KaizenStream_BE.service.ItemService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("/item")
public class ItemController {
    ItemService itemService;
    // Thêm 1 Item
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Item>>  addItem(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") String price,
            @RequestParam("status") String status,
            @RequestParam("image") MultipartFile imageFile
    ) throws IOException {
        Item item = itemService.addItem(name, description, price, status, imageFile);
        return ResponseEntity.ok(
                ApiResponse.<Item>builder()
                        .code(1000)
                        .message("true")
                        .result(item)
                        .build()
        );
    }
    //Tìm 1 item theo id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Item>>getItemById(@PathVariable String id){
        Item item = itemService.getItemById(id);
        return ResponseEntity.ok(
                ApiResponse.<Item>builder()
                        .code(1000)
                        .message("true")
                        .result(item)
                        .build()
        );
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Item>> updateItem(
            @PathVariable String id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") String price,
            @RequestParam("status") String status,
            @RequestParam(value = "image", required = false) MultipartFile imageFile
    ) throws IOException {
        Item updatedItem = itemService.updateItem(id, name, description, price, status, imageFile);
        return ResponseEntity.ok(
                ApiResponse.<Item>builder()
                        .code(1000)
                        .message("true")
                        .result(updatedItem)
                        .build()
        );
    }

    // 4️⃣ Lấy danh sách tất cả Item
   @GetMapping("/all")
   public ResponseEntity<ApiResponse<List<Item>>> getAllItems() {
       List<Item> items = itemService.getAllItems();

       return ResponseEntity.ok(
               ApiResponse.<List<Item>>builder()
                       .code(1000)
                       .message("Get All Items successfully !")
                       .result(items)
                       .build()
       );
   }

   // Cập nhật status
   @PutMapping("/status")
   public ResponseEntity<?> updateStatus(@RequestBody UpdateStatusRequest request) {
       String itemId = request.getItemId(); // Lấy giá trị từ DTO
       String status = request.getStatus();

       try {
           Item updatedItem = itemService.updateItemStatus(itemId, status);
           return ResponseEntity.ok( ApiResponse.<Item>builder()
                   .code(1000)
                   .message("true")
                   .build());
       } catch (RuntimeException e) {
           return ResponseEntity.badRequest().body(e.getMessage());
       }
   }

}
