package com.example.mediai.ui.medication;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mediai.R;
import com.example.mediai.data.local.DatabaseHelper;
import com.example.mediai.data.local.dao.MedicationDao;
import com.example.mediai.data.model.Medication;
import com.example.mediai.data.repository.MedicationRepository;
import com.example.mediai.notification.ReminderScheduler;
import com.example.mediai.util.SessionManager;
import com.example.mediai.util.ValidationUtils;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

public class AddMedicationActivity extends AppCompatActivity {

    private EditText etName, etDose, etTime, etNotes;
    private TextInputLayout tilName, tilDose, tilTime;
    private RadioGroup rgFrequency;
    private Button btnSave, btnCancel, btnDelete;
    private MedicationRepository medicationRepository;
    private SessionManager sessionManager;
    private long editingMedicationId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);

        sessionManager = new SessionManager(this);
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        MedicationDao medicationDao = new MedicationDao(dbHelper);
        medicationRepository = new MedicationRepository(medicationDao);

        initViews();
        setupListeners();

        // Check if editing
        editingMedicationId = getIntent().getLongExtra("medication_id", -1);
        if (editingMedicationId > 0) {
            loadMedication(editingMedicationId);
        }
    }

    private void initViews() {
        etName = findViewById(R.id.et_name);
        etDose = findViewById(R.id.et_dose);
        etTime = findViewById(R.id.et_time);
        etNotes = findViewById(R.id.et_notes);
        tilName = findViewById(R.id.til_name);
        tilDose = findViewById(R.id.til_dose);
        tilTime = findViewById(R.id.til_time);
        rgFrequency = findViewById(R.id.rg_frequency);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        btnDelete = findViewById(R.id.btn_delete);

        // Make time field read-only to force using the picker
        etTime.setFocusable(false);
        etTime.setClickable(true);
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveMedication());
        btnCancel.setOnClickListener(v -> finish());
        btnDelete.setOnClickListener(v -> deleteMedication());
        etTime.setOnClickListener(v -> showTimePickerDialog());
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // If editing, start with current medication time
        String currentTime = etTime.getText().toString();
        if (ValidationUtils.isValidTime(currentTime)) {
            String[] parts = currentTime.split(":");
            hour = Integer.parseInt(parts[0]);
            minute = Integer.parseInt(parts[1]);
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, selectedMinute) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, selectedMinute);
                    etTime.setText(time);
                    tilTime.setError(null);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void loadMedication(long id) {
        Medication medication = medicationRepository.getById(id);
        if (medication != null) {
            setTitle(R.string.edit_medication_title);
            etName.setText(medication.getName());
            etDose.setText(medication.getDose());
            etTime.setText(medication.getTime());
            etNotes.setText(medication.getNotes());

            // Set frequency radio button
            String freq = medication.getFrequency();
            if (freq.equals(getString(R.string.frequency_every_8h))) {
                rgFrequency.check(R.id.rb_every_8h);
            } else if (freq.equals(getString(R.string.frequency_every_12h))) {
                rgFrequency.check(R.id.rb_every_12h);
            } else if (freq.equals(getString(R.string.frequency_once_daily))) {
                rgFrequency.check(R.id.rb_once_daily);
            } else {
                rgFrequency.check(R.id.rb_custom);
            }

            btnDelete.setVisibility(View.VISIBLE);
        }
    }

    private void saveMedication() {
        String name = etName.getText().toString().trim();
        String dose = etDose.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        boolean isValid = true;

        tilName.setError(null);
        tilDose.setError(null);
        tilTime.setError(null);

        if (!ValidationUtils.isNotEmpty(name)) {
            tilName.setError(getString(R.string.empty_field_error));
            isValid = false;
        }
        if (!ValidationUtils.isNotEmpty(dose)) {
            tilDose.setError(getString(R.string.empty_field_error));
            isValid = false;
        }
        if (!ValidationUtils.isNotEmpty(time)) {
            tilTime.setError(getString(R.string.empty_field_error));
            isValid = false;
        } else if (!ValidationUtils.isValidTime(time)) {
            tilTime.setError(getString(R.string.invalid_time_error));
            isValid = false;
        }

        if (!isValid) return;

        int selectedId = rgFrequency.getCheckedRadioButtonId();
        RadioButton selectedRb = findViewById(selectedId);
        String frequency = selectedRb != null ? selectedRb.getText().toString() : getString(R.string.frequency_once_daily);

        Medication medication;
        if (editingMedicationId > 0) {
            medication = medicationRepository.getById(editingMedicationId);
            if (medication == null) {
                Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
                return;
            }
            medication.setName(name);
            medication.setDose(dose);
            medication.setTime(time);
            medication.setFrequency(frequency);
            medication.setNotes(notes);
        } else {
            medication = new Medication();
            medication.setUserId(sessionManager.getUserId());
            medication.setName(name);
            medication.setDose(dose);
            medication.setTime(time);
            medication.setFrequency(frequency);
            medication.setNotes(notes);
            medication.setActive(true);
        }

        long result = medicationRepository.save(medication);
        if (result > 0) {
            // Schedule notification
            if (medication.getId() > 0) {
                ReminderScheduler.scheduleReminder(this, medication.getId(), name, dose, time);
            }
            Toast.makeText(this, R.string.medication_saved, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteMedication() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.delete_confirm)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    if (editingMedicationId > 0) {
                        ReminderScheduler.cancelReminder(this, editingMedicationId);
                        medicationRepository.delete(editingMedicationId);
                        Toast.makeText(this, R.string.medication_deleted, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }
}