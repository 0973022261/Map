package com.example.inhm.mybehavior;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onButton1Clicked(View v){
        MapService mapService = new MapService(MainActivity.this);
        if(mapService.isGetLocation()) {
            double latitude = mapService.getLatitude();
            double longitude = mapService.getLongitude();

            Log.e("adTast ", "lat = "+latitude+"lon = "+longitude);

                    Toast.makeText(getApplicationContext(),"위도 = "+latitude+", 경도 = "+longitude,Toast.LENGTH_LONG).show();
        } else {
            mapService.showSettingAlert();
        }

//        Intent intent = new Intent(this,MapService.class);
//        startService(intent);
    }
    public void onButton2Clicked(View v){

//        ServerThread serverThread = new ServerThread();
//        serverThread.start();
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        String date = serverThread.spinner_date();
//
        String date = "2016-05-12,2016-05-13,2016-05-14,2016-05-15,2016-05-16,2016-05-17,2016-05-18,2016-05-19";
        Intent intent = new Intent(this,MapsActivity.class);
        intent.putExtra("spinner_date",date);
        startActivity(intent);
    }
}
