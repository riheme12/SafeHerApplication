package com.example.safeherapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ShakeDetector shakeDetector;
    private Button sosBtn;
    private TextView greetingView;
    private LinearLayout virtualEscortRow;
    private LinearLayout fakeCallRow;
    private LinearLayout safeMapRow;
    private LinearLayout emergencyContactRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sosBtn = findViewById(R.id.sosBtn);
        greetingView = findViewById(R.id.greetingView);
        virtualEscortRow = findViewById(R.id.virtualEscortRow);
        fakeCallRow = findViewById(R.id.fakeCallRow);
        safeMapRow = findViewById(R.id.safeMapRow);
        emergencyContactRow = findViewById(R.id.emergencyContactRow);

        setGreeting();

        sosBtn.setOnClickListener(v -> showSOSDialog());

        virtualEscortRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Virtual Escort Clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, VirtualEscortActivity.class));
            }
        });

        fakeCallRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Fake Call Clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, FakeCallActivity.class));
            }
        });

        safeMapRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "SafeMap Clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, SafeMapActivity.class));
            }
        });

        emergencyContactRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Emergency contact clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, EmergencycontactActivity.class));
            }
        });

        shakeDetector = new ShakeDetector(this, new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showShakeDialog();
                    }
                });
            }
        });
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    return true;

                case R.id.nav_profile:
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shakeDetector != null) shakeDetector.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (shakeDetector != null) shakeDetector.stop();
    }

    private void setGreeting() {
        int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour < 12) {
            greeting = "Good Morning";
        } else if (hour < 18) {
            greeting = "Good Afternoon";
        } else {
            greeting = "Good Evening";
        }
        greetingView.setText(greeting + ", User");
    }

    private void showSOSDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Send Emergency Alert")
                .setMessage("This will send an SMS to all your emergency contacts with your location. Continue?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Send Alert", (dialog, which) -> sendEmergencyAlert())
                .show();
    }

    private void showShakeDialog() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(android.os.VibrationEffect.createOneShot(100, android.os.VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);
        }
        new AlertDialog.Builder(this)
                .setTitle("Shake Detected")
                .setMessage("Send emergency alert to your contacts?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Send Alert", (dialog, which) -> sendEmergencyAlert())
                .show();
    }

    private void sendEmergencyAlert() {
        Toast.makeText(this, "Emergency alert has been sent to your contacts.", Toast.LENGTH_LONG).show();
        // Implement notification/sms logic if needed
    }
}
