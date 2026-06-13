package com.example.mediai.data.model;

public class Medication {
    private long id;
    private long userId;
    private String name;
    private String dose;
    private String time;
    private String frequency;
    private String notes;
    private boolean active;
    private long createdAt;

    public Medication() {}

    public Medication(long id, long userId, String name, String dose, String time,
                      String frequency, String notes, boolean active, long createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.dose = dose;
        this.time = time;
        this.frequency = frequency;
        this.notes = notes;
        this.active = active;
        this.createdAt = createdAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDose() { return dose; }
    public void setDose(String dose) { this.dose = dose; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}