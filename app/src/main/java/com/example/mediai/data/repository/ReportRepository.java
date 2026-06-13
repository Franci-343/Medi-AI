package com.example.mediai.data.repository;

import com.example.mediai.data.local.dao.DoseLogDao;
import com.example.mediai.data.local.dao.MedicationDao;
import com.example.mediai.data.model.DoseLog;
import com.example.mediai.util.DateTimeUtils;

import java.util.List;

public class ReportRepository {
    private DoseLogDao doseLogDao;
    private MedicationDao medicationDao;

    public ReportRepository(DoseLogDao doseLogDao, MedicationDao medicationDao) {
        this.doseLogDao = doseLogDao;
        this.medicationDao = medicationDao;
    }

    public int getWeeklyTakenCount(long userId) {
        long startOfWeek = DateTimeUtils.getStartOfWeekTimestamp();
        long now = DateTimeUtils.getCurrentTimestamp();
        return doseLogDao.getTakenCountByUserIdAndPeriod(userId, startOfWeek, now);
    }

    public int getWeeklyMissedCount(long userId) {
        long startOfWeek = DateTimeUtils.getStartOfWeekTimestamp();
        long now = DateTimeUtils.getCurrentTimestamp();
        return doseLogDao.getMissedCountByUserIdAndPeriod(userId, startOfWeek, now);
    }

    public int getAdherencePercentage(long userId) {
        int taken = getWeeklyTakenCount(userId);
        int missed = getWeeklyMissedCount(userId);
        int total = taken + missed;
        if (total == 0) return 0;
        return (int) Math.round(((double) taken / total) * 100);
    }

    public List<DoseLog> getRecentLogs(long userId, int limit) {
        return doseLogDao.findRecentByUserId(userId, limit);
    }

    public List<DoseLog> getWeeklyLogs(long userId) {
        long startOfWeek = DateTimeUtils.getStartOfWeekTimestamp();
        long now = DateTimeUtils.getCurrentTimestamp();
        return doseLogDao.findByUserIdAndPeriod(userId, startOfWeek, now);
    }

    public List<DoseLog> getAllLogs(long userId) {
        return doseLogDao.findByUserId(userId);
    }
}