package com.example.himom;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class BackgroundService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Create an IntentFilter instance.
        IntentFilter intentFilter = new IntentFilter();

        // Add network connectivity change action.
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");

        // Set broadcast receiver priority.
        intentFilter.setPriority(100);

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
//        registerReceiver(smsListener, filter);

//        // Create a network change broadcast receiver.
//        screenOnOffReceiver = new ScreenOnOffReceiver();
//
//        // Register the broadcast receiver with the intent filter object.
//        registerReceiver(screenOnOffReceiver, intentFilter);
//
//        Log.d(ScreenOnOffReceiver.SCREEN_TOGGLE_TAG, "Service onCreate: screenOnOffReceiver is registered.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}
