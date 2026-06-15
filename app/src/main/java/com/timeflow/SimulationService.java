package com.timeflow;
import android.app.*;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.core.app.NotificationCompat;

public class SimulationService extends Service {
    public int onStartCommand(Intent intent, int flags, int startId) {
        String pkg = intent.getStringExtra("pkg");
        int min = intent.getIntExtra("min", 0);
        
        NotificationChannel c = new NotificationChannel("tf", "TimeFlow", NotificationManager.IMPORTANCE_LOW);
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(c);
        
        startForeground(1, new NotificationCompat.Builder(this, "tf")
            .setContentTitle("Активно: " + pkg)
            .setContentText("Таймер: " + min + " мин.")
            .setSmallIcon(android.R.drawable.ic_media_play).build());

        if (min > 0) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                stopSelf();
            }, min * 60 * 1000);
        }
        return START_STICKY;
    }
    public IBinder onBind(Intent i) { return null; }
}
