package com.aimove.iot.falldetector.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.aimove.iot.falldetector.R;

public class WelcomeActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        long SPLASH_LENGTH = 3000;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        /* Récupération du numéro de version de l'application */
        try{
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            // Récupération du TextView permettant l'affichage de la version
            TextView versionNumber = findViewById (R.id.versionNumber);
            versionNumber.setText (version);
        }catch (PackageManager.NameNotFoundException e ){
            e.printStackTrace ();
        }

        /* Affichage du SplashScreen pendant la durée SPLASH_LENGTH */
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }, SPLASH_LENGTH);



    }
}
