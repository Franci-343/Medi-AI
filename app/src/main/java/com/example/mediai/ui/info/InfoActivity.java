package com.example.mediai.ui.info;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mediai.R;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setTitle(R.string.info_title);
    }
}