package com.example.bankingsystem.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Random;

@Service
public class PinService {
    
    private final Map<String, PinData> activePins = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Random random = new Random();
    
    public static class PinData {
        private final String pin;
        private final long createdAt;
        private boolean used;
        private boolean viewed;
        
        public PinData(String pin) {
            this.pin = pin;
            this.createdAt = System.currentTimeMillis();
            this.used = false;
            this.viewed = false;
        }
        
        public String getPin() { return pin; }
        public long getCreatedAt() { return createdAt; }
        public boolean isUsed() { return used; }
        public void setUsed(boolean used) { this.used = used; }
        public boolean isViewed() { return viewed; }
        public void setViewed(boolean viewed) { this.viewed = viewed; }
    }
    
    public String generatePin(String sessionId) {
        // Generate 6-digit PIN
        String pin = String.format("%06d", random.nextInt(1000000));
        
        // Store PIN with session ID
        PinData pinData = new PinData(pin);
        activePins.put(sessionId, pinData);
        
        // Schedule PIN expiration after 5 minutes
        scheduler.schedule(() -> {
            activePins.remove(sessionId);
        }, 5, TimeUnit.MINUTES);
        
        return pin;
    }
    
    public String viewPin(String sessionId) {
        PinData pinData = activePins.get(sessionId);
        if (pinData != null && !pinData.isUsed()) {
            pinData.setViewed(true);
            return pinData.getPin();
        }
        return null;
    }
    
    public boolean verifyPin(String sessionId, String enteredPin) {
        PinData pinData = activePins.get(sessionId);
        if (pinData != null && !pinData.isUsed() && pinData.isViewed()) {
            boolean isValid = pinData.getPin().equals(enteredPin);
            if (isValid) {
                pinData.setUsed(true);
                // Remove PIN after successful verification
                scheduler.schedule(() -> {
                    activePins.remove(sessionId);
                }, 1, TimeUnit.SECONDS);
            }
            return isValid;
        }
        return false;
    }
    
    public boolean isPinViewed(String sessionId) {
        PinData pinData = activePins.get(sessionId);
        return pinData != null && pinData.isViewed();
    }
    
    public boolean isPinUsed(String sessionId) {
        PinData pinData = activePins.get(sessionId);
        return pinData != null && pinData.isUsed();
    }
    
    public void invalidatePin(String sessionId) {
        activePins.remove(sessionId);
    }
}
