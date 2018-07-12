package com.epgeotrack.app.ep_geo_tracking;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!isRunning(context)) {
            Intent background = new Intent(context, GeofenceInitializationService.class);
            context.startService(background);
        } else {
            Log.d("class:AlarmReceiver", "GeofenceInitializationService: is already running...");
        }
    }

    private boolean isRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : Objects.requireNonNull(manager).getRunningServices(Integer.MAX_VALUE)) {
            if (GeofenceInitializationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
