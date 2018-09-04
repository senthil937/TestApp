package arasan.com.myapplication1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ChildActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);
        txtBattery = (TextView) findViewById(R.id.txtBattery);

        txtLatLan = (TextView) findViewById(R.id.txtLATLAN);

        btnBack = (Button) findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChildActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

//    @Subscribe
//    public void onEvent(BatteryLevelEvent event)
//    {
//        txtBattery.append("Battery = " + event.getBatteryLevel() + "%");
//    }
//    @Subscribe
//    public  void onEvent(LocationLevelEvent event)
//    {
//        txtLatLan.append("LT = " + event.getLAT()+ "\n");
//        txtLatLan.append("LN = " + event.getLAN()+ "\n");
//    }
}
