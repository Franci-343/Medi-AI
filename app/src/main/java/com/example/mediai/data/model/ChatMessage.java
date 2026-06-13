package com.example.mediai.data.model;

public class ChatMessage {
    private long id;
    private long userId;
    private String role; // system, user, assistant
    private String content;
    private long createdAt;
    private String provider; // MISTRAL, DEEPSEEK, DEMO

    public ChatMessage() {}

    public ChatMessage(long id, long userId, String role, String content,
                       long createdAt, String provider) {
        this.id = id;
        this.userId = userId;
        this.role = role;
        this.content = content;
        this.createdAt = createdAt;
        this.provider = provider;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
}