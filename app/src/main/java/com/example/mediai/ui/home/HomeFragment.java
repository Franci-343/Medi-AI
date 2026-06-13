package com.example.mediai.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediai.R;
import com.example.mediai.data.local.DatabaseHelper;
import com.example.mediai.data.local.dao.DoseLogDao;
import com.example.mediai.data.local.dao.MedicationDao;
import com.example.mediai.data.model.Medication;
import com.example.mediai.data.repository.MedicationRepository;
import com.example.mediai.notification.ReminderScheduler;
import com.example.mediai.ui.medication.AddMedicationActivity;
import com.example.mediai.util.DateTimeUtils;
import com.example.mediai.util.SessionManager;

import java.util.List;

public class HomeFragment extends Fragment {

    private TextView tvGreeting, tvSummary;
    private RecyclerView rvMedications;
    private MedicationAdapter adapter;
    private MedicationRepository medicationRepository;
    private DoseLogDao doseLogDao;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sessionManager = new SessionManager(requireContext());
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(requireContext());
        MedicationDao medicationDao = new MedicationDao(dbHelper);
        doseLogDao = new DoseLogDao(dbHelper);
        medicationRepository = new MedicationRepository(medicationDao);

        initViews(view);
        setupListeners(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void initViews(View view) {
        tvGreeting = view.findViewById(R.id.tv_greeting);
        tvSummary = view.findViewById(R.id.tv_summary);
        rvMedications = view.findViewById(R.id.rv_medications);
        rvMedications.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupListeners(View view) {
        view.findViewById(R.id.btn_add_medication).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddMedicationActivity.class));
        });
    }

    private void loadData() {
        long userId = sessionManager.getUserId();
        String userName = sessionManager.getUserName();

        tvGreeting.setText(DateTimeUtils.getGreeting(userName));

        List<Medication> medications = medicationRepository.getActiveByUserId(userId);
        int count = medications.size();

        int takenToday = 0;
        long startOfDay = DateTimeUtils.getStartOfDayTimestamp();
        long now = DateTimeUtils.getCurrentTimestamp();
        for (Medication med : medications) {
            long scheduledTime = DateTimeUtils.getTimestampForTime(med.getTime());
            if (scheduledTime <= now && scheduledTime >= startOfDay) {
                // Check if already marked as taken
                if (doseLogDao.findByMedicationAndSchedule(med.getId(), scheduledTime) != null) {
                    takenToday++;
                }
            }
        }

        tvSummary.setText(getString(R.string.doses_taken_format, takenToday, count));

        if (adapter == null) {
            adapter = new MedicationAdapter(medications,
                    medication -> markAsTaken(medication),
                    medication -> editMedication(medication));
            rvMedications.setAdapter(adapter);
        } else {
            adapter.updateList(medications);
        }
    }

    private void markAsTaken(Medication medication) {
        long userId = sessionManager.getUserId();
        long scheduledTime = DateTimeUtils.getTimestampForTime(medication.getTime());

        com.example.mediai.data.model.DoseLog doseLog = new com.example.mediai.data.model.DoseLog();
        doseLog.setMedicationId(medication.getId());
        doseLog.setUserId(userId);
        doseLog.setScheduledAt(scheduledTime);
        doseLog.setStatus("TAKEN");
        doseLog.setRegisteredAt(DateTimeUtils.getCurrentTimestamp());

        doseLogDao.insert(doseLog);
        Toast.makeText(getContext(), R.string.status_taken, Toast.LENGTH_SHORT).show();
        loadData();
    }

    private void editMedication(Medication medication) {
        Intent intent = new Intent(getActivity(), AddMedicationActivity.class);
        intent.putExtra("medication_id", medication.getId());
        startActivity(intent);
    }
}