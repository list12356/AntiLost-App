package lst.test;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by Li on 2016/4/12.
 */
public class myItem {
    myItem()
    {
        rssi=0;
        longitude=0;
        latitude=0;
    }
    String Address;
    String Name;
    int rssi;
    double longitude;
    double latitude;
    String last_time;
}
