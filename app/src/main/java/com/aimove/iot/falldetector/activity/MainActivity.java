package com.aimove.iot.falldetector.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aimove.iot.falldetector.R;
import com.aimove.iot.falldetector.detector.FallDetector;
import com.aimove.iot.falldetector.utils.AccelerometerCoordinate;
import com.aimove.iot.falldetector.utils.GyrometerCoordinate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {

    /**
     * Fall detector
     */
    private FallDetector fallDetector;

    /**
     * Sensor Manager
     */
    private SensorManager sensorManager;

    /**
     * Media Player
     */
    private MediaPlayer mp = null;

    /**
     * button for stop the sound
     */
    private Button buttonStop;
    /**
     * button for the recording
     */
    private Button buttonSendText;

    /**
     * Check Status Permission
     */
    public  static final int PERMISSIONS_MULTIPLE_REQUEST = 42;

    /**
     * Last Address
     */
    private String lastAddress = "";

    /**
     * Instance of SMS manager
     */
    private SmsManager smsManager = SmsManager.getDefault();

    public static final String SENT_SMS_ACTION_NAME = "SMS_SENT";
    public static final String DELIVERED_SMS_ACTION_NAME = "SMS_DELIVERED";
    private static final long SOUND_LENGTH = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Fall Detector");
        fallDetector = new FallDetector();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
        buttonStop = findViewById(R.id.stop);
        buttonStop.setOnClickListener(this);
        buttonSendText = findViewById(R.id.send_text);
        buttonSendText.setOnClickListener(this);

        launchServiceLocation();
    }

    public void launchServiceLocation(){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        boolean enableGPS = sharedPreferences.getBoolean("enableGps", false);
        if(enableGPS){
            checkPermission();
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            // Define a listener that responds to location updates
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    Address address = makeUseOfNewLocation(location);
                    String address_text = "";
                    if (address != null){
                        address_text = address.getAddressLine(0) +"," +address.getAdminArea();
                    }
                    lastAddress = address_text;

                }

                public void onStatusChanged(String provider, int status, Bundle extras) {}

                public void onProviderEnabled(String provider) {}

                public void onProviderDisabled(String provider) {}
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }else if(id == R.id.action_history) {
            Intent intent  = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        }else if(id == R.id.action_record) {
            this.saveDataAfterRecord();
        }else if(id == R.id.action_about) {
            Intent intent  = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            AccelerometerCoordinate accelerometerCoordinate = new AccelerometerCoordinate();
            accelerometerCoordinate.setX(event.values[0]);
            accelerometerCoordinate.setY(event.values[1]);
            accelerometerCoordinate.setZ(event.values[2]);
            fallDetector.addDataToList(accelerometerCoordinate);
            int result = fallDetector.runAnalysis(accelerometerCoordinate);
            if (result == -1)
            {
                if (mp == null){
                    mp = MediaPlayer.create(this, R.raw.falling);
                    Log.e("Sound", "playing");
                }
                if (!mp.isPlaying()){
                    mp.start();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mp.stop();
                            mp.release();
                            mp = null;
                            writeTextToFallingRecord();
                            sendSms();
                            makePhoneCall();
                        }
                    }, SOUND_LENGTH);
                }
            }
        }else {
            GyrometerCoordinate gyrometerCoordinate = new GyrometerCoordinate();
            gyrometerCoordinate.setX(event.values[0]);
            gyrometerCoordinate.setY(event.values[1]);
            gyrometerCoordinate.setZ(event.values[2]);
            fallDetector.addDataToListGyroMeterList(gyrometerCoordinate);
        }
    }

    /**
     * Write history of fall in a file
     */
    private void writeTextToFallingRecord(){
        Date date = new Date();
        String filename = "historyOfFall.txt";
        File directory = new File(this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "record_data");
        String path = Objects.requireNonNull(this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)).getPath() + "/record_data/";
        File fileHistory = new File(path+filename);
        if (!directory.mkdirs()) {
            Log.e("Help", "Directory not created");
        }
        try{
            FileOutputStream fileInput = new FileOutputStream(fileHistory, true);
            PrintStream printstream = new PrintStream(fileInput);
            printstream.print("Fall the "+ DateFormat.getDateTimeInstance().format(date) +"\n");
            fileInput.close();
        } catch (IOException e) {
            Log.e("Write", "Error during the writing of the file", e);
        }
    }

    /**
     * Function that is called when save the data for analysis purpose
     */
    private void saveDataAfterRecord(){
        Date date = new Date();
        String filename = "record_data_"+date.getTime()+"_acc.txt";
        String filenameGyro = "record_data_"+date.getTime()+"_gyro.txt";
        File directory = new File(this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "record_data");
        String path = Objects.requireNonNull(this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)).getPath() + "/record_data/";

        File fileAcc = new File(path+filename);
        File fileGyro = new File(path+filenameGyro);
        if (!directory.mkdirs()) {
            Log.e("Help", "Directory not created");
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileAcc));
            for (AccelerometerCoordinate accelerometerCoordinate : fallDetector.retrieveListOfCoordinates()){
                writer.write(""
                        +accelerometerCoordinate.getX()
                        +" "
                        +accelerometerCoordinate.getY()
                        +" "
                        +accelerometerCoordinate.getZ());
                writer.newLine();
            }
            writer.flush();
            writer.close();

            writer = new BufferedWriter(new FileWriter(fileGyro));
            for (GyrometerCoordinate gyrometerCoordinate : fallDetector.retrieveListOfGyrometerCoordinates()){
                writer.write(""
                        +gyrometerCoordinate.getX()
                        +" "
                        +gyrometerCoordinate.getY()
                        +" "
                        +gyrometerCoordinate.getZ());
                writer.newLine();
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.stop){
            if(mp != null){
                mp.stop();
                mp.release();
            }
            mp = null;
        } else if (v.getId() == R.id.send_text) {
            sendSms();
        }
    }

    /**
     * Method that will check permission
     */
    private void checkPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(MainActivity.this, SEND_SMS);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{SEND_SMS, ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, CALL_PHONE}, PERMISSIONS_MULTIPLE_REQUEST);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                if (grantResults.length > 0) {
                    boolean checkCallPhonePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean checkFinePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean checkCoarsePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean checkWritePermission = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean checkReadPermission = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean checkCallPermission = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                    if (checkCallPhonePermission && checkFinePermission && checkCoarsePermission && checkWritePermission && checkReadPermission && checkCallPermission) {
                        Log.e("Perm", "Permission Granted");
                    }
                }
        }
    }

    private void sendSms(){
        checkPermission();
        try {
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            String contactNumber = sharedPreferences.getString("contactNumber", "");
            if (null != contactNumber && !contactNumber.isEmpty()){
                String customizeMessage = sharedPreferences.getString("customizeMessage","I fall in the street, please call me in a way to check if I'm ok.");
                PendingIntent sentPI = PendingIntent.getBroadcast(MainActivity.this, 0, new Intent(SENT_SMS_ACTION_NAME), 0);
                PendingIntent deliveredPI = PendingIntent.getBroadcast(MainActivity.this, 0, new Intent(DELIVERED_SMS_ACTION_NAME), 0);

                ArrayList<String> parts = smsManager.divideMessage(customizeMessage + " "+ lastAddress);
                ArrayList<PendingIntent> sendList = new ArrayList<>();
                sendList.add(sentPI);

                ArrayList<PendingIntent> deliverList = new ArrayList<>();
                deliverList.add(deliveredPI);
                smsManager.sendMultipartTextMessage(contactNumber, null, parts, sendList, deliverList);
                Toast.makeText(this, "OK", Toast.LENGTH_LONG).show();
            }



        } catch (Exception e) {
            Toast.makeText(this, "Error"+e, Toast.LENGTH_LONG).show();
        }
    }

    public void makePhoneCall(){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String contactNumber = sharedPreferences.getString("contactNumber", "");
        if(null != contactNumber && !contactNumber.isEmpty()){
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+contactNumber));
            startActivity(intent);
        }


    }

    private Address makeUseOfNewLocation(Location location){
        try{
            Geocoder geo = new Geocoder(MainActivity.this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0) {
                return addresses.get(0);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        buttonStop.setOnClickListener(null);
        buttonSendText.setOnClickListener(null);
        sensorManager.unregisterListener(this);
        sensorManager = null;
    }
}
