package com.example.mediai.ui.reports;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mediai.R;
import com.example.mediai.data.model.DoseLog;
import com.example.mediai.util.DateTimeUtils;

import java.util.List;

public class DoseLogAdapter extends RecyclerView.Adapter<DoseLogAdapter.ViewHolder> {

    private List<DoseLog> logs;

    public DoseLogAdapter(List<DoseLog> logs) {
        this.logs = logs;
    }

    public void updateList(List<DoseLog> newLogs) {
        this.logs = newLogs;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dose_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DoseLog log = logs.get(position);
        holder.tvMedication.setText("Medicamento #" + log.getMedicationId());
        holder.tvTime.setText(DateTimeUtils.formatDateTime(log.getScheduledAt()));

        String status = log.getStatus();
        if ("TAKEN".equals(status)) {
            holder.tvStatus.setText(R.string.status_taken);
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.success));
        } else if ("MISSED".equals(status)) {
            holder.tvStatus.setText(R.string.status_missed);
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.danger));
        } else {
            holder.tvStatus.setText(R.string.status_pending);
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.accent));
        }
    }

    @Override
    public int getItemCount() {
        return logs != null ? logs.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedication, tvTime, tvStatus;

        ViewHolder(View itemView) {
            super(itemView);
            tvMedication = itemView.findViewById(R.id.tv_log_medication);
            tvTime = itemView.findViewById(R.id.tv_log_time);
            tvStatus = itemView.findViewById(R.id.tv_log_status);
        }
    }
}