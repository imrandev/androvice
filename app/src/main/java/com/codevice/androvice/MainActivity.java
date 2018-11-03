package com.codevice.androvice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codevice.androvice.config.Config;
import com.codevice.androvice.service.ServiceTime;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.btn_service);

        startButton.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener
            = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startService();
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

    public void startService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, ServiceTime.class));
        } else {
            startService(new Intent(this, ServiceTime.class));
        }
    }

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
