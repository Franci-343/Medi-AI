package com.example.mediai.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mediai.R;
import com.example.mediai.data.model.Medication;
import com.example.mediai.util.DateTimeUtils;

import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.ViewHolder> {

    private List<Medication> medications;
    private OnTakeListener onTakeListener;
    private OnItemClickListener onItemClickListener;

    public interface OnTakeListener {
        void onTake(Medication medication);
    }

    public interface OnItemClickListener {
        void onItemClick(Medication medication);
    }

    public MedicationAdapter(List<Medication> medications, OnTakeListener onTakeListener, OnItemClickListener onItemClickListener) {
        this.medications = medications;
        this.onTakeListener = onTakeListener;
        this.onItemClickListener = onItemClickListener;
    }

    public void updateList(List<Medication> newList) {
        this.medications = newList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medication, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Medication med = medications.get(position);
        holder.tvTime.setText(med.getTime());
        holder.tvName.setText(med.getName());
        holder.tvDose.setText(med.getDose());
        holder.tvNotes.setText(med.getNotes() != null ? med.getNotes() : "");
        holder.tvStatus.setText(R.string.status_pending);
        holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.accent));
        holder.btnTake.setVisibility(View.VISIBLE);

        holder.btnTake.setOnClickListener(v -> {
            if (onTakeListener != null) {
                onTakeListener.onTake(med);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(med);
            }
        });
    }

    @Override
    public int getItemCount() {
        return medications != null ? medications.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvName, tvDose, tvNotes, tvStatus;
        Button btnTake;

        ViewHolder(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_medication_time);
            tvName = itemView.findViewById(R.id.tv_medication_name);
            tvDose = itemView.findViewById(R.id.tv_medication_dose);
            tvNotes = itemView.findViewById(R.id.tv_medication_notes);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnTake = itemView.findViewById(R.id.btn_take);
        }
    }
}