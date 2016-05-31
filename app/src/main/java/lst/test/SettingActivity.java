package lst.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

public class SettingActivity extends AppCompatActivity {

    Switch open;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        open=(Switch) findViewById(R.id.distance_alarm);
        open.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    MainActivity.distance_alert=true;
                } else {
                    MainActivity.distance_alert=false;
                }
            }
        });
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                if(pos==0)
                    MainActivity.min_rssi=110;
                if(pos==1)
                    MainActivity.min_rssi=90;
                if(pos==2)
                    MainActivity.min_rssi=70;
            }
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

    }

    public void onBackPressed() {
        Intent in=new Intent(SettingActivity.this,MainActivity.class);
        startActivity(in);
        finish();
    }
}
