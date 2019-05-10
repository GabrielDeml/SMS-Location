package com.example.himom;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;


public class SmsListener extends BroadcastReceiver {
    String m_smsMessage;
    String m_Number;
    LocationManager m_locationManager;
    LocationListener m_locationListener;
    Context m_context;
    String[] m_phoneNumbersToCheck;

    public SmsListener(String[] phoneNumbersToCheck) {
        m_phoneNumbersToCheck = phoneNumbersToCheck;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        m_context = context;
//        Toast.makeText(context, "We got an sms", Toast.LENGTH_LONG).show();
        // Acquire a reference to the system Location Manager
        for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
            m_smsMessage = smsMessage.getMessageBody();
            m_Number = smsMessage.getOriginatingAddress().replaceAll("[^0-9]", "");
//            Toast.makeText(context, "From  |" + m_Number + "|     " + m_smsMessage, Toast.LENGTH_LONG).show();
        }
        if (m_smsMessage.equalsIgnoreCase("location")) {
            if (checkIfCorrectNumber()){
                Toast.makeText(context, "Location requested form: " + m_Number, Toast.LENGTH_LONG).show();
                // Create the location manager
                m_locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                //Set up the listener
                m_locationListener = setUpLocationListener();
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //If we don't have access to the location tell the user
                    Toast.makeText(context, "Number " + m_Number + "requested location, but we don't have permission to read location", Toast.LENGTH_LONG).show();
                } else {
                    //Start getting the location
                    m_locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, m_locationListener);
                }
            }
        }


    }

    public boolean checkIfCorrectNumber() {
        for (String phoneNumberToCheck : m_phoneNumbersToCheck) {
            if (m_Number.equalsIgnoreCase(phoneNumberToCheck)) {
                return true;
            }
        }
        return false;
    }

    public void sendText(double latitude, double longitude) {
        //Set up smsManager
        SmsManager smsManager = SmsManager.getDefault();
        //Generate the text that we want to sent. This url will bring the user to a pin in google maps
        String textToSend = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
        //Send the text
        smsManager.sendTextMessage(m_Number, null, textToSend, null, null);
    }

    public LocationListener setUpLocationListener() {
        //Set up the location listener
        return new LocationListener() {
            @SuppressLint("MissingPermission")
            public void onLocationChanged(Location location) {
                //Sent the text with location
                sendText(location.getLatitude(), location.getLongitude());
                //Stop getting the location
                m_locationManager.removeUpdates(m_locationListener);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
    }
}
