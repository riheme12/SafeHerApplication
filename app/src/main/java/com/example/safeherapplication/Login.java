package com.example.safeherapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {


    private EditText emailEdit, passwordEdit;
    private ImageButton showPasswordBtn;
    private Button loginBtn;
    private TextView signupLink;
    private boolean showPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        emailEdit = findViewById(R.id.emailEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        showPasswordBtn = findViewById(R.id.showPasswordBtn);
        loginBtn = findViewById(R.id.loginBtn);
        signupLink = findViewById(R.id.signupLink);

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

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEdit.getText().toString().trim();
                String password = passwordEdit.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Login.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Replace with real login logic (API call, etc)
                if (email.equals("test@example.com") && password.equals("123456")) {
                    Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    // start home activity
                    startActivity(new Intent(Login.this, MainActivity.class));
                    finish();
                } else {
                    new AlertDialog.Builder(Login.this)
                            .setTitle("Login Failed")
                            .setMessage("Invalid credentials. Please try again.")
                            .setPositiveButton("OK", null)
                            .show();
                }
            }
        });
    }
}