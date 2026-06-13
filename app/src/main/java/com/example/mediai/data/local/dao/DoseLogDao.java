package com.example.mediai.data.local.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mediai.data.local.DatabaseContract.DoseLogEntry;
import com.example.mediai.data.local.DatabaseHelper;
import com.example.mediai.data.model.DoseLog;
import com.example.mediai.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class DoseLogDao {
    private DatabaseHelper databaseHelper;

    public DoseLogDao(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public long insert(DoseLog doseLog) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DoseLogEntry.COL_MEDICATION_ID, doseLog.getMedicationId());
        values.put(DoseLogEntry.COL_USER_ID, doseLog.getUserId());
        values.put(DoseLogEntry.COL_SCHEDULED_AT, doseLog.getScheduledAt());
        values.put(DoseLogEntry.COL_STATUS, doseLog.getStatus());
        values.put(DoseLogEntry.COL_REGISTERED_AT, doseLog.getRegisteredAt() != 0 ? doseLog.getRegisteredAt() : DateTimeUtils.getCurrentTimestamp());
        return db.insert(DoseLogEntry.TABLE_NAME, null, values);
    }

    public int update(DoseLog doseLog) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DoseLogEntry.COL_STATUS, doseLog.getStatus());
        values.put(DoseLogEntry.COL_REGISTERED_AT, doseLog.getRegisteredAt());
        return db.update(DoseLogEntry.TABLE_NAME, values,
                DoseLogEntry.COL_ID + " = ?",
                new String[]{String.valueOf(doseLog.getId())});
    }

    public DoseLog findByMedicationAndSchedule(long medicationId, long scheduledAt) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DoseLogEntry.TABLE_NAME, null,
                    DoseLogEntry.COL_MEDICATION_ID + " = ? AND " +
                            DoseLogEntry.COL_SCHEDULED_AT + " = ?",
                    new String[]{String.valueOf(medicationId), String.valueOf(scheduledAt)},
                    null, null, null);
            if (cursor.moveToFirst()) {
                return cursorToDoseLog(cursor);
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public List<DoseLog> findByUserId(long userId) {
        List<DoseLog> logs = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DoseLogEntry.TABLE_NAME, null,
                    DoseLogEntry.COL_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}, null, null,
                    DoseLogEntry.COL_SCHEDULED_AT + " DESC");
            while (cursor.moveToNext()) {
                logs.add(cursorToDoseLog(cursor));
            }
            return logs;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public List<DoseLog> findByUserIdAndPeriod(long userId, long startTimestamp, long endTimestamp) {
        List<DoseLog> logs = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DoseLogEntry.TABLE_NAME, null,
                    DoseLogEntry.COL_USER_ID + " = ? AND " +
                            DoseLogEntry.COL_SCHEDULED_AT + " >= ? AND " +
                            DoseLogEntry.COL_SCHEDULED_AT + " <= ?",
                    new String[]{String.valueOf(userId), String.valueOf(startTimestamp), String.valueOf(endTimestamp)},
                    null, null, DoseLogEntry.COL_SCHEDULED_AT + " DESC");
            while (cursor.moveToNext()) {
                logs.add(cursorToDoseLog(cursor));
            }
            return logs;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public List<DoseLog> findRecentByUserId(long userId, int limit) {
        List<DoseLog> logs = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DoseLogEntry.TABLE_NAME, null,
                    DoseLogEntry.COL_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}, null, null,
                    DoseLogEntry.COL_SCHEDULED_AT + " DESC", String.valueOf(limit));
            while (cursor.moveToNext()) {
                logs.add(cursorToDoseLog(cursor));
            }
            return logs;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public int getTakenCountByUserIdAndPeriod(long userId, long startTimestamp, long endTimestamp) {
        return getStatusCountByUserIdAndPeriod(userId, "TAKEN", startTimestamp, endTimestamp);
    }

    public int getMissedCountByUserIdAndPeriod(long userId, long startTimestamp, long endTimestamp) {
        return getStatusCountByUserIdAndPeriod(userId, "MISSED", startTimestamp, endTimestamp);
    }

    private int getStatusCountByUserIdAndPeriod(long userId, String status, long startTimestamp, long endTimestamp) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + DoseLogEntry.TABLE_NAME +
                            " WHERE " + DoseLogEntry.COL_USER_ID + " = ? AND " +
                            DoseLogEntry.COL_STATUS + " = ? AND " +
                            DoseLogEntry.COL_SCHEDULED_AT + " >= ? AND " +
                            DoseLogEntry.COL_SCHEDULED_AT + " <= ?",
                    new String[]{String.valueOf(userId), status, String.valueOf(startTimestamp), String.valueOf(endTimestamp)});
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private DoseLog cursorToDoseLog(Cursor cursor) {
        DoseLog doseLog = new DoseLog();
        doseLog.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DoseLogEntry.COL_ID)));
        doseLog.setMedicationId(cursor.getLong(cursor.getColumnIndexOrThrow(DoseLogEntry.COL_MEDICATION_ID)));
        doseLog.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(DoseLogEntry.COL_USER_ID)));
        doseLog.setScheduledAt(cursor.getLong(cursor.getColumnIndexOrThrow(DoseLogEntry.COL_SCHEDULED_AT)));
        doseLog.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(DoseLogEntry.COL_STATUS)));
        doseLog.setRegisteredAt(cursor.getLong(cursor.getColumnIndexOrThrow(DoseLogEntry.COL_REGISTERED_AT)));
        return doseLog;
    }
}