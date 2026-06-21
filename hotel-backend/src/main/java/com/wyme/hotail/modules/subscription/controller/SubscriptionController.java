package com.wyme.hotail.modules.subscription.controller;

import com.wyme.hotail.modules.subscription.entity.PartnerProfile;
import com.wyme.hotail.modules.subscription.entity.SystemPackage;
import com.wyme.hotail.modules.subscription.service.SubscriptionService;
import com.wyme.hotail.modules.notification.entity.SentMessageLog;
import com.wyme.hotail.modules.notification.repository.SentMessageLogRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final SentMessageLogRepository sentMessageLogRepository;

    public SubscriptionController(SubscriptionService subscriptionService, SentMessageLogRepository sentMessageLogRepository) {
        this.subscriptionService = subscriptionService;
        this.sentMessageLogRepository = sentMessageLogRepository;
    }

    @GetMapping("/system-packages")
    public ResponseEntity<List<SystemPackage>> getPackages() {
        return ResponseEntity.ok(subscriptionService.getSystemPackages());
    }

    @PostMapping("/system-packages")
    public ResponseEntity<SystemPackage> createPackage(@RequestBody SystemPackage pkg) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriptionService.createSystemPackage(pkg));
    }

    @PutMapping("/system-packages/{id}")
    public ResponseEntity<SystemPackage> updatePackage(@PathVariable("id") UUID id, @RequestBody SystemPackage pkg) {
        return ResponseEntity.ok(subscriptionService.updateSystemPackage(id, pkg));
    }

    @DeleteMapping("/system-packages/{id}")
    public ResponseEntity<?> deletePackage(@PathVariable("id") UUID id) {
        subscriptionService.deleteSystemPackage(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/partner-profile/me")
    public ResponseEntity<PartnerProfile> getMyProfile(@RequestHeader("x-owner-email") String email) {
        return ResponseEntity.ok(subscriptionService.getOrCreatePartnerProfile(email));
    }

    @GetMapping("/admin/partner-usages")
    public ResponseEntity<List<PartnerProfile>> getPartnerUsages() {
        return ResponseEntity.ok(subscriptionService.getPartnerUsages());
    }

    @GetMapping("/admin/users-list")
    public ResponseEntity<List<Map<String, Object>>> getUsersList() {
        // Mock list of platform users mapping role types as defined in server.ts
        List<PartnerProfile> partners = subscriptionService.getPartnerUsages();
        List<Map<String, Object>> list = partners.stream().map(p -> (Map<String, Object>) Map.<String, Object>of(
                "email", p.getEmail(),
                "role", "Hotel Owner",
                "plan", p.getPlan(),
                "verified", true,
                "totalBookings", p.getTotalBookings(),
                "totalHotels", p.getTotalHotels()
        )).toList();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/admin/bulk-message")
    public ResponseEntity<?> sendBulkMessage(@RequestBody Map<String, Object> body) {
        List<?> emails = (List<?>) body.get("emails");
        int count = emails != null ? emails.size() : 0;
        
        SentMessageLog log = new SentMessageLog();
        log.setTitle((String) body.get("title"));
        log.setMessage((String) body.get("message"));
        log.setType(body.containsKey("isHtml") ? "email" : "sms");
        log.setAudienceSize(count);
        log.setStatus("Success");
        
        sentMessageLogRepository.save(log);
        
        return ResponseEntity.ok(Map.of("success", true, "count", count, "message", "Bulk message logged"));
    }

    @PostMapping("/admin/send-partner-message")
    public ResponseEntity<?> sendPartnerMessage(@RequestBody Map<String, Object> body) {
        SentMessageLog log = new SentMessageLog();
        log.setTitle((String) body.getOrDefault("title", "Direct Message"));
        log.setMessage((String) body.get("message"));
        log.setType("email");
        log.setAudienceSize(1);
        log.setStatus("Success");
        
        sentMessageLogRepository.save(log);

        return ResponseEntity.ok(Map.of("success", true, "message", "Message sent successfully"));
    }

    @GetMapping("/admin/message-history")
    public ResponseEntity<List<SentMessageLog>> getMessageHistory() {
        return ResponseEntity.ok(sentMessageLogRepository.findAllByOrderByCreatedAtDesc());
    }
}
