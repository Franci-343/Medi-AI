package com.example.mediai.data.local.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mediai.data.local.DatabaseContract.UserEntry;
import com.example.mediai.data.local.DatabaseHelper;
import com.example.mediai.data.model.User;
import com.example.mediai.util.DateTimeUtils;
import com.example.mediai.util.HashUtils;

public class UserDao {
    private DatabaseHelper databaseHelper;

    public UserDao(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public long insert(User user) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserEntry.COL_FULL_NAME, user.getFullName());
        values.put(UserEntry.COL_EMAIL, user.getEmail());
        values.put(UserEntry.COL_PASSWORD_HASH, user.getPasswordHash());
        values.put(UserEntry.COL_CREATED_AT, user.getCreatedAt() != 0 ? user.getCreatedAt() : DateTimeUtils.getCurrentTimestamp());
        return db.insert(UserEntry.TABLE_NAME, null, values);
    }

    public User findByEmail(String email) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(UserEntry.TABLE_NAME, null,
                    UserEntry.COL_EMAIL + " = ?",
                    new String[]{email}, null, null, null);
            if (cursor.moveToFirst()) {
                return cursorToUser(cursor);
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public User findById(long id) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(UserEntry.TABLE_NAME, null,
                    UserEntry.COL_ID + " = ?",
                    new String[]{String.valueOf(id)}, null, null, null);
            if (cursor.moveToFirst()) {
                return cursorToUser(cursor);
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public User authenticate(String email, String password) {
        User user = findByEmail(email);
        if (user != null) {
            String hashedInput = HashUtils.sha256(password);
            if (hashedInput != null && hashedInput.equals(user.getPasswordHash())) {
                return user;
            }
        }
        return null;
    }

    public boolean emailExists(String email) {
        return findByEmail(email) != null;
    }

    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(UserEntry.COL_ID)));
        user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COL_FULL_NAME)));
        user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COL_EMAIL)));
        user.setPasswordHash(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COL_PASSWORD_HASH)));
        user.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(UserEntry.COL_CREATED_AT)));
        return user;
    }
}