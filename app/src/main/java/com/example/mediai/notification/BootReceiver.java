package com.example.mediai.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mediai.data.local.DatabaseHelper;
import com.example.mediai.data.local.dao.MedicationDao;
import com.example.mediai.data.model.Medication;
import com.example.mediai.util.SessionManager;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Re-schedule all alarms for the logged-in user
            SessionManager sessionManager = new SessionManager(context);
            if (sessionManager.isLoggedIn()) {
                long userId = sessionManager.getUserId();
                DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
                MedicationDao medicationDao = new MedicationDao(dbHelper);
                List<Medication> medications = medicationDao.findByUserId(userId);

                for (Medication med : medications) {
                    ReminderScheduler.scheduleReminder(
                            context,
                            med.getId(),
                            med.getName(),
                            med.getDose(),
                            med.getTime()
                    );
                }
            }
        }
    }
}