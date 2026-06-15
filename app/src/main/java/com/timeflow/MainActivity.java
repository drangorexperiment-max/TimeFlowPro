package com.timeflow;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollView sv = new ScrollView(this);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(40, 40, 40, 40);
        sv.addView(ll);
        setContentView(sv);

        final EditText timeInput = new EditText(this);
        timeInput.setHint("Введите минуты (например: 1)");
        timeInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        ll.addView(timeInput);

        Button stop = new Button(this);
        stop.setText("ОСТАНОВИТЬ ВСЁ");
        stop.setOnClickListener(v -> stopService(new Intent(this, SimulationService.class)));
        ll.addView(stop);

        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        for (ApplicationInfo ai : apps) {
            if (pm.getLaunchIntentForPackage(ai.packageName) != null && !ai.packageName.equals(getPackageName())) {
                Button b = new Button(this);
                b.setText(ai.loadLabel(pm).toString());
                b.setOnClickListener(v -> {
                    String val = timeInput.getText().toString();
                    int min = val.isEmpty() ? 0 : Integer.parseInt(val);
                    launch(ai.packageName, min);
                });
                ll.addView(b);
            }
        }
    }

    private void launch(String pkg, int m) {
        try {
            ActivityOptions o = ActivityOptions.makeBasic();
            java.lang.reflect.Method method = ActivityOptions.class.getMethod("setLaunchWindowingMode", int.class);
            method.invoke(o, 5);
            o.setLaunchBounds(new Rect(0,0,1,1));
            Intent i = getPackageManager().getLaunchIntentForPackage(pkg);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i, o.toBundle());
            
            Intent s = new Intent(this, SimulationService.class);
            s.putExtra("pkg", pkg);
            s.putExtra("min", m);
            startForegroundService(s);
            Toast.makeText(this, "Старт " + pkg + " на " + m + " мин.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {}
    }
}
