package com.example.bankingsystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "twilio")
public class TwilioConfig {
    
    private Account account = new Account();
    private Auth auth = new Auth();
    private Phone phone = new Phone();
    private boolean enabled = false;
    
    public static class Account {
        private String sid;
        
        public String getSid() {
            return sid;
        }
        
        public void setSid(String sid) {
            this.sid = sid;
        }
    }
    
    public static class Auth {
        private String token;
        
        public String getToken() {
            return token;
        }
        
        public void setToken(String token) {
            this.token = token;
        }
    }
    
    public static class Phone {
        private String number;
        
        public String getNumber() {
            return number;
        }
        
        public void setNumber(String number) {
            this.number = number;
        }
    }
    
    // Getters and Setters
    public Account getAccount() {
        return account;
    }
    
    public void setAccount(Account account) {
        this.account = account;
    }
    
    public Auth getAuth() {
        return auth;
    }
    
    public void setAuth(Auth auth) {
        this.auth = auth;
    }
    
    public Phone getPhone() {
        return phone;
    }
    
    public void setPhone(Phone phone) {
        this.phone = phone;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
