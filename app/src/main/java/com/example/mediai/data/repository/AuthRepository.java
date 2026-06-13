package com.example.mediai.data.repository;

import com.example.mediai.data.local.dao.UserDao;
import com.example.mediai.data.model.User;
import com.example.mediai.util.DateTimeUtils;
import com.example.mediai.util.HashUtils;

public class AuthRepository {
    private UserDao userDao;

    public AuthRepository(UserDao userDao) {
        this.userDao = userDao;
    }

    public long register(String fullName, String email, String password) {
        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPasswordHash(HashUtils.sha256(password));
        user.setCreatedAt(DateTimeUtils.getCurrentTimestamp());
        return userDao.insert(user);
    }

    public User login(String email, String password) {
        return userDao.authenticate(email.trim().toLowerCase(), password);
    }

    public boolean isEmailTaken(String email) {
        return userDao.emailExists(email.trim().toLowerCase());
    }

    public User getUserById(long id) {
        return userDao.findById(id);
    }
}