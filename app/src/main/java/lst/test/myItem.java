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
        last_location=null;
    }
    String Address;
    String Name;
    int rssi;
    double latitude;
    double longitude;
    String last_location;
    String last_time;
}
