package com.example.mediai.ui.medication;

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

public class AddMedicationActivity extends AppCompatActivity {

    private EditText etName, etDose, etTime, etNotes;
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
        rgFrequency = findViewById(R.id.rg_frequency);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        btnDelete = findViewById(R.id.btn_delete);
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveMedication());
        btnCancel.setOnClickListener(v -> finish());
        btnDelete.setOnClickListener(v -> deleteMedication());
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

        if (!ValidationUtils.isNotEmpty(name)) {
            Toast.makeText(this, R.string.empty_field_error, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!ValidationUtils.isNotEmpty(dose)) {
            Toast.makeText(this, R.string.empty_field_error, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!ValidationUtils.isValidTime(time)) {
            Toast.makeText(this, R.string.empty_field_error, Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedId = rgFrequency.getCheckedRadioButtonId();
        RadioButton selectedRb = findViewById(selectedId);
        String frequency = selectedRb != null ? selectedRb.getText().toString() : getString(R.string.frequency_once_daily);

        Medication medication;
        if (editingMedicationId > 0) {
            medication = medicationRepository.getById(editingMedicationId);
            if (medication != null) {
                medication.setName(name);
                medication.setDose(dose);
                medication.setTime(time);
                medication.setFrequency(frequency);
                medication.setNotes(notes);
            }
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