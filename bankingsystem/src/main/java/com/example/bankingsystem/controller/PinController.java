package com.example.bankingsystem.controller;

import com.example.bankingsystem.service.PinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pin")
@CrossOrigin(origins = "*")
public class PinController {
    
    @Autowired
    private PinService pinService;
    
    @PostMapping("/generate")
    public ResponseEntity<?> generatePin(@RequestParam String sessionId) {
        try {
            String pin = pinService.generatePin(sessionId);
            Map<String, Object> response = new HashMap<>();
            response.put("sessionId", sessionId);
            response.put("message", "PIN generated successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/view/{sessionId}")
    public ResponseEntity<?> viewPin(@PathVariable String sessionId) {
        try {
            String pin = pinService.viewPin(sessionId);
            if (pin != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("pin", pin);
                response.put("message", "PIN can only be viewed once. Please note it down.");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("PIN not found, expired, or already used", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPin(@RequestParam String sessionId, @RequestParam String pin) {
        try {
            boolean isValid = pinService.verifyPin(sessionId, pin);
            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);
            response.put("message", isValid ? "PIN verified successfully" : "Invalid PIN");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/status/{sessionId}")
    public ResponseEntity<?> getPinStatus(@PathVariable String sessionId) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("viewed", pinService.isPinViewed(sessionId));
            response.put("used", pinService.isPinUsed(sessionId));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
