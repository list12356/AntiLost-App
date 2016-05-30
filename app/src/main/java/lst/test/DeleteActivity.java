package lst.test;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DeleteActivity extends AppCompatActivity {

    ListView mItemlList;
    ArrayAdapter myArrayAdapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        setView();
    }
    private void setView()
    {
        mItemlList=(ListView) findViewById(R.id.DelList);
        myArrayAdapter=new ArrayAdapter(this,R.layout.simple_list_item_1,MainActivity.myItemList.Show);
        mItemlList.setAdapter(myArrayAdapter);
        mItemlList.setOnItemClickListener(new DelClick());
    }

    public void onBackPressed() {
        Intent in=new Intent(DeleteActivity.this,MainActivity.class);
        startActivity(in);
        finish();
    }

    public class DelClick implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View arg1, final int pos, long arg3)
        {
            final String msg = MainActivity.myItemList.Data.get(pos).Name;
            AlertDialog.Builder dialog = new AlertDialog.Builder(DeleteActivity.this);// 定义一个弹出框对象
            dialog.setTitle("Are you sure to delete this device?");
            dialog.setMessage(msg);
            dialog.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.myItemList.Delete(pos);
                            Intent in=new Intent(DeleteActivity.this,MainActivity.class);
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
}
