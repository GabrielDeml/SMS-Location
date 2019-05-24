package com.example.himom

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.SmsManager
import android.widget.Toast
import java.util.*


class SmsListener(internal var phoneNumbersToCheck: Array<String>) : BroadcastReceiver() {

    lateinit var m_context: Context
    lateinit var m_locationListener: LocationListener
    lateinit var m_locationManager: LocationManager
    lateinit var m_smsMessage: String
    lateinit var m_Number: String
    val CHANNEL_ID: String = "com.gabriel.ANDROID"
    var notificationi = 0

    override fun onReceive(context: Context, intent: Intent) {
        //Save away the context
        m_context = context
        // Acquire a reference to the system Location Manager
        for (sms in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
            //Get the sms message
            m_smsMessage = sms.messageBody
            //Get the number that it came from and clean it up
            m_Number = sms.originatingAddress!!.replace("[^0-9]".toRegex(), "")
//                        Toast.makeText(context, "From  |" + m_Number + "|     " + m_smsMessage, Toast.LENGTH_SHORT).show()
        }
        //Check to see if the message is right
        if (m_smsMessage.equals("location", ignoreCase = true)) {
//            Toast.makeText(m_context, "We like location", Toast.LENGTH_SHORT).show()
            //Check to see if the message is from a approved number
            println(Arrays.toString(phoneNumbersToCheck))
            if (checkIfCorrectNumber(m_Number, phoneNumbersToCheck)) {
//                Toast.makeText(m_context, "We like it", Toast.LENGTH_SHORT).show()
                //Get the last notification ID and increment it and save it away
                val sharedPref: SharedPreferences = m_context.getSharedPreferences("thing", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPref.edit()
                notificationi = sharedPref.getInt("savedI", 0)
                notificationi++
                editor.putInt("savedI", notificationi)
                editor.apply()
                println(notificationi)

                //Set up the warning toast
//                var inflater  = m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
//                val inflater  = context.getSystemService( Context.LAYOUT_INFLATER_SERVICE )
//                var toastView = inflat

                //Tell the user that we are sending their location
//                Toast.makeText(context, "Location requested form: $m_Number", Toast.LENGTH_LONG).show()

                // Create the location manager
                m_locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                //Set up the listener
                m_locationListener = object : LocationListener {
                    @SuppressLint("MissingPermission")
                    override fun onLocationChanged(location: Location) {
                        //Sent the text with location
                        sendText(location.latitude, location.longitude, m_Number)
                        //Create and show the notification
                        showNotification(initNotification(m_Number, location.latitude, location.longitude))
                        //Stop getting updates
                        m_locationManager.removeUpdates(m_locationListener)
                    }

                    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

                    override fun onProviderEnabled(provider: String) {}

                    override fun onProviderDisabled(provider: String) {}
                }
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //If we don't have access to the location tell the user
                    Toast.makeText(context, "Number $m_Number requested location, but we don't have permission to read location", Toast.LENGTH_LONG).show()
                } else {
                    //Start getting the location
//                    Toast.makeText(m_context, "trying to get updates", Toast.LENGTH_LONG).show()
                    m_locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, m_locationListener)
                }
            }

        }
    }


    fun initNotification(number: String, latitude: Double, longitude: Double): NotificationCompat.Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            println("We are coming from oreo land. What's up?")
            println("We made it into the if function")
            // Create the NotificationChannel
            val name = "requested location"
            val descriptionText = "Location requested"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = m_context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)


        }
        var builder = NotificationCompat.Builder(m_context, CHANNEL_ID)
                .setContentTitle("Location Requested")
                .setContentText("Your location was requested by $number")
                .setSmallIcon(R.drawable.locationicon)
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText("Your location was requested by $number and this location was sent: https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
        return builder
    }

    fun showNotification(builder: NotificationCompat.Builder) {
        with(NotificationManagerCompat.from(m_context)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationi, builder.build())
        }
    }


    fun checkIfCorrectNumber(number: String, correctNumbers: Array<String>): Boolean {
        for (phoneNumberToCheck in correctNumbers) {
            if (number.equals(phoneNumberToCheck, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    fun sendText(latitude: Double, longitude: Double, number: String) {
        //Set up smsManager
        val smsManager = SmsManager.getDefault()
        //Generate the text that we want to sent. This url will bring the user to a pin in google maps
        val textToSend = "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
//    val textToSend = "https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude"
        //Send the text
        smsManager.sendTextMessage(number, null, textToSend, null, null)
    }
}