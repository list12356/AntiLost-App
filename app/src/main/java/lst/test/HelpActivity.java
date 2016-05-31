package lst.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        TextView text=(TextView)findViewById(R.id.helptext);
        text.setMovementMethod(ScrollingMovementMethod.getInstance());
        text.setText("Keep your bluetooth on in your phone and switch on the raspberry Pi." +
                "Once you finished, open the app, tap the menu in the right-up corner and select the `add item' option, the page will jump to another activity." +
                " Press the scan button and youcan get all the bluetooth devices avaliable, select one you want to keep listening and give it a name(blank for its default name)." +
                " Then you can get the device shown on your main item list. If you no longer want to keep the device, you can select the `delete item` in the menu and choose the device in the delete phrase." +
                "If you want to find your Item, select it and the program will try to connect it ,if success, you can get the RSSI of the device and a speculated distance according to the RSSI." +
                " Choose the `Call my item' button, the program will send a message to the Raspeberry Pi to make it beep. " +
                "If it fails, you can see the information about the last time and location that the device was scanned");
    }
    public void onBackPressed() {
        Intent in=new Intent(HelpActivity.this,MainActivity.class);
        startActivity(in);
        finish();
    }
}
