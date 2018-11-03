package com.codevice.androvice.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;

import com.codevice.androvice.config.Config;

import static com.codevice.androvice.config.Config.DELAY_TIME;
import static com.codevice.androvice.config.Config.MSG_UPDATE;

public class ServiceHandler extends Handler {

    private Context context;

    public ServiceHandler(Looper looper, Context context) {
        super(looper);
        this.context = context;
    }

    // Define how to handle any incoming messages here
    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case MSG_UPDATE: {
                Intent intent = new Intent(Config.ACTION_NAME);
                intent.putExtra("result", "Hello Service");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                Message nmsg = obtainMessage(MSG_UPDATE);
                sendMessageDelayed(nmsg, DELAY_TIME);
            } break;
            default:
                super.handleMessage(message);
        }
    }
}
