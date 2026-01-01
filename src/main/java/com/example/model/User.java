package com.example.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * User Model
 * Represents a user in the system (Patient or Guardian)
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum Role {
        PATIENT,    // Patient manages their own medicines and records doses
        GUARDIAN    // Guardian monitors patient(s) and receives notifications
    }
    
    private int id;
    private String username;
    private String passwordHash;
    private Role role;
    private String fullName;
    private String email;
    private LocalDateTime createdAt;
    private boolean active;
    
    // Constructors
    public User() {
        this.createdAt = LocalDateTime.now();
        this.active = true;
        this.role = Role.PATIENT; // Default to patient for backward compatibility
    }
    
    public User(String username, String passwordHash) {
        this();
        this.username = username;
        this.passwordHash = passwordHash;
    }
    
    public User(String username, String passwordHash, Role role, String fullName, String email) {
        this();
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isPatient() {
        return role == Role.PATIENT;
    }
    
    public boolean isGuardian() {
        return role == Role.GUARDIAN;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +
                '}';
    }
}
