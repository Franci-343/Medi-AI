package com.example.mediai.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mediai.data.local.DatabaseContract.DoseLogEntry;
import com.example.mediai.data.local.DatabaseContract.MedicationEntry;
import com.example.mediai.data.local.DatabaseContract.UserEntry;
import com.example.mediai.data.local.DatabaseContract.ChatMessageEntry;
import com.example.mediai.util.HashUtils;
import com.example.mediai.util.DateTimeUtils;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserEntry.CREATE_TABLE);
        db.execSQL(MedicationEntry.CREATE_TABLE);
        db.execSQL(DoseLogEntry.CREATE_TABLE);
        db.execSQL(ChatMessageEntry.CREATE_TABLE);

        // Insert demo data
        insertDemoData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ChatMessageEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DoseLogEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MedicationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME);
        onCreate(db);
    }

    private void insertDemoData(SQLiteDatabase db) {
        long now = DateTimeUtils.getCurrentTimestamp();

        // Demo user: demo@mediai.com / 123456
        String passwordHash = HashUtils.sha256("123456");
        db.execSQL("INSERT INTO " + UserEntry.TABLE_NAME +
                        "(" + UserEntry.COL_FULL_NAME + ", " + UserEntry.COL_EMAIL + ", " +
                        UserEntry.COL_PASSWORD_HASH + ", " + UserEntry.COL_CREATED_AT + ") VALUES(?, ?, ?, ?)",
                new Object[]{"Juan Pérez", "demo@mediai.com", passwordHash, now});

        long demoUserId = 1;

        // Demo medications
        db.execSQL("INSERT INTO " + MedicationEntry.TABLE_NAME +
                        "(" + MedicationEntry.COL_USER_ID + ", " + MedicationEntry.COL_NAME + ", " +
                        MedicationEntry.COL_DOSE + ", " + MedicationEntry.COL_TIME + ", " +
                        MedicationEntry.COL_FREQUENCY + ", " + MedicationEntry.COL_NOTES + ", " +
                        MedicationEntry.COL_ACTIVE + ", " + MedicationEntry.COL_CREATED_AT + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{demoUserId, "Amoxicilina 500mg", "1 cápsula", "08:00", "Cada 8 horas", "Después del desayuno", 1, now});

        db.execSQL("INSERT INTO " + MedicationEntry.TABLE_NAME +
                        "(" + MedicationEntry.COL_USER_ID + ", " + MedicationEntry.COL_NAME + ", " +
                        MedicationEntry.COL_DOSE + ", " + MedicationEntry.COL_TIME + ", " +
                        MedicationEntry.COL_FREQUENCY + ", " + MedicationEntry.COL_NOTES + ", " +
                        MedicationEntry.COL_ACTIVE + ", " + MedicationEntry.COL_CREATED_AT + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{demoUserId, "Ibuprofeno 400mg", "1 tableta", "14:00", "Cada 12 horas", "Con comida", 1, now});

        db.execSQL("INSERT INTO " + MedicationEntry.TABLE_NAME +
                        "(" + MedicationEntry.COL_USER_ID + ", " + MedicationEntry.COL_NAME + ", " +
                        MedicationEntry.COL_DOSE + ", " + MedicationEntry.COL_TIME + ", " +
                        MedicationEntry.COL_FREQUENCY + ", " + MedicationEntry.COL_NOTES + ", " +
                        MedicationEntry.COL_ACTIVE + ", " + MedicationEntry.COL_CREATED_AT + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{demoUserId, "Vitamina C", "1 tableta", "21:00", "Una vez al día", "Antes de dormir", 1, now});
    }
}