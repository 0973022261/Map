package com.example.inhm.mybehavior;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapService extends Service implements LocationListener {

    private final Context mContext;
    private GoogleMap mMap;

    double lat;//위도
    double lon;//경도

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean isGetLocation = false;

    Location location;

    private static final int REQUSET_INPUT = 1;

    //최소 GPS 정보 업데이트 거리 10미터
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    //최소 GPS 정보 업데이트 시간 10분
    private static final long MIN_TIME_BW_UPDATES = 1000 * 10;

    protected LocationManager locationManager = null;
    private LocationListener locationListener = null;

    public MapService(Context context) {


        Log.d("inhm1234","1");
        mContext = context;
        Log.d("inhm1234","2");
        getLocation();
        Log.d("inhm1234", "3");

    }

    public Location getLocation() {
        try {
            Log.d("inhm1234","4");
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            //GPS 정보 가져오기
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            //현재 네트워크 상태 값 알아오기
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // GPS 와 네트워크 사용이 가능하지 않을때 소스 구현

                Log.d("inhm1234","5");
            } else {

                Log.d("inhm1234","6");
                isGetLocation = true;

                //네트워크 정보로 부터 위치값 가져오기
                if (isNetworkEnabled) {
                    Log.d("inhm1234","isNetworkEnabled");
                    if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    }
                    Log.d("inhm1234","1");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        Log.d("inhm1234","locationManager != null");
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            //위도 경도 저장
                            lat = location.getLatitude();
                            lon = location.getLongitude();

                            Log.d("inhm1234","lat = "+lat+",lon ="+lon);
                        }
                    }
                }
                if (isGPSEnabled) {
                    Log.d("inhm1234","isGPSEnabled");
                    if (location == null) {
                        Log.d("inhm1234","location == null");
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("inhm1234", ""+e);
        }
        return location;
    }

    /**
     * GPS 종료
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(MapService.this);
        }
    }

    /**
     * 위도값을 가져옵니다.
     */
    public double getLatitude(){
        if(location != null) {
            lat = location.getLatitude();
            Log.d("inhm1234","lat = "+lat);
        }
        Log.d("inhm1234","8");
        return lat;
    }
    /**
     * 경도값을 가져옵니다.
     */
    public double getLongitude(){
        if(location != null) {
            lon = location.getLongitude();
            Log.d("inhm1234","lon = "+lon);
        }
        Log.d("inhm1234","9");
        return lon;
    }

    /**
     * GPS 나 WIFI 정보가 켜져있는지 확인합니다.
     */

    public boolean isGetLocation() {
        return this.isGetLocation;
    }

    /**
     * GPS 정보를 가져오지 못했을때 설정 창으로 갈지 물어보는 alret 창
     */

    public void showSettingAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle("GPS 사용 유무 세팅");
        alertDialog.setMessage("GPS 설정 화면으로 가시겠습니까?");

        //OK를 누르게 되면 설정창으로 이동.
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        //CANCLE 하면 종료 합니다.
        alertDialog.setNegativeButton("CANCLE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
        Log.d("inhm1234","10");

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("inhm1234","11");
        lat = location.getLatitude();
        lon = location.getLongitude();
        Log.d("inhm1234", "10");

        Toast.makeText(mContext,"lat = "+lat+",lon = "+lon,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        double lat = getLatitude();
        double lon = getLongitude();
        String location = String.format(String.valueOf(lat+lon));
        Log.d("inhm1234","location = "+location);
        ServerThread serverThread = new ServerThread(REQUSET_INPUT,location);
        serverThread.start();
        return super.onStartCommand(intent, flags, startId);
    }
}
