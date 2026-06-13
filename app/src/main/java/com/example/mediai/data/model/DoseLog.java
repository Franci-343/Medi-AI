package com.example.mediai.data.model;

public class DoseLog {
    private long id;
    private long medicationId;
    private long userId;
    private long scheduledAt;
    private String status; // TAKEN, MISSED, PENDING
    private long registeredAt;

    public DoseLog() {}

    public DoseLog(long id, long medicationId, long userId, long scheduledAt,
                   String status, long registeredAt) {
        this.id = id;
        this.medicationId = medicationId;
        this.userId = userId;
        this.scheduledAt = scheduledAt;
        this.status = status;
        this.registeredAt = registeredAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getMedicationId() { return medicationId; }
    public void setMedicationId(long medicationId) { this.medicationId = medicationId; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public long getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(long scheduledAt) { this.scheduledAt = scheduledAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(long registeredAt) { this.registeredAt = registeredAt; }
}