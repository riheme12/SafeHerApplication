package com.example.safeherapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FakeCallActivity extends AppCompatActivity {

    private EditText callerNameInput;
    private EditText delayEditText;

    private TextView delayValueText;
    private Button startCallBtn;
    private View incomingCallLayout;
    private TextView incomingCallerName;
    private Button acceptBtn;
    private Button declineBtn;
    private ImageView callerIcon;

    private MediaPlayer ringtonePlayer;
    private Vibrator vibrator;
    private Handler handler;
    private int delaySeconds = 5; // valeur par défaut

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_call);

        // Initialize views
        callerNameInput = findViewById(R.id.callerNameInput);
        delayEditText = findViewById(R.id.delayEditText);
        delayValueText = findViewById(R.id.delayValueText);

        startCallBtn = findViewById(R.id.startCallBtn);

        incomingCallLayout = findViewById(R.id.incomingCallLayout);
        incomingCallerName = findViewById(R.id.incomingCallerName);

        acceptBtn = findViewById(R.id.acceptBtn);
        declineBtn = findViewById(R.id.declineBtn);
        callerIcon = findViewById(R.id.callerIcon);

        handler = new Handler();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // Default delay display
        delayValueText.setText("5 seconds");

        // Start fake call
        startCallBtn.setOnClickListener(v -> scheduleFakeCall());

        // Accept call
        acceptBtn.setOnClickListener(v -> {
            stopRingtone();
            showActiveCallScreen();
        });

        // Decline call
        declineBtn.setOnClickListener(v -> {
            stopRingtone();
            endCall();
        });
        Intent intent = getIntent();
        String callerName = intent.getStringExtra("callerName");
        if (callerName != null) {
            triggerFakeCall(callerName);
        }

    }

    private void scheduleFakeCall() {

        // Caller name
        String callerName = callerNameInput.getText().toString().trim();
        if (callerName.isEmpty()) callerName = "Mom";

        // Delay text
        String delayText = delayEditText.getText().toString().trim();
        if (delayText.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer le délai en secondes", Toast.LENGTH_SHORT).show();
            return;
        }

        delaySeconds = Integer.parseInt(delayText);
        delayValueText.setText(delaySeconds + " seconds");

        final String finalCallerName = callerName;

        Toast.makeText(this, "Appel programmé dans " + delaySeconds + " secondes",
                Toast.LENGTH_SHORT).show();

        startCallBtn.setEnabled(false);
        startCallBtn.setText("En attente...");

        Intent serviceIntent = new Intent(this, FakeCallService.class);
        serviceIntent.putExtra("callerName", finalCallerName);
        serviceIntent.putExtra("delay", delaySeconds);
        startService(serviceIntent);
    }

    private void triggerFakeCall(String callerName) {

        incomingCallerName.setText(callerName);
        incomingCallLayout.setVisibility(View.VISIBLE);

        // Show screen over lock screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        playRingtone();

        // Vibrate
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            long[] pattern = {0, 500, 1000};
            vibrator.vibrate(android.os.VibrationEffect.createWaveform(pattern, 0));
        } else {
            long[] pattern = {0, 500, 1000};
            vibrator.vibrate(pattern, 0);
        }
    }
    private void playRingtone() {
        try {
            stopRingtone();

            // Charger le fichier musique.mp3 depuis res/raw
            ringtonePlayer = MediaPlayer.create(this, R.raw.musique);
            ringtonePlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); // ou STREAM_RING si tu veux
            ringtonePlayer.setLooping(true);
            ringtonePlayer.start();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Impossible de lire la sonnerie", Toast.LENGTH_SHORT).show();
        }
    }



    private void stopRingtone() {
        if (ringtonePlayer != null) {
            ringtonePlayer.stop();
            ringtonePlayer.release();
            ringtonePlayer = null;
        }
        vibrator.cancel();
    }

    private void showActiveCallScreen() {
        incomingCallLayout.setVisibility(View.GONE);

        Toast.makeText(this, "Appel en cours avec " + incomingCallerName.getText(),
                Toast.LENGTH_LONG).show();

        // Auto end call after 30 seconds
        handler.postDelayed(this::endCall, 30000);
    }

    private void endCall() {
        incomingCallLayout.setVisibility(View.GONE);
        startCallBtn.setEnabled(true);
        startCallBtn.setText("Schedule Fake Call");

        Toast.makeText(this, "Appel terminé", Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRingtone();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
