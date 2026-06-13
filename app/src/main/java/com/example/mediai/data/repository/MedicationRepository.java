package com.example.mediai.data.repository;

import com.example.mediai.data.local.dao.MedicationDao;
import com.example.mediai.data.model.Medication;
import com.example.mediai.util.DateTimeUtils;

import java.util.List;

public class MedicationRepository {
    private MedicationDao medicationDao;

    public MedicationRepository(MedicationDao medicationDao) {
        this.medicationDao = medicationDao;
    }

    public long save(Medication medication) {
        if (medication.getId() > 0) {
            medicationDao.update(medication);
            return medication.getId();
        } else {
            medication.setCreatedAt(DateTimeUtils.getCurrentTimestamp());
            medication.setActive(true);
            return medicationDao.insert(medication);
        }
    }

    public boolean delete(long medicationId) {
        return medicationDao.delete(medicationId) > 0;
    }

    public Medication getById(long medicationId) {
        return medicationDao.findById(medicationId);
    }

    public List<Medication> getActiveByUserId(long userId) {
        return medicationDao.findByUserId(userId);
    }

    public List<Medication> getAllByUserId(long userId) {
        return medicationDao.getAllByUserId(userId);
    }

    public int getActiveCount(long userId) {
        return medicationDao.getActiveCountByUserId(userId);
    }
}