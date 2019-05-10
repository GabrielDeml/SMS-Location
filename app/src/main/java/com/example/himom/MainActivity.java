package com.example.himom;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String[] PERMISSIONS_NEEDED = {Manifest.permission.READ_SMS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS};
    String TAG = "From MainActivity";
    boolean m_isStarted = false;
    Context m_context;
    EditText m_phoneInput;
    String[] m_numberArray;
    BroadcastReceiver smsListener;
    SharedPreferences m_sharedPref;
    SharedPreferences.Editor m_editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Save away the context so we can use it later
        getSupportActionBar().hide();
        m_context = this;

        //Set up the saving parameters
        m_sharedPref = getSharedPreferences("thing", Context.MODE_PRIVATE);
        m_editor = m_sharedPref.edit();

        //Set saved phone numbers from last session
        m_phoneInput = findViewById(R.id.phoneNumberInput);
        m_phoneInput.setText(m_sharedPref.getString("savedPhoneInputText", ""));

        //If the stop button is pressed
        Button stopButton = findViewById(R.id.stop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //If things are started stop listening for sms and set started state to false
                if (m_isStarted) {
                    unregisterReceiver(smsListener);
                    m_isStarted = false;
                }
            }
        });

        //Create and use the start button
        Button startButton = findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //If we are already started stop
                if (m_isStarted) {
                    unregisterReceiver(smsListener);
                }

                // Log that we were clicked
                Log.d(TAG, "onClick: Hey we were clicked");

                //Get permissions
                getPermissionsIfNotGranted(PERMISSIONS_NEEDED);

                // Get text from the phone input and save it
                String phoneInputText = m_phoneInput.getText().toString();
                m_editor.putString("savedPhoneInputText", phoneInputText);
                m_editor.apply();

                // Tell the user that we are starting
                Toast.makeText(m_context, "starting", Toast.LENGTH_SHORT).show();


                if (phoneInputText.length() > 10) {
                    m_numberArray = phoneInputText.split(",");
                } else if (phoneInputText.length() == 10) {
                    m_numberArray = new String[]{phoneInputText};
                } else {
                    Toast.makeText(m_context, "Invalid phone number. Needs to be 11 digits including the country code AKA USA is 1", Toast.LENGTH_LONG).show();
                }
                m_numberArray = cleanArray(m_numberArray);
                smsListener = new SmsListener(m_numberArray);
                IntentFilter filter = new IntentFilter();
                filter.addAction("android.provider.Telephony.SMS_RECEIVED");
                registerReceiver(smsListener, filter);
                m_isStarted = true;
            }
        });
    }

    public String[] cleanArray(String[] numberArray) {
        int i = 0;
        String[] cleanedArray = {"-1"};
        for (String number : numberArray) {
            cleanedArray[i] = number.replaceAll("[^0-9]", "");
            if (cleanedArray[i].length() == 10){
                cleanedArray[i] = ("1" + cleanedArray[i]);
                Toast.makeText(m_context, "Defaulting to country code for the USA, if that is not your country code you need to add it to the phone number", Toast.LENGTH_LONG).show();
            }
        }
        return cleanedArray;
    }

    public void getPermissionsIfNotGranted(String[] permissions) {
        if (checkIfAnyoneIsNotGranted(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

    public boolean checkIfAnyoneIsNotGranted(String[] permissions) {
        for (String permission : permissions)
            if (!(ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED)) {
                return true;
            }
        return false;
    }
}
