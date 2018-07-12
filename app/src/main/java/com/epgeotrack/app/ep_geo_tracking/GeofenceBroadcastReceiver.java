package com.epgeotrack.app.ep_geo_tracking;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = "GeofenceBroadcastReceiver";

    public static final String GEOFENCE_ACTION = "com.epgeotrack.app.ep_geo_tracking.ACTION_RECEIVE_GEOFENCE";

    Context context;

    Intent broadcastIntent = new Intent();

    public GeofenceBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.d(TAG, "Received geo fencing event.");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            handleError(intent);
        } else {
            handleEnterExit(geofencingEvent);
        }
    }

    private void handleError(Intent intent) {
        // Get the error code
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        String errorMessage;

        switch (geofencingEvent.getErrorCode()) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                errorMessage = "Geo fence not available " + geofencingEvent.getErrorCode();
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                errorMessage = "Too many geo fences " + geofencingEvent.getErrorCode();
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                errorMessage = "Too many pending intents " + geofencingEvent.getErrorCode();
            default:
                errorMessage = "Unknown error " + geofencingEvent.getErrorCode();
        }

        // Log the error
        Log.e(TAG, "Geofence handleError:" + errorMessage);

        // Set the action and error message for the broadcast intent
        broadcastIntent
                .setAction(GEOFENCE_ACTION)
                .putExtra("GEOFENCE_STATUS", errorMessage);

        // Broadcast the error *locally* to other components in this app
        LocalBroadcastManager.getInstance(context).sendBroadcast(
                broadcastIntent);
    }

    private void handleEnterExit(GeofencingEvent geofencingEvent) {
        Log.d(TAG, "Handle Geofencing enter and exit.");

        // Get the type of transition (entry or exit)
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that a valid transition was reported
        if ((geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
                || (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            for (Geofence geofence : triggeringGeofences) {
                //payload_data=getNotificationMessageFromPreference(geofence.getRequestId());
                //String strNotificationTitle=getNotificationTitle(payload_data);
                String geofenceTransitionString = getTransitionString(geofenceTransition);
                String geofenceText = geofenceTransitionString + " : " + geofence.getRequestId();
                Log.i(TAG, "Geofence Transition:" + geofenceText);

                sendEventDetailNotificatonIntent(geofenceText);

                // Create an Intent to broadcast to the app
                broadcastIntent
                        .setAction(GEOFENCE_ACTION)
                        .putExtra("EXTRA_GEOFENCE_ID", geofence.getRequestId())
                        .putExtra("EXTRA_GEOFENCE_TRANSITION_TYPE", geofenceTransitionString);
                LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
            }
        } else {
            // Always log as an error
            Log.e(TAG, "Invalid transition type " + geofenceTransition);
        }
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "Entered to Geo fence";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "Exit from Geo fence";
            default:
                return "Unknown geo fence transition";
        }
    }

    protected void sendEventDetailNotificatonIntent(String event_name) {
        Log.d(TAG, "Sending Geofencing notification");

        Toast.makeText(context, "Geo fence triggered - " + event_name, Toast.LENGTH_SHORT).show();

        NotificationCompat.Builder notificationBuild = new NotificationCompat.Builder(context, "notify_001")
                .setSmallIcon(R.drawable.amu_bubble_mask)
                .setContentTitle("Crossing geo fence")
                .setContentText(event_name);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(ThreadLocalRandom.current().nextInt(0, 1000 + 1), notificationBuild.build());

//        int requestID = (int) System.currentTimeMillis();
//        Intent event_detail_intent = new Intent(context, LocationDetailActivity.class);
//        event_detail_intent.putExtra("NotifyTitle",event_name);
//
//        PendingIntent pIntent = PendingIntent.getActivity(context, requestID,
//                event_detail_intent, 0);
//        NotificationCompat.Builder notificationBuilder;
//        notificationBuilder = new NotificationCompat.Builder(context)
//                .setContentTitle(event_name)
//                .setSmallIcon(R.drawable.amu_bubble_mask);
//        // Set pending intent
//        notificationBuilder.setContentIntent(pIntent);
//
//        // Set Vibrate, Sound and Light
//        int defaults = 0;
//        defaults = defaults | Notification.DEFAULT_LIGHTS;
//        //	defaults = defaults | Notification.DEFAULT_VIBRATE;
//        defaults = defaults | Notification.DEFAULT_SOUND;
//
//        notificationBuilder.setDefaults(defaults);
//        // Set the content for Notification
//        notificationBuilder.setContentText("Tap here to view detail");
//        // Set autocancel
//        notificationBuilder.setAutoCancel(true);
//        NotificationManager notificationManager =
//                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        // Assign random number for multiple notification
//        Random random = new Random();
//        int randomNumber = random.nextInt(9999 - 1000) + 1000;
//        notificationManager.notify(randomNumber /* ID of notification */, notificationBuilder.build());
    }
}
