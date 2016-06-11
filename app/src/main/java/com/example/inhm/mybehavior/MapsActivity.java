package com.example.inhm.mybehavior;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,AdapterView.OnItemSelectedListener {

    private GoogleMap mMap;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;

    Intent intent;

    protected LocationManager locationManager = null;

    ArrayList<String> arrayList;
    String lat;
    String format;
    SimpleDateFormat sdf;
    String today_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

//        /**
//         * 오늘 날짜를 가져옵니다.
//         */
//        format = new String("yyyy-MM-dd");
//        sdf = new SimpleDateFormat(format, Locale.KOREA);
//        today_date = sdf.format(new Date());
//        arrayList.add(today_date);

        /**
         * 스피너 날짜를 받아옵니다.
         */
        Intent intent = getIntent();
        String spinner_date = (String) intent.getSerializableExtra("spinner_date");

        arrayList = new ArrayList<String>();

        /**
         * 날짜를 콤마로 나눕니다.
         */
        String[] spinner_arr = spinner_date.split(",");

        /**
         * 배열 크기만큼 스피너 갯수를 저장합니다.
         */
        for(int i = 0 ; i < spinner_arr.length;i++){
            arrayList.add(spinner_arr[i]);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,arrayList);
        Spinner sp = (Spinner) findViewById(R.id.spinner);
        sp.setPrompt("저장된 날짜");//스피너 제목
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        String gps = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!(gps.matches(".*gps.*") && gps.matches(".*network.*"))) {

            // GPS OFF 일때 Dialog 표시
            AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
            gsDialog.setTitle("위치 서비스 설정");
            gsDialog.setMessage("무선 네트워크 사용, GPS 위성 사용을 모두 체크하셔야 정확한 위치 서비스가 가능합니다.\n위치 서비스 기능을 설정하시겠습니까?");
            gsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // GPS설정 화면으로 이동
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                }
            })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    }).create().show();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


        mMap.clear();
        Toast.makeText(this,arrayList.get(position),Toast.LENGTH_LONG).show();
//        ServerThread serverThread = new ServerThread(arrayList.get(position));
//        serverThread.start();
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        lat = serverThread.latLng();

        if(position == 0) {
            lat = "37.517180,127.041268/37.557180,127.051268/40.0,130.0/50.0,133.0/50.23,133.1234";
        }else if(position == 1) {
            lat = "12.123,13.441/36.76,11.214/36.55,13.1331";
        }else {
            lat = "40.123,31.242/35.2424,33.1412/28.124,60.1242";
        }
        ArrayList<LatLng> arrayList1 = new ArrayList<LatLng>();

        String[] str_arr = lat.split("/");
        String[] split_by_comma;
        for(int i = 0; i < str_arr.length;i++){
            split_by_comma = str_arr[i].split(",");

            double v1 = Double.parseDouble(split_by_comma[0]);
            double v2 = Double.parseDouble(split_by_comma[0]);
            arrayList1.add(new LatLng(v1, v2));
            MarkerOptions marker = new MarkerOptions();
            marker.position(arrayList1.get(i));
            mMap.addMarker(marker);

            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.RED);
            polylineOptions.width(5);
            polylineOptions.addAll(arrayList1);
            mMap.addPolyline(polylineOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(arrayList1.get(i),15));
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
