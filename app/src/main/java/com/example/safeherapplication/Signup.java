package com.example.safeherapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Signup extends AppCompatActivity {
    private EditText nameEdit, emailEdit, passwordEdit, confirmPasswordEdit;
    private ImageButton showPasswordBtn, showConfirmPasswordBtn;
    private Button signupBtn;
    private TextView loginLink;
    private ImageButton backBtn;
    private boolean showPassword = false;
    private boolean showConfirmPassword = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        nameEdit = findViewById(R.id.nameEdit);
        emailEdit = findViewById(R.id.emailEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        confirmPasswordEdit = findViewById(R.id.confirmPasswordEdit);
        showPasswordBtn = findViewById(R.id.showPasswordBtn);
        showConfirmPasswordBtn = findViewById(R.id.showConfirmPasswordBtn);
        signupBtn = findViewById(R.id.signupBtn);
        loginLink = findViewById(R.id.loginLink);
        backBtn = findViewById(R.id.backBtn);
        showPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPassword = !showPassword;
                if (showPassword) {
                    passwordEdit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    showPasswordBtn.setImageResource(R.drawable.eye_icon);
                } else {
                    passwordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showPasswordBtn.setImageResource(R.drawable.eye_icon);
                }
                passwordEdit.setSelection(passwordEdit.getText().length());
            }
        });

        showConfirmPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmPassword = !showConfirmPassword;
                if (showConfirmPassword) {
                    confirmPasswordEdit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    showConfirmPasswordBtn.setImageResource(R.drawable.eye_icon);
                } else {
                    confirmPasswordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showConfirmPasswordBtn.setImageResource(R.drawable.eye_icon);
                }
                confirmPasswordEdit.setSelection(confirmPasswordEdit.getText().length());
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Navigate back to login
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Signup.this, Login.class));
                finish();

            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEdit.getText().toString().trim();
                String email = emailEdit.getText().toString().trim();
                String password = passwordEdit.getText().toString();
                String confirmPassword = confirmPasswordEdit.getText().toString();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(Signup.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(Signup.this, "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(Signup.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Fake signup logic, replace with your own registration code / API
                new AlertDialog.Builder(Signup.this)
                        .setTitle("Account Created!")
                        .setMessage("Welcome to SafeHer. Your safety is our priority.")
                        .setPositiveButton("Get Started", (dialog, which) -> {
                            startActivity(new Intent(Signup.this, Login.class));
                            finish();
                        })
                        .show();
            }
        });
    }
    }