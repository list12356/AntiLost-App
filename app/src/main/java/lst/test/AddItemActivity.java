package lst.test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AddItemActivity extends AppCompatActivity {

    BluetoothAdapter myBluetoothAdapter;
    private int REQUEST_ENABLE_BT = 1;
    ListView myDeviceView;
    List<String> myDeviceList;
    ArrayAdapter myDeviceAdapter;
    TextView myText;
    Button bt_search;
    private boolean hasregister=false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        setView();
        setBluetooth();

    }

    private void setBluetooth(){


        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (myBluetoothAdapter == null) {
            // 设备不支持蓝牙
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("No bluetooth devices");
            dialog.setMessage("Your equipment does not support bluetooth, please change device");

            dialog.setNegativeButton("cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            dialog.show();
        }
        if (!myBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
    public void setView()
    {
        myDeviceList=new ArrayList<>();
        myDeviceAdapter = new ArrayAdapter(this, R.layout.simple_list_item_1,myDeviceList);
        myDeviceView=(ListView)findViewById(R.id.DeviceToAdd);  ;
        myDeviceView.setAdapter(myDeviceAdapter);
        myDeviceView.setOnItemClickListener(new ListClickMonitor());
        bt_search=(Button)findViewById(R.id.bt_search);
        bt_search.setOnClickListener(new ClickMonitor());
        myText=(TextView)findViewById(R.id.AddText);
        myText.setText("Please select the device you want to add");
    }
    public void onBackPressed() {
        Intent in=new Intent(AddItemActivity.this,MainActivity.class);
        startActivity(in);
        finish();
    }
    private void findAvalibleDevice(){
        //获取可配对蓝牙设备
        if(myBluetoothAdapter!=null){
            myDeviceList.clear();
            myDeviceAdapter.notifyDataSetChanged();
        }
        Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                myDeviceList.add(device.getName() + "\n" + device.getAddress());
                myDeviceAdapter.notifyDataSetChanged();
            }
        }
        else{ //不存在已经配对过的蓝牙设备
            myDeviceList.add("No can be matched to use bluetooth");
            myDeviceAdapter.notifyDataSetChanged();
        }
    }
    public class ListClickMonitor implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3)
        {
            Log.e("msgParent", "Parent= "+arg0);
            Log.e("msgView", "View= "+arg1);
            Log.e("msgChildView", "ChildView= " + arg0.getChildAt(pos - arg0.getFirstVisiblePosition()));

            final String msg = myDeviceList.get(pos);

            if(myBluetoothAdapter!=null&&myBluetoothAdapter.isDiscovering()){
                myBluetoothAdapter.cancelDiscovery();
                bt_search.setText("repeat search");
            }

            AlertDialog.Builder dialog = new AlertDialog.Builder(AddItemActivity.this);// 定义一个弹出框对象
            dialog.setTitle("Please enter your name of the device");
            dialog.setMessage(msg);
            final EditText tmpedit=new EditText(AddItemActivity.this);
            dialog.setView(tmpedit);
            dialog.setPositiveButton("Add",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            String tmpname;
                            if(tmpedit.getText().toString().length()!=0)
                                tmpname=tmpedit.getText().toString();
                            else
                                tmpname=msg.substring(0,msg.length()-17);
                            MainActivity.myItemList.add(tmpname,msg.substring(msg.length()-17));
                            Intent in=new Intent(AddItemActivity.this,MainActivity.class);
                            startActivity(in);
                            finish();
                        }
                    });
            dialog.setNegativeButton("cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            dialog.show();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(resultCode){
            case RESULT_OK:
                findAvalibleDevice();
                break;
            case RESULT_CANCELED:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class ClickMonitor implements View.OnClickListener {

        public void onClick(View v) {
            if(myBluetoothAdapter.isDiscovering()){
                myBluetoothAdapter.cancelDiscovery();
                bt_search.setText("repeat search");
            }else{
                findAvalibleDevice();
                myBluetoothAdapter.startDiscovery();
                bt_search.setText("stop search");
            }
        }
    }

    private final BroadcastReceiver  myReceiver = new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                myDeviceList.add(device.getName() + "\n" + device.getAddress());
                myDeviceAdapter.notifyDataSetChanged();
            }
        }
    };

    protected void onStart() {
        //注册蓝牙接收广播
        if(!hasregister){
            hasregister=true;
            IntentFilter filterStart=new IntentFilter(BluetoothDevice.ACTION_FOUND);
            IntentFilter filterEnd=new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(myReceiver, filterStart);
            registerReceiver(myReceiver, filterEnd);
        }
        super.onStart();
    }

    protected void onDestroy() {
        if(myBluetoothAdapter!=null&&myBluetoothAdapter.isDiscovering()){
            myBluetoothAdapter.cancelDiscovery();
        }
        if(hasregister){
            hasregister=false;
            unregisterReceiver(myReceiver);
        }
        super.onDestroy();
    }
}