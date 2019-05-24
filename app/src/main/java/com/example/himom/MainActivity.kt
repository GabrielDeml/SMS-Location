package com.example.himom

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private var TAG: String = "From MainActivity"
    private var m_isStarted = false
    var m_context: Context = this
    private lateinit var smsListener: BroadcastReceiver
    val PERMISSIONS_NEEDED = arrayOf(Manifest.permission.READ_SMS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()
        val intent = Intent(applicationContext, BackGroundService::class.java)
        val sharedPref: SharedPreferences = getSharedPreferences("thing", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        //Set saved phone numbers from last session
        val phoneInput: EditText = findViewById(R.id.phoneNumberInput)
        phoneInput.setText(sharedPref.getString("savedPhoneInputText", ""))


        //If the stop button is pressed
        val stopButton: Button = findViewById(R.id.stop)
        stopButton.setOnClickListener(View.OnClickListener {
            //If things are started stop listening for sms and set started state to false
            Toast.makeText(m_context, "stopping", Toast.LENGTH_SHORT).show()
            println("Stopping the thing for you dude. Ok if you want a more descriptive message we are trying to stop the background service.")
//                val foregroundToKill : Intent = Intent(BackGroundService, BackGroundService::class.java)
            this.stopService(intent)
        })

        //Create and use the start button
        val startButton: Button = findViewById(R.id.start)
        startButton.setOnClickListener {
            //If we are already started stop
            this.stopService(intent)

            // Log that we were clicked
            Log.d(TAG, "onClick: Hey we were clicked")

            //Get permissions
            getPermissionsIfNotGranted(PERMISSIONS_NEEDED)

            // Get text from the phone input and save it
            val phoneInputText = phoneInput.text.toString()
            editor.putString("savedPhoneInputText", phoneInputText)
            editor.apply()

            // Tell the user that we are starting
            Toast.makeText(m_context, "starting", Toast.LENGTH_SHORT).show()

            // Init numberArray
            var numberArray: Array<String>? = null

            //Put things into the array
            when {
                phoneInputText.length > 10 -> numberArray = phoneInputText.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                phoneInputText.length == 10 -> numberArray = arrayOf(phoneInputText)
                else -> Toast.makeText(m_context, "Invalid phone number. Needs to be 11 digits including the country code AKA USA is 1", Toast.LENGTH_LONG).show()
            }
            // Make sure is isn't null
            if (numberArray != null) {
                //Clean the array
                numberArray = cleanArray(numberArray)
                println(Arrays.toString(numberArray))
                intent.putExtra("numberArray", numberArray)
//                m_smsListener = SmsListener(numberArray)
//                val filter = IntentFilter()
//                filter.addAction("android.provider.Telephony.SMS_RECEIVED")
//                registerReceiver(m_smsListener, filter)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent)
                    println("we are using oreo")
                } else startService(intent)
            } else Toast.makeText(m_context, "Please enter a phone number", Toast.LENGTH_SHORT).show()
//            Thread.sleep(4000)
//            stopService(intent)
        }
    }

    private fun cleanArray(numberArray: Array<String>): Array<String> {
        var cleanedArray: ArrayList<String> = ArrayList()
        for ((i, number) in numberArray.withIndex()) {
            println(i)
            if(number.equals("Gabriel" , ignoreCase = true)){
                Toast.makeText(m_context, "HI Gabriel", Toast.LENGTH_SHORT).show()
            }
            var numberClean = number.replace("[^0-9]".toRegex(), "")
            if (numberClean.length == 10) {
                numberClean = "1$numberClean"
                Toast.makeText(m_context, "Defaulting to country code for the USA, if that is not your country code you need to add it to the phone number", Toast.LENGTH_SHORT).show()
            }
            cleanedArray.add(numberClean)
        }
        return cleanedArray.toTypedArray()
    }

    private fun getPermissionsIfNotGranted(permissions: Array<String>) {
        if (checkIfAnyoneIsNotGranted(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, 1)
        }
    }

    private fun checkIfAnyoneIsNotGranted(permissions: Array<String>): Boolean {
        for (permission in permissions)
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return true
            }
        return false
    }
}
