package com.example.mediai.data.local.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mediai.data.local.DatabaseContract.MedicationEntry;
import com.example.mediai.data.local.DatabaseHelper;
import com.example.mediai.data.model.Medication;
import com.example.mediai.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class MedicationDao {
    private DatabaseHelper databaseHelper;

    public MedicationDao(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public long insert(Medication medication) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MedicationEntry.COL_USER_ID, medication.getUserId());
        values.put(MedicationEntry.COL_NAME, medication.getName());
        values.put(MedicationEntry.COL_DOSE, medication.getDose());
        values.put(MedicationEntry.COL_TIME, medication.getTime());
        values.put(MedicationEntry.COL_FREQUENCY, medication.getFrequency());
        values.put(MedicationEntry.COL_NOTES, medication.getNotes());
        values.put(MedicationEntry.COL_ACTIVE, medication.isActive() ? 1 : 0);
        values.put(MedicationEntry.COL_CREATED_AT, medication.getCreatedAt() != 0 ? medication.getCreatedAt() : DateTimeUtils.getCurrentTimestamp());
        return db.insert(MedicationEntry.TABLE_NAME, null, values);
    }

    public int update(Medication medication) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MedicationEntry.COL_NAME, medication.getName());
        values.put(MedicationEntry.COL_DOSE, medication.getDose());
        values.put(MedicationEntry.COL_TIME, medication.getTime());
        values.put(MedicationEntry.COL_FREQUENCY, medication.getFrequency());
        values.put(MedicationEntry.COL_NOTES, medication.getNotes());
        values.put(MedicationEntry.COL_ACTIVE, medication.isActive() ? 1 : 0);
        return db.update(MedicationEntry.TABLE_NAME, values,
                MedicationEntry.COL_ID + " = ?",
                new String[]{String.valueOf(medication.getId())});
    }

    public int delete(long medicationId) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        return db.delete(MedicationEntry.TABLE_NAME,
                MedicationEntry.COL_ID + " = ?",
                new String[]{String.valueOf(medicationId)});
    }

    public Medication findById(long medicationId) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(MedicationEntry.TABLE_NAME, null,
                    MedicationEntry.COL_ID + " = ?",
                    new String[]{String.valueOf(medicationId)}, null, null, null);
            if (cursor.moveToFirst()) {
                return cursorToMedication(cursor);
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public List<Medication> findByUserId(long userId) {
        List<Medication> medications = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(MedicationEntry.TABLE_NAME, null,
                    MedicationEntry.COL_USER_ID + " = ? AND " + MedicationEntry.COL_ACTIVE + " = 1",
                    new String[]{String.valueOf(userId)}, null, null,
                    MedicationEntry.COL_TIME + " ASC");
            while (cursor.moveToNext()) {
                medications.add(cursorToMedication(cursor));
            }
            return medications;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public List<Medication> getAllByUserId(long userId) {
        List<Medication> medications = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(MedicationEntry.TABLE_NAME, null,
                    MedicationEntry.COL_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}, null, null,
                    MedicationEntry.COL_TIME + " ASC");
            while (cursor.moveToNext()) {
                medications.add(cursorToMedication(cursor));
            }
            return medications;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public int getActiveCountByUserId(long userId) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + MedicationEntry.TABLE_NAME +
                    " WHERE " + MedicationEntry.COL_USER_ID + " = ? AND " +
                    MedicationEntry.COL_ACTIVE + " = 1", new String[]{String.valueOf(userId)});
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

    private Medication cursorToMedication(Cursor cursor) {
        Medication medication = new Medication();
        medication.setId(cursor.getLong(cursor.getColumnIndexOrThrow(MedicationEntry.COL_ID)));
        medication.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(MedicationEntry.COL_USER_ID)));
        medication.setName(cursor.getString(cursor.getColumnIndexOrThrow(MedicationEntry.COL_NAME)));
        medication.setDose(cursor.getString(cursor.getColumnIndexOrThrow(MedicationEntry.COL_DOSE)));
        medication.setTime(cursor.getString(cursor.getColumnIndexOrThrow(MedicationEntry.COL_TIME)));
        medication.setFrequency(cursor.getString(cursor.getColumnIndexOrThrow(MedicationEntry.COL_FREQUENCY)));
        medication.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(MedicationEntry.COL_NOTES)));
        medication.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(MedicationEntry.COL_ACTIVE)) == 1);
        medication.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(MedicationEntry.COL_CREATED_AT)));
        return medication;
    }
}