package arasan.com.myapplication1.BusEventModel;

import android.location.LocationManager;

/**
 * Created by Senthilarasan.S on 24/11/2017.
 */

public class LocationLevelEvent {
    private String LAT ,LAN , LocName;

    public LocationLevelEvent(String LAT, String LAN, String LocName)
    {
        this.LAT  = LAT;
        this.LAN = LAN;
        this.LocName = LocName;
    }

    public String getLAT()
    {
        return this.LAT;
    }

    public String getLAN()
    {
        return this.LAN;
    }

    public String getLocName()
    {
        return this.LocName;
    }
}
