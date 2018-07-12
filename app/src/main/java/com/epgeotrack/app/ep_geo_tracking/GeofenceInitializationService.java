package com.epgeotrack.app.ep_geo_tracking;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class GeofenceInitializationService extends Service {

    private GeofenceBroadcastReceiver geoFenceReceiver;

    private boolean isRunning;

    private Thread backgroundThread;

    public GeofenceInitializationService() {
    }

    @Override
    public void onCreate() {
        this.isRunning = false;
        this.backgroundThread = new Thread(initTask);
    }

    private Runnable initTask = new Runnable() {
        public void run() {
            Log.d("class:GeofenceInitializationService", "Registering:GeofenceBroadcastReceiver");
            geoFenceReceiver = new GeofenceBroadcastReceiver();
            IntentFilter geoFenceIntentFilter = new IntentFilter("com.epgeotrack.app.ep_geo_tracking.ACTION_RECEIVE_GEOFENCE");
            registerReceiver(geoFenceReceiver, geoFenceIntentFilter);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(geoFenceReceiver);
        this.isRunning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
