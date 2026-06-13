package com.example.mediai.data.local.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mediai.data.local.DatabaseContract.ChatMessageEntry;
import com.example.mediai.data.local.DatabaseHelper;
import com.example.mediai.data.model.ChatMessage;
import com.example.mediai.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageDao {
    private DatabaseHelper databaseHelper;

    public ChatMessageDao(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public long insert(ChatMessage chatMessage) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ChatMessageEntry.COL_USER_ID, chatMessage.getUserId());
        values.put(ChatMessageEntry.COL_ROLE, chatMessage.getRole());
        values.put(ChatMessageEntry.COL_CONTENT, chatMessage.getContent());
        values.put(ChatMessageEntry.COL_CREATED_AT, chatMessage.getCreatedAt() != 0 ? chatMessage.getCreatedAt() : DateTimeUtils.getCurrentTimestamp());
        values.put(ChatMessageEntry.COL_PROVIDER, chatMessage.getProvider());
        return db.insert(ChatMessageEntry.TABLE_NAME, null, values);
    }

    public List<ChatMessage> findByUserId(long userId) {
        List<ChatMessage> messages = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(ChatMessageEntry.TABLE_NAME, null,
                    ChatMessageEntry.COL_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}, null, null,
                    ChatMessageEntry.COL_CREATED_AT + " ASC");
            while (cursor.moveToNext()) {
                messages.add(cursorToChatMessage(cursor));
            }
            return messages;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public List<ChatMessage> findRecentByUserId(long userId, int limit) {
        List<ChatMessage> messages = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(ChatMessageEntry.TABLE_NAME, null,
                    ChatMessageEntry.COL_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}, null, null,
                    ChatMessageEntry.COL_CREATED_AT + " DESC", String.valueOf(limit));
            while (cursor.moveToNext()) {
                messages.add(cursorToChatMessage(cursor));
            }
            // Reverse to maintain chronological order
            java.util.Collections.reverse(messages);
            return messages;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public int deleteByUserId(long userId) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        return db.delete(ChatMessageEntry.TABLE_NAME,
                ChatMessageEntry.COL_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
    }

    private ChatMessage cursorToChatMessage(Cursor cursor) {
        ChatMessage message = new ChatMessage();
        message.setId(cursor.getLong(cursor.getColumnIndexOrThrow(ChatMessageEntry.COL_ID)));
        message.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(ChatMessageEntry.COL_USER_ID)));
        message.setRole(cursor.getString(cursor.getColumnIndexOrThrow(ChatMessageEntry.COL_ROLE)));
        message.setContent(cursor.getString(cursor.getColumnIndexOrThrow(ChatMessageEntry.COL_CONTENT)));
        message.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(ChatMessageEntry.COL_CREATED_AT)));
        message.setProvider(cursor.getString(cursor.getColumnIndexOrThrow(ChatMessageEntry.COL_PROVIDER)));
        return message;
    }
}