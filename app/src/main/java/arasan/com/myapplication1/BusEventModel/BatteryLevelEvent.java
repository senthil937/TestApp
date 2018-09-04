package arasan.com.myapplication1.BusEventModel;

/**
 * Created by Senthilarasan.S on 23/11/2017.
 */

public class BatteryLevelEvent {
    private int batteryLevel;

    public BatteryLevelEvent(int batteryLevel)
    {
        this.batteryLevel = batteryLevel;
    }
    public int getBatteryLevel()
    {
        return this.batteryLevel;
    }
}
