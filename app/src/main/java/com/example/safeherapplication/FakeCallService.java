package com.example.safeherapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class FakeCallService extends Service {

    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String callerName = intent.getStringExtra("callerName");
        int delay = intent.getIntExtra("delay", 5);

        Toast.makeText(this, "Appel programmé dans " + delay + " secondes", Toast.LENGTH_SHORT).show();

        handler.postDelayed(() -> {
            // Lancer l’activité FakeCallActivity pour afficher l’appel
            Intent callIntent = new Intent(this, FakeCallActivity.class);
            callIntent.putExtra("callerName", callerName);
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(callIntent);

            stopSelf();
        }, delay * 1000L);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
