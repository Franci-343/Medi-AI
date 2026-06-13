package com.example.mediai.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mediai.R;
import com.example.mediai.data.local.DatabaseHelper;
import com.example.mediai.data.local.dao.UserDao;
import com.example.mediai.data.repository.AuthRepository;
import com.example.mediai.util.ValidationUtils;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvBackToLogin;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        UserDao userDao = new UserDao(dbHelper);
        authRepository = new AuthRepository(userDao);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);
        tvBackToLogin = findViewById(R.id.tv_back_to_login);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });

        tvBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void attemptRegister() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!ValidationUtils.isValidName(name)) {
            Toast.makeText(this, R.string.register_error_name, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            Toast.makeText(this, R.string.register_error_email, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            Toast.makeText(this, R.string.register_error_password, Toast.LENGTH_SHORT).show();
            return;
        }

        if (authRepository.isEmailTaken(email)) {
            Toast.makeText(this, R.string.register_error_email_exists, Toast.LENGTH_SHORT).show();
            return;
        }

        long result = authRepository.register(name, email, password);
        if (result > 0) {
            Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }
}