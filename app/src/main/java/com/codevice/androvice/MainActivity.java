package com.codevice.androvice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codevice.androvice.config.Config;
import com.codevice.androvice.receiver.AndroviceReceiver;
import com.codevice.androvice.service.IntentServiceTime;
import com.codevice.androvice.service.ServiceTime;

public class MainActivity extends AppCompatActivity {

    private AndroviceReceiver androviceReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button serviceButton = findViewById(R.id.btn_service);
        Button intentServiceButton = findViewById(R.id.btn_intent_service);

        serviceButton.setOnClickListener(onClickListener);
        intentServiceButton.setOnClickListener(onClickListener);

        setUpReceiver();
    }

    private View.OnClickListener onClickListener
            = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_service:
                    onStartService();
                    break;
                case R.id.btn_intent_service:
                    onStartIntentService();
                    break;
            }
        }
    };

    @Override
    protected void onPause() {
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        // Register to receive messages.
        // We are registering an observer (broadcastReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Config.ACTION_NAME));
        super.onResume();
    }

    public void onStartService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, ServiceTime.class));
        } else {
            startService(new Intent(this, ServiceTime.class));
        }
    }

    // Starts the IntentService
    public void onStartIntentService() {
        Intent i = new Intent(this, IntentServiceTime.class);
        i.putExtra("foo", "Hello Intent Service");
        i.putExtra("receiver", androviceReceiver);
        startService(i);
    }

    public void setUpReceiver(){
        androviceReceiver = new AndroviceReceiver(new Handler());
        // This is where we specify what happens when data is received from the service
        androviceReceiver.setReceiver(receiver);
    }

    private AndroviceReceiver.Receiver receiver =  new AndroviceReceiver.Receiver() {
        @Override
        public void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == RESULT_OK) {
                String resultValue = resultData.getString("resultValue");
                Toast.makeText(MainActivity.this, resultValue, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null){
                if (action.equals(Config.ACTION_NAME)){
                    String result = intent.getStringExtra("result");
                    Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                    Log.e("BroadcastReceiver", result);
                } else {
                    Log.e("BroadcastReceiver", "ACTION not found");
                }
            } else {
                Log.e("BroadcastReceiver", "Action Error");
            }
        }
    };
}
