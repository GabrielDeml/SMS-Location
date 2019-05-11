package com.example.himom;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

public class BackgroundService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public String[] m_numberArray;
    public SmsListener m_smsListener;
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

        m_numberArray = new String[]{"16038587719"};
        m_smsListener = new SmsListener(m_numberArray);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(m_smsListener, filter);
        Log.d(TAG, "onCreate: Hello from the background");

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
        unregisterReceiver(m_smsListener);
    }

}
