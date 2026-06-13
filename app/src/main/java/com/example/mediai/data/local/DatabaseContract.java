package com.example.mediai.data.local;

public class DatabaseContract {

    public static final String DATABASE_NAME = "medi_ai.db";
    public static final int DATABASE_VERSION = 1;

    public static class UserEntry {
        public static final String TABLE_NAME = "users";
        public static final String COL_ID = "id";
        public static final String COL_FULL_NAME = "full_name";
        public static final String COL_EMAIL = "email";
        public static final String COL_PASSWORD_HASH = "password_hash";
        public static final String COL_CREATED_AT = "created_at";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FULL_NAME + " TEXT NOT NULL, " +
                COL_EMAIL + " TEXT NOT NULL UNIQUE, " +
                COL_PASSWORD_HASH + " TEXT NOT NULL, " +
                COL_CREATED_AT + " INTEGER NOT NULL)";
    }

    public static class MedicationEntry {
        public static final String TABLE_NAME = "medications";
        public static final String COL_ID = "id";
        public static final String COL_USER_ID = "user_id";
        public static final String COL_NAME = "name";
        public static final String COL_DOSE = "dose";
        public static final String COL_TIME = "time";
        public static final String COL_FREQUENCY = "frequency";
        public static final String COL_NOTES = "notes";
        public static final String COL_ACTIVE = "active";
        public static final String COL_CREATED_AT = "created_at";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_ID + " INTEGER NOT NULL, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_DOSE + " TEXT NOT NULL, " +
                COL_TIME + " TEXT NOT NULL, " +
                COL_FREQUENCY + " TEXT NOT NULL, " +
                COL_NOTES + " TEXT, " +
                COL_ACTIVE + " INTEGER NOT NULL DEFAULT 1, " +
                COL_CREATED_AT + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + COL_USER_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COL_ID + "))";
    }

    public static class DoseLogEntry {
        public static final String TABLE_NAME = "dose_logs";
        public static final String COL_ID = "id";
        public static final String COL_MEDICATION_ID = "medication_id";
        public static final String COL_USER_ID = "user_id";
        public static final String COL_SCHEDULED_AT = "scheduled_at";
        public static final String COL_STATUS = "status";
        public static final String COL_REGISTERED_AT = "registered_at";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MEDICATION_ID + " INTEGER NOT NULL, " +
                COL_USER_ID + " INTEGER NOT NULL, " +
                COL_SCHEDULED_AT + " INTEGER NOT NULL, " +
                COL_STATUS + " TEXT NOT NULL, " +
                COL_REGISTERED_AT + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + COL_MEDICATION_ID + ") REFERENCES " + MedicationEntry.TABLE_NAME + "(" + MedicationEntry.COL_ID + "), " +
                "FOREIGN KEY(" + COL_USER_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COL_ID + "))";
    }

    public static class ChatMessageEntry {
        public static final String TABLE_NAME = "chat_messages";
        public static final String COL_ID = "id";
        public static final String COL_USER_ID = "user_id";
        public static final String COL_ROLE = "role";
        public static final String COL_CONTENT = "content";
        public static final String COL_CREATED_AT = "created_at";
        public static final String COL_PROVIDER = "provider";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_ID + " INTEGER NOT NULL, " +
                COL_ROLE + " TEXT NOT NULL, " +
                COL_CONTENT + " TEXT NOT NULL, " +
                COL_CREATED_AT + " INTEGER NOT NULL, " +
                COL_PROVIDER + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COL_USER_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COL_ID + "))";
    }
}