package com.btitsolutions.staygrateful;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Context context = this;
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(2000);
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
                finally{
                    SharedPreferences SystemSettings = getSharedPreferences("SystemSettings", MODE_PRIVATE);
                    String language = SystemSettings.getString("LanguageSetting", "am");

                    Resources res = getApplicationContext().getResources();
                    // Change locale settings in the app.
                    DisplayMetrics dm = res.getDisplayMetrics();
                    android.content.res.Configuration conf = res.getConfiguration();
                    conf.setLocale(new Locale(language.toLowerCase())); // API 17+ only.
                    res.updateConfiguration(conf, dm);

                    finish();
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();

    }
}
