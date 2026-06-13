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
import com.example.mediai.data.model.User;
import com.example.mediai.data.repository.AuthRepository;
import com.example.mediai.ui.info.InfoActivity;
import com.example.mediai.ui.main.MainActivity;
import com.example.mediai.util.SessionManager;
import com.example.mediai.util.ValidationUtils;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvDocumentation;
    private AuthRepository authRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        // Check if already logged in
        if (sessionManager.isLoggedIn()) {
            startMainActivity();
            return;
        }

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        UserDao userDao = new UserDao(dbHelper);
        authRepository = new AuthRepository(userDao);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        tvDocumentation = findViewById(R.id.tv_documentation);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        tvDocumentation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, InfoActivity.class));
            }
        });
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.login_error_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            Toast.makeText(this, R.string.register_error_email, Toast.LENGTH_SHORT).show();
            return;
        }

        User user = authRepository.login(email, password);
        if (user != null) {
            sessionManager.saveSession(user.getId(), user.getFullName(), user.getEmail());
            Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
            startMainActivity();
        } else {
            Toast.makeText(this, R.string.login_error_invalid, Toast.LENGTH_SHORT).show();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}