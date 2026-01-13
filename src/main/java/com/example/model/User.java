package com.example.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * User Model
 * Represents a user in the system (Patient or Guardian)
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final String ROLE_PATIENT = "PATIENT";
    public static final String ROLE_GUARDIAN = "GUARDIAN";
    
    private int id;
    private String username;
    private String password; // Raw password for simplicity or hash
    private String role;
    private String fullName;
    private String email;
    private LocalDateTime createdAt;
    private boolean active;
    
    public User() {
        this.createdAt = LocalDateTime.now();
        this.active = true;
        this.role = ROLE_PATIENT;
    }
    
    public User(String username, String password, String role, String fullName) {
        this();
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public boolean isPatient() { return ROLE_PATIENT.equals(role); }
    public boolean isGuardian() { return ROLE_GUARDIAN.equals(role); }
}
