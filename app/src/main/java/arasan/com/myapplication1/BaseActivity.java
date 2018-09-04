package arasan.com.myapplication1;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import arasan.com.myapplication1.BusEventModel.BatteryLevelEvent;
import arasan.com.myapplication1.BusEventModel.LocationLevelEvent;

import static android.content.Intent.ACTION_BATTERY_CHANGED;


public class BaseActivity extends AppCompatActivity {
    private LocationListener listener;
    private LocationManager locationManager;
    private Geocoder geocoder;
    String _Location = "";
    private  Location locFrom, locTo;


    String TAG = "BASE";
    private BroadcastReceiver myReceiver;
    int newlvl = 0;
    int oldlvl = 0;
    double newLAT = 0.0d;
    double newLON = 0.0d;
    double oldLAT = 0.0d;
    double oldLON = 0.0d;
    int lvl = 0;
    TextView txtBattery ,txtLatLan;
    Button btnNext, btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
       EventBus.getDefault().register(this);



    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!runtime_permission())
        {


            listener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.i(TAG, "onLocationChanged: "+ location.getLongitude() + " " + location.getLatitude());
                    geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());


                    List<Address> listAddresses = null;
                    try {
                        listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude() , 1);

                        if(null!=listAddresses&&listAddresses.size()>0){
                            _Location = listAddresses.get(0).getAddressLine(0);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    newLON = location.getLongitude();
                    newLAT = location.getLatitude();

                    if(newLON != oldLON || newLAT != oldLAT)
                    {

                        if(oldLON != 0 && oldLAT != 0)
                        {
                            locFrom = new Location("Loc1");
                            locFrom.setLatitude(oldLAT);
                            locFrom.setLongitude(oldLON);

                            locTo = new Location("Loc2");
                            locTo.setLongitude(newLON);
                            locTo.setLatitude(newLAT);

                            double distance = locFrom.distanceTo(locTo);
                            Log.i(TAG, "Distance : " + distance);
                            Log.i(TAG, "Old LAT " + oldLAT + " LON " + oldLON + " NEW LAT " + newLAT + " NEW LON " + newLON);
                    if(distance >=50.0d)
                        {
                            Intent i = new Intent("location_update");
                             i.putExtra("LAT",location.getLatitude());
                             i.putExtra("LAN",location.getLongitude());
                             i.putExtra("LOCNAME" , _Location);

                            sendBroadcast(i);
                        }

                        }
                        oldLON = newLON;
                        oldLAT = newLAT;
                    }


                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {
                    Intent i = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            };
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            //noinspection MissingPermission
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, listener);
        }
        registerReceiver(getMyReceiver,new IntentFilter(ACTION_BATTERY_CHANGED));
        registerReceiver(getMyReceiver,new IntentFilter("location_update"));

    }

    private BroadcastReceiver getMyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            Log.i(TAG, "onReceive: Action = " + intent.getAction().toString());
            if(intent.getAction() == "android.intent.action.BATTERY_CHANGED")
            {
                newlvl  =  extras.getInt(BatteryManager.EXTRA_LEVEL,0);

                if(newlvl != oldlvl)
                {
                    Toast.makeText(context, "Receiver = " + newlvl + " old = " + oldlvl, Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(new BatteryLevelEvent(newlvl));
                    oldlvl = newlvl;
                }
            }

            if(intent.getAction() == "location_update")
            {
                String LAT,LAN,LocName;
                LAT = intent.getExtras().get("LAT").toString();
                LAN = intent.getExtras().get("LAN").toString();
                LocName = intent.getExtras().get("LOCNAME").toString();
                EventBus.getDefault().post(new LocationLevelEvent(LAT,LAN,LocName));
                Toast.makeText(context, "Receiver = LAT" + LAT + " LAN = " + LAN, Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(myReceiver);
       // EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        if(locationManager != null)
        {
            locationManager.removeUpdates(listener);
        }
        unregisterReceiver(myReceiver);
        EventBus.getDefault().unregister(this);
    }

    private boolean runtime_permission() {


        if(Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);
            return true;
        }
        else
        {
            return false;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED &&grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {
              //  enable_buttons();
            }
            else
            {
                runtime_permission();
            }
        }
    }


    //@Subscribe
//    public void onMessageEvent(BatteryLevelEvent event)
//{
//    batterylvl = event.getBatteryLevel();
//
//    Toast.makeText(getApplicationContext(),"Battery Level = " + batterylvl ,Toast.LENGTH_SHORT).show();
//}
    @Subscribe
    public void onEvent(BatteryLevelEvent event)
    {
        txtBattery.setText("Battery = " + event.getBatteryLevel() + "%");
    }
@Subscribe
public  void onEvent(LocationLevelEvent event)
{
    txtLatLan.setText("LT = " + event.getLAT()+ "\n");
    txtLatLan.setText("LN = " + event.getLAN()+ "\n");
}
}
