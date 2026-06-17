package com.wyme.hotail.modules.image.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    @Value("${SUPABASE_URL:}")
    private String supabaseUrl;

    @Value("${SUPABASE_KEY:}")
    private String supabaseKey;

    @Value("${SUPABASE_BUCKET:hotel-images}")
    private String bucketName;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, String> uploadImageAndThumbnail(byte[] imageBytes, String originalFilename, String contentType) throws IOException {
        if (supabaseUrl == null || supabaseUrl.isEmpty() || supabaseKey == null || supabaseKey.isEmpty()) {
            throw new IllegalStateException("Supabase storage credentials are not configured in environment variables.");
        }

        // Clean up project URL if it contains trailing slash
        String baseUrl = supabaseUrl.endsWith("/") ? supabaseUrl.substring(0, supabaseUrl.length() - 1) : supabaseUrl;

        // Extract format name (e.g. "jpg", "png")
        String formatName = "jpg";
        if (contentType != null && contentType.contains("/")) {
            formatName = contentType.substring(contentType.lastIndexOf("/") + 1);
        }
        if (formatName.equalsIgnoreCase("jpeg")) {
            formatName = "jpg";
        }

        // Generate unique file path/name
        String uniqueId = UUID.randomUUID().toString();
        String fileExt = formatName.toLowerCase();
        String mainFileName = uniqueId + "." + fileExt;
        String thumbFileName = uniqueId + "_thumb." + fileExt;

        // Generate thumbnail bytes (e.g. max width 300px)
        byte[] thumbnailBytes = generateThumbnail(imageBytes, 300, 300, formatName);

        // Upload both files
        String mainUrl = uploadToSupabase(imageBytes, mainFileName, contentType, baseUrl);
        String thumbUrl = uploadToSupabase(thumbnailBytes, thumbFileName, contentType, baseUrl);

        Map<String, String> urls = new HashMap<>();
        urls.put("url", mainUrl);
        urls.put("thumbnailUrl", thumbUrl);
        return urls;
    }

    private String uploadToSupabase(byte[] fileBytes, String fileName, String contentType, String baseUrl) {
        String uploadUrl = baseUrl + "/storage/v1/object/" + bucketName + "/" + fileName;
        String publicUrl = baseUrl + "/storage/v1/object/public/" + bucketName + "/" + fileName;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseKey);
        headers.setContentType(MediaType.parseMediaType(contentType));

        HttpEntity<byte[]> entity = new HttpEntity<>(fileBytes, headers);

        try {
            // Supabase API requires POST request to upload
            restTemplate.exchange(new URI(uploadUrl), HttpMethod.POST, entity, String.class);
            return publicUrl;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image to Supabase Storage: " + e.getMessage(), e);
        }
    }

    private byte[] generateThumbnail(byte[] originalImageBytes, int targetWidth, int targetHeight, String formatName) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(originalImageBytes);
        BufferedImage originalImage = ImageIO.read(bis);
        if (originalImage == null) {
            // If image rendering fails, just return original bytes
            return originalImageBytes;
        }

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        int newWidth = targetWidth;
        int newHeight = targetHeight;

        if (originalWidth > originalHeight) {
            newHeight = (originalHeight * targetWidth) / originalWidth;
        } else {
            newWidth = (originalWidth * targetHeight) / originalHeight;
        }

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, formatName, bos);
        return bos.toByteArray();
    }
}
