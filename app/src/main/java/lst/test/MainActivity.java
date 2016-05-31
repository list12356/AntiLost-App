package lst.test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Timer;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.BDNotifyListener;//假如用到位置提醒功能，需要import该类
import com.baidu.location.Poi;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static ItemList myItemList;
    private boolean hasregister = false;
    private final BroadcastReceiver myReceiver = new myBroadcastReceiver();
    public final static String EXTRA_MESSAGE = "com.example.myapp.MESSAGE";

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    static boolean distance_alert=true;
    ListView MainList;
    ArrayAdapter myArrayAdapter;
    BluetoothAdapter myBluetoothAdapter;
    private ClientThread clientConnectThread = null;
    Timer timer;
    TextView status_text;
    int locked=0;
    Calendar calendar=Calendar.getInstance();
    BluetoothDevice device=null;
    BluetoothSocket socket=null;
    String mylocation=null;
    double longitude=0;
    double latitude=0;

    public static int min_rssi=70;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数

        setView();
        initLocation();
        init_connection();

        mLocationClient.start();
        myBluetoothAdapter.startDiscovery();
    }
    protected void onDestroy() {
        if(myBluetoothAdapter!=null&&myBluetoothAdapter.isDiscovering()){
            myBluetoothAdapter.cancelDiscovery();
        }
        if(hasregister){
            hasregister=false;
            unregisterReceiver(myReceiver);
        }
        timer.cancel();
        super.onDestroy();
    }

    void init_connection()
    {
        myItemList=new ItemList();
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //注册蓝牙接收广播
        if(!hasregister)
        {
            hasregister = true;
            IntentFilter filterStart = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            IntentFilter filterEnd = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(myReceiver, filterStart);
            registerReceiver(myReceiver, filterEnd);
        }
        locked=0;
        myBluetoothAdapter.startDiscovery();
        timer=new Timer();
        timer.schedule(new myTimer(),0,3000);
    }
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }
    void setView()
    {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        SimpleAdapter myAdapter = new SimpleAdapter(this,getData(),R.layout.list_item_1,
//
//                new String[]{"name","status"},
//                new int[]{android.R.id.text1,android.R.id.text2});

        MainList=(ListView) findViewById(R.id.ItemList);
        myItemList= new ItemList();
        myArrayAdapter=new ArrayAdapter(this,R.layout.simple_list_item_1,myItemList.Show);
        MainList.setAdapter(myArrayAdapter);
        MainList.setOnItemClickListener(new ListClickMonitor());
        status_text=(TextView) findViewById(R.id.status_text);
    }
    public void onResume() {
        locked=0;
        super.onResume();
        myArrayAdapter.notifyDataSetChanged();
        if(timer==null)
            timer=new Timer();
        timer.schedule(new myTimer(),0,3000);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add) {
            myBluetoothAdapter.cancelDiscovery();
            locked=1;
            Intent jump=new Intent(MainActivity.this, AddItemActivity.class);
            startActivity(jump);
            finish();
            return true;
        }
        if (id == R.id.del) {
            myBluetoothAdapter.cancelDiscovery();
            locked=1;
            Intent jump=new Intent(MainActivity.this, DeleteActivity.class);
            startActivity(jump);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            Intent jump=new Intent(MainActivity.this, SettingActivity.class);
            startActivity(jump);
            finish();

        } else if (id == R.id.nav_share) {
            Intent jump=new Intent(MainActivity.this, HelpActivity.class);
            startActivity(jump);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //点击设备时
    public class ListClickMonitor implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View arg1,int pos_t, long arg3)
        {
            final int pos = pos_t;
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            //开启连接
            if(locked==0) {
                locked = 1;
                clientConnectThread = new ClientThread();
                clientConnectThread.start();
                clientConnectThread.run(pos);
                locked = 0;
            }
            //若该物品在线
            if (socket.isConnected()) {
                dialog.setTitle("Device:" + myItemList.Data.get(pos).Name);
                //显示信号强度
                DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                double dist=Math.pow(10.0,((Math.abs(myItemList.Data.get(pos).rssi)-60.0)/20.0));
                String p=decimalFormat.format(dist);//format 返回的是字符串
                dialog.setMessage("RSSI:"+myItemList.Data.get(pos).rssi+"\n"+"speculated distance:"+p+"m");
                dialog.setPositiveButton("Call my item",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                sendMessageHandle(socket);
                                shutdownClient(pos);
                            }
                        });
                dialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                shutdownClient(pos);
                            }
                        });
            } else
            //若不在线
            {
                try {
                    Method m = device.getClass().getMethod("removeBond", (Class[]) null);
                    m.invoke(device);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.setTitle("Device:" + myItemList.Data.get(pos).Name+"Address:"+myItemList.Data.get(pos).Address);
                dialog.setMessage("Last Scan time:"+myItemList.Data.get(pos).last_time+"\nLast Location:"+myItemList.Data.get(pos).last_location);
                dialog.setPositiveButton("Retry Connection",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (socket != null) {
                                    try {
                                        socket.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    socket = null;
                                }
                            }
                        });
                dialog.setNeutralButton("View Map", new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface arg0, int arg1) {
                         Intent jump=new Intent(MainActivity.this, MapActivity.class);
                         jump.putExtra(EXTRA_MESSAGE, String.valueOf(pos));
                         startActivity(jump);
                         finish();
                        }
                });

                dialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                shutdownClient(pos);
                            }
                        });
            }
            dialog.show();
            locked=0;
        }
    }
    //开启客户端
    private class ClientThread extends Thread {
        public void run(int pos) {
            myBluetoothAdapter.cancelDiscovery();
            device = myBluetoothAdapter.getRemoteDevice(myItemList.Data.get(pos).Address);
            try {
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    //利用反射方法调用BluetoothDevice.createBond(BluetoothDevice remoteDevice);
                    Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                    createBondMethod.invoke(device);
                    //Log.d("BlueToothTestActivity", "开始配对");
                  }

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                //创建一个Socket连接：只需要服务器在注册时的UUID号
                socket =  device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                socket.connect();
                //
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            myBluetoothAdapter.startDiscovery();
        }
    };
    /* 停止客户端连接 */
    private void shutdownClient(final int pos) {
        new Thread() {
            @Override
            public void run() {
                myBluetoothAdapter.cancelDiscovery();
                if(clientConnectThread!=null)
                {
                    clientConnectThread.interrupt();
                    clientConnectThread=null;
                }
                if (socket!= null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    socket= null;
                }
                myBluetoothAdapter.startDiscovery();
            }
        }.start();
    }
    //发送数据
    private void sendMessageHandle(BluetoothSocket socket)
    {
        if (socket == null)
        {
            return;
        }
        try {
            OutputStream os = socket.getOutputStream();
            os.write(getHexBytes("aaaaa"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //输出信号转16进制
    private byte[] getHexBytes(String message) {
        int len = message.length() / 2;
        char[] chars = message.toCharArray();
        String[] hexStr = new String[len];
        byte[] bytes = new byte[len];
        for (int i = 0, j = 0; j < len; i += 2, j++) {
            hexStr[j] = "" + chars[i] + chars[i + 1];
            bytes[j] = (byte) Integer.parseInt(hexStr[j], 16);
        }
        return bytes;
    }
    class myTimer extends TimerTask {
        public void run() {
                if(myBluetoothAdapter.isDiscovering()) {
                    myBluetoothAdapter.cancelDiscovery();
                }
                myBluetoothAdapter.startDiscovery();
        }
    }
    class myBroadcastReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                for(int i=0;i<myItemList.Data.size();i++)
                {
                    if(device.getAddress().equals(myItemList.Data.get(i).Address))
                    {
                        myItemList.Data.get(i).rssi=intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                        myItemList.Data.get(i).last_time=calendar.get(Calendar.YEAR)+"/"+calendar.get(Calendar.MONTH)+"/"
                                +calendar.get(Calendar.DATE)+" "+calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE)
                                +":"+calendar.get(Calendar.SECOND);
                        status_text.setText("Device:"+ myItemList.Data.get(i).Name+" RSSI:"+myItemList.Data.get(i).rssi);
                        if(mylocation!=null) {
                            myItemList.Data.get(i).last_location=mylocation;
                            myItemList.Data.get(i).latitude=latitude;
                            myItemList.Data.get(i).longitude=longitude;
                        }
                        if (Math.abs(myItemList.Data.get(i).rssi) > min_rssi&&locked==0&&distance_alert==true) {
                            locked=1;
                            clientConnectThread = new ClientThread();
                            clientConnectThread.start();
                            clientConnectThread.run(i);
                            if(socket.isConnected())
                            {
                                sendMessageHandle(socket);
                                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                vibrator.vibrate(5000);
                            }
                            shutdownClient(i);
                            locked=0;
                        }
                    }
                }
            }
        }
    }
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("latitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\ntype : ");
                sb.append("GPS");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\ntype : ");
                sb.append("Network Location");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ntype : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            mylocation=sb.toString();
            latitude=location.getLatitude();
            longitude=location.getLongitude();
        }
    }
}
