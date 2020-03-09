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



import android.content.pm.PackageManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import android.database.Cursor;
import java.text.ParseException;
import android.media.MediaPlayer;
import android.provider.Settings;
import com.google.android.gms.location.DetectedActivity;
import android.support.annotation.NonNull;
import android.Manifest;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.content.Context;
import android.widget.ImageView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.location.ActivityRecognition;
import android.widget.TextView;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import com.google.android.gms.common.api.GoogleApiClient;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.maps.OnMapReadyCallback;
import android.util.Log;
import android.widget.LinearLayout;
import android.view.View;
import com.google.android.gms.maps.SupportMapFragment;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback{

    //Declare and initialize the variables
    public static final String ACTIVITY_INTENT = "activity_intent";
    private String ActivityTAG = MainActivity.class.getSimpleName();
    BroadcastReceiver broadcastReceiver;
    public GoogleApiClient mApiClient;

    private TextView ActivityName, ActivityConf;
    private ImageView ActivityImage;
    private LinearLayout LinearLyt;

    String nam, activityString, tim = null;
    Date timeFromDB = null;
    long activityTimeLength = 0;
    int numberrows, activityPic =0;
    MediaPlayer player = new MediaPlayer();
    private GoogleMap mMap;
    private ActivityDB myDB;
    String dateString;
    String timeString;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create and initialize mediaPlayer object
        player = MediaPlayer.create(this,
                Settings.System.DEFAULT_RINGTONE_URI);

        //setting user permission for locations
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }



        LinearLyt = findViewById(R.id.mapLayout);
        ActivityName = findViewById(R.id.activityName);
        ActivityConf = findViewById(R.id.activityConf);
        ActivityImage = findViewById(R.id.activityImg);
        myDB = new ActivityDB(this);

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("activity_intent")) {
                    int activitytype = intent.getIntExtra("type", -1);
                    int activityconfidence = intent.getIntExtra("confidence", 0);
                    try {
                        handleUserActivity(activitytype, activityconfidence);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        startTracking();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle){
        startTracking();
    }



    private void handleUserActivity(int activity, int activityconfidence) throws ParseException {
//        activityString = getString(R.string.still);
//        activityPic = R.drawable.still;

        Calendar c = Calendar.getInstance();
        SimpleDateFormat tf = new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        String formattedTime = tf.format(c.getTime());
        dateString = formattedDate;
        timeString = formattedTime;

        switch (activity) {
            case DetectedActivity.IN_VEHICLE: {
                activityString = getString(R.string.in_vehicle);
                activityPic = R.drawable.driving;
                break;
            }
            case DetectedActivity.RUNNING: {
                activityString = getString(R.string.running);
                activityPic = R.drawable.running;
                break;
            }
            case DetectedActivity.STILL: {
                activityString = getString(R.string.still);
                activityPic = R.drawable.still;
                break;
            }
            case DetectedActivity.WALKING: {
                activityString = getString(R.string.walking);
                activityPic = R.drawable.walking;
                break;
            }
        }

        Log.e(ActivityTAG, "User activity: " + activityString + ", Confidence: " + activityconfidence);



        if (activityString != null && activityconfidence > 60) {
            System.out.println("Activity detected" + activityString + " with confidence " + activityconfidence);
            //Getting details of last activity added from DB
            ActivityName.setText(activityString);
            ActivityConf.setText("Confidence: " + activityconfidence);
            ActivityImage.setImageResource(activityPic);

            //Fetch from DB and set the detected activity
            numberrows = myDB.numberOfRows();
            System.out.println("numberbof rows" + numberrows );
            if (numberrows > 0){
                Cursor rs = myDB.getActivity();
                rs.moveToLast();
                nam = rs.getString(rs.getColumnIndex(myDB.activityname));
                tim = rs.getString(rs.getColumnIndex(myDB.time));
                timeFromDB = tf.parse(tim);
                System.out.println("last activity in DB is " + nam);
            }


            //Inserting activity and its time into DB
            if(nam == null || (nam.equals(activityString)==false)) {
                System.out.println("New activity is added to DB = " + activityString);
                myDB.addActivity(activityString, dateString, timeString);
                playMusic(activityString);
            }


            //if the activity has changed , we toast a message of last activity
            if( nam != null && (nam.equals(activityString)==false) && numberrows > 0)
            {
                System.out.println("printing toast msg for " + nam);
                    Date CurrentActivityTime = tf.parse(timeString);
                    activityTimeLength = CurrentActivityTime.getTime() - timeFromDB.getTime();
                    System.out.println(activityTimeLength + "activity time in miliseconds");
                    System.out.println(activityTimeLength / 1000 + "activity time in seconds");
                    Toast.makeText(getApplicationContext(), nam + " for " + activityTimeLength / 1000 + "seconds",
                            Toast.LENGTH_SHORT).show();

            }

            //if activity is walking or in vheicle , show the map with current location tracking
            if(activityString.equals("Walking") || activityString.equals("InVehicle")){
                System.out.println("activity is walking and trying to get maps ~~~~~~~~~~~~~~~~~~~~~");
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
                mapFragment.getMapAsync(this);
                LinearLyt.setVisibility(View.VISIBLE);
            }
            else{
                LinearLyt.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        stopTracking();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("Connection Failed!");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        mMap.setMyLocationEnabled(true);
    }




    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter("activity_intent"));

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    //Method to play music when walking or Running Activity
    private void playMusic(String label){
        System.out.println(player);
        if(player != null)
            System.out.println(player + "printing player object" );
        else {
            System.out.println("no player object");
            player = MediaPlayer.create(this,
                    Settings.System.DEFAULT_RINGTONE_URI);

        }
        //Check if the activity is walking
        if(label.equals("Walking") || label.equals("Running")) {
            //start the player, if its not playing already
            System.out.println("Media Player: Walking or running play music");
            if((!player.isPlaying())) {
                player.start();
                player.setLooping(true);
            }
        }
        else
        {
            System.out.println("Media Player: check id playing");
            //Stop the player for other activities
            if(player.isPlaying()) {
                System.out.println("Media Player: stopping play music");
                player.pause();
            }
        }

    }

    private void startTracking() {
        Intent intent = new Intent(MainActivity.this, ActivityRecognitionService.class);
        startService(intent);
    }

    private void stopTracking() {
        Intent intent = new Intent(MainActivity.this, ActivityRecognitionService.class);
        stopService(intent);
    }


}
