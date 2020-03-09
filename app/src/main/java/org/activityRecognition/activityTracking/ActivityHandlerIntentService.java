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

import android.content.Intent;
import com.google.android.gms.location.ActivityRecognitionResult;
import android.app.IntentService;
import com.google.android.gms.location.DetectedActivity;
import android.support.v4.content.LocalBroadcastManager;


import java.util.ArrayList;

public class ActivityHandlerIntentService extends IntentService {
    public ActivityHandlerIntentService() {
        super(ActivityHandlerIntentService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)){
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();
        for (DetectedActivity activity : detectedActivities) {
            sendDetectedActivities(activity);
        }}
    }

    private void sendDetectedActivities(DetectedActivity activity) {
        Intent intent = new Intent("activity_intent");
        intent.putExtra("type", activity.getType());
        intent.putExtra("confidence", activity.getConfidence());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
