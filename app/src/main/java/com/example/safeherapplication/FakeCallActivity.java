package com.example.safeherapplication;

import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class FakeCallActivity extends AppCompatActivity {

    private EditText callerNameInput;
    private SeekBar delaySeekBar;
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
    private int delaySeconds = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_call);

        // Initialize views
        callerNameInput = findViewById(R.id.callerNameInput);
        delaySeekBar = findViewById(R.id.delaySeekBar);
        delayValueText = findViewById(R.id.delayValueText);
        startCallBtn = findViewById(R.id.startCallBtn);
        incomingCallLayout = findViewById(R.id.incomingCallLayout);
        incomingCallerName = findViewById(R.id.incomingCallerName);
        acceptBtn = findViewById(R.id.acceptBtn);
        declineBtn = findViewById(R.id.declineBtn);
        callerIcon = findViewById(R.id.callerIcon);

        handler = new Handler();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // Setup delay seekbar
        delaySeekBar.setMax(60);
        delaySeekBar.setProgress(5);
        delayValueText.setText(delaySeconds + " secondes");

        delaySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                delaySeconds = Math.max(1, progress);
                delayValueText.setText(delaySeconds + " secondes");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Start fake call button
        startCallBtn.setOnClickListener(v -> scheduleFakeCall());

        // Accept button
        acceptBtn.setOnClickListener(v -> {
            stopRingtone();
            showActiveCallScreen();
        });

        // Decline button
        declineBtn.setOnClickListener(v -> {
            stopRingtone();
            endCall();
        });
    }

    private void scheduleFakeCall() {
        String callerName = callerNameInput.getText().toString().trim();
        if (callerName.isEmpty()) {
            callerName = "Maman";
        }

        final String finalCallerName = callerName;

        Toast.makeText(this, "Appel programmé dans " + delaySeconds + " secondes",
                Toast.LENGTH_SHORT).show();

        // Hide setup, show waiting message
        startCallBtn.setEnabled(false);
        startCallBtn.setText("En attente...");

        handler.postDelayed(() -> triggerFakeCall(finalCallerName), delaySeconds * 1000L);
    }

    private void triggerFakeCall(String callerName) {
        // Show incoming call screen
        incomingCallerName.setText(callerName);
        incomingCallLayout.setVisibility(View.VISIBLE);

        // Make screen show over lock screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        // Play ringtone
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
            Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            ringtonePlayer = MediaPlayer.create(this, ringtoneUri);
            ringtonePlayer.setAudioStreamType(AudioManager.STREAM_RING);
            ringtonePlayer.setLooping(true);
            ringtonePlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
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
        startCallBtn.setText("Démarrer l'appel simulé");
        Toast.makeText(this, "Appel terminé", Toast.LENGTH_SHORT).show();
        finish();
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