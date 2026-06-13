package com.example.mediai.ui.reports;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
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
import com.example.mediai.data.model.DoseLog;
import com.example.mediai.data.repository.ReportRepository;
import com.example.mediai.util.Constants;
import com.example.mediai.util.DateTimeUtils;
import com.example.mediai.util.SessionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ReportsFragment extends Fragment {

    private TextView tvAdherence, tvTaken, tvMissed;
    private RecyclerView rvLogs;
    private DoseLogAdapter adapter;
    private ReportRepository reportRepository;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        sessionManager = new SessionManager(requireContext());
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(requireContext());
        DoseLogDao doseLogDao = new DoseLogDao(dbHelper);
        MedicationDao medicationDao = new MedicationDao(dbHelper);
        reportRepository = new ReportRepository(doseLogDao, medicationDao);

        initViews(view);
        setupListeners(view);
        loadData();

        return view;
    }

    private void initViews(View view) {
        tvAdherence = view.findViewById(R.id.tv_adherence);
        tvTaken = view.findViewById(R.id.tv_taken);
        tvMissed = view.findViewById(R.id.tv_missed);
        rvLogs = view.findViewById(R.id.rv_logs);
        rvLogs.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupListeners(View view) {
        view.findViewById(R.id.btn_export_pdf).setOnClickListener(v -> exportPdf());
    }

    private void loadData() {
        long userId = sessionManager.getUserId();

        int taken = reportRepository.getWeeklyTakenCount(userId);
        int missed = reportRepository.getWeeklyMissedCount(userId);
        int adherence = reportRepository.getAdherencePercentage(userId);

        tvAdherence.setText(getString(R.string.adherence_percentage, adherence));
        tvTaken.setText(getString(R.string.doses_taken, taken));
        tvMissed.setText(getString(R.string.doses_missed, missed));

        List<DoseLog> recentLogs = reportRepository.getRecentLogs(userId, 20);
        if (adapter == null) {
            adapter = new DoseLogAdapter(recentLogs);
            rvLogs.setAdapter(adapter);
        } else {
            adapter.updateList(recentLogs);
        }
    }

    private void exportPdf() {
        long userId = sessionManager.getUserId();
        String userName = sessionManager.getUserName();

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        titlePaint.setTextSize(24);
        titlePaint.setColor(Color.parseColor("#0D9488"));
        titlePaint.setFakeBoldText(true);
        canvas.drawText("Medi AI - Reporte de Medicamentos", 20, 40, titlePaint);

        paint.setTextSize(14);
        paint.setColor(Color.BLACK);
        canvas.drawText("Usuario: " + userName, 20, 80, paint);
        canvas.drawText("Fecha: " + DateTimeUtils.formatDate(DateTimeUtils.getCurrentTimestamp()), 20, 105, paint);

        int taken = reportRepository.getWeeklyTakenCount(userId);
        int missed = reportRepository.getWeeklyMissedCount(userId);
        int adherence = reportRepository.getAdherencePercentage(userId);
        int total = taken + missed;

        canvas.drawText("Resumen Semanal:", 20, 145, titlePaint);
        paint.setTextSize(13);
        canvas.drawText("Total dosis: " + total, 20, 175, paint);
        canvas.drawText("Tomadas: " + taken, 20, 195, paint);
        canvas.drawText("Omitidas: " + missed, 20, 215, paint);
        canvas.drawText("Adherencia: " + adherence + "%", 20, 235, paint);

        canvas.drawText("Detalle de dosis:", 20, 275, titlePaint);

        List<DoseLog> logs = reportRepository.getWeeklyLogs(userId);
        int y = 305;
        paint.setTextSize(11);
        for (DoseLog log : logs) {
            if (y > 800) break;
            String logText = DateTimeUtils.formatDateTime(log.getScheduledAt()) +
                    " - " + log.getStatus();
            canvas.drawText(logText, 20, y, paint);
            y += 20;
        }

        pdfDocument.finishPage(page);

        // Save PDF
        File dir = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                Constants.PDF_DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = Constants.PDF_FILE_PREFIX + System.currentTimeMillis() + ".pdf";
        File file = new File(dir, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            pdfDocument.writeTo(fos);
            fos.close();
            Toast.makeText(getContext(), getString(R.string.export_success) + ": " + file.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();
    }
}