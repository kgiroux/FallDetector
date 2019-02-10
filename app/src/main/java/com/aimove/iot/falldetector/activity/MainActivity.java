package com.aimove.iot.falldetector.activity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.aimove.iot.falldetector.R;
import com.aimove.iot.falldetector.detector.FallDetector;
import com.aimove.iot.falldetector.utils.AccelerometerCoordinate;
import com.aimove.iot.falldetector.utils.GyrometerCoordinate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

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
    private Button buttonRecord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fallDetector = new FallDetector();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        buttonStop = findViewById(R.id.stopbutton);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);

        buttonStop.setOnClickListener(this);
        buttonRecord = findViewById(R.id.recordbutton);
        buttonRecord.setOnClickListener(this);
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
            int result = fallDetector.runAnalysis();
            Log.d("Result", String.valueOf(result));
            if (result == -1){
                if (mp == null){
                    mp = MediaPlayer.create(this, R.raw.falling);
                }
                if (!mp.isPlaying()){
                    mp.start();
                }

            }
        } else {
            GyrometerCoordinate gyrometerCoordinate = new GyrometerCoordinate();
            gyrometerCoordinate.setX(event.values[0]);
            gyrometerCoordinate.setY(event.values[1]);
            gyrometerCoordinate.setZ(event.values[2]);
            fallDetector.addDataToListGyroMeterList(gyrometerCoordinate);
            int result = fallDetector.runAnalysis();
            Log.d("Result", String.valueOf(result));
            if (result == -1){
                if (mp == null){
                    mp = MediaPlayer.create(this, R.raw.falling);
                }
                if (!mp.isPlaying()){
                    mp.start();
                }

            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.stopbutton){
            mp.stop();
            mp.release();
            mp = null;
        }else if (v.getId() == R.id.recordbutton){
            Date date = new Date();
            String filename = "record_data_"+date.getTime()+"_acc.txt";
            String filenameGyro = "record_data_"+date.getTime()+"_gyro.txt";
            File directory = new File(this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "record_data");
            String path = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath() + "/record_data/";

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
    }

}
