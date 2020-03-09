/*
References:

https://code.tutsplus.com/tutorials/how-to-recognize-user-activity-with-activity-recognition--cms-25851
https://developer.android.com/studio/write/image-asset-studio
https://developers.google.com/android/reference/com/google/android/gms/location/ActivityRecognitionClient#requestActivityUpdates(long,%20android.app.PendingIntent)
https://developers.google.com/android/reference/com/google/android/gms/location/DetectedActivity
https://www.youtube.com/watch?v=jHZ5HV7zsOs
https://developer.android.com/reference/android/support/v4/content/LocalBroadcastManager
https://developer.android.com/reference/android/content/BroadcastReceiver.html
http://blog.maxaller.name/android/2016/11/24/local-broadcast-receiver.html
https://developers.google.com/maps/documentation/android-sdk/start
https://developer.android.com/guide/topics/media/mediaplayer
*/




package org.activityRecognition.activityTracking;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class ActivityRecognitionService extends Service {
    private static final String TAG = ActivityRecognitionService.class.getSimpleName();

    private Intent intent;
    private PendingIntent pendingIntent;
    IBinder iBinder = new ActivityRecognitionService.LocalBinder();
    private ActivityRecognitionClient activityRecognitionClient;


    public class LocalBinder extends Binder {
        public ActivityRecognitionService getServerInstance() {
            return ActivityRecognitionService.this;
        }
    }

    public ActivityRecognitionService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        activityRecognitionClient = new ActivityRecognitionClient(this);
        intent = new Intent(this, ActivityHandlerIntentService.class);
        pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        activityUpdatesHandler();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void activityUpdatesHandler() {
        Task<Void> task = activityRecognitionClient.requestActivityUpdates(
                3000,
                pendingIntent);

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(getApplicationContext(), "Waiting for activity to be detected", Toast.LENGTH_SHORT).show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Requesting activity updates failed to start", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // removeActivityUpdatesButtonHandler();
    }


    public void removeActivityUpdatesButtonHandler() {
        Task<Void> task = activityRecognitionClient.removeActivityUpdates(
                pendingIntent);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(getApplicationContext(),
                        "Removed activity updates successfully!",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to remove activity updates!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


}
