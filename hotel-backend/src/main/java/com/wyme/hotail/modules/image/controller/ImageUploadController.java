package com.wyme.hotail.modules.image.controller;

import com.wyme.hotail.modules.image.service.SupabaseStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class ImageUploadController {

    private final SupabaseStorageService supabaseStorageService;

    public ImageUploadController(SupabaseStorageService supabaseStorageService) {
        this.supabaseStorageService = supabaseStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Uploaded file is empty"));
        }

        try {
            Map<String, String> urls = supabaseStorageService.uploadImageAndThumbnail(
                    file.getBytes(),
                    file.getOriginalFilename(),
                    file.getContentType()
            );
            return ResponseEntity.ok(urls);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Image upload failed: " + e.getMessage()));
        }
    }
}
