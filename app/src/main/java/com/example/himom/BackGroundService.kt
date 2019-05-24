package com.example.himom

import android.app.PendingIntent
import android.app.Service
import android.app.TaskStackBuilder
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat

class BackGroundService : Service() {
    fun unregisterSNSListener() {
        println("Unregistering")
        unregisterReceiver(m_smsListener)
    }

    lateinit var m_smsListener: SmsListener

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        // Create an Intent for the activity you want to start
        val resultIntent = Intent(this, MainActivity::class.java)
        // Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val builder = NotificationCompat.Builder(this, "com.gabriel.ANDROID")
                    .setContentTitle("SMS location Running")
                    .setContentText("SMS location is running in the background")
                    .setSmallIcon(R.drawable.locationicon)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setContentIntent(resultPendingIntent)

            val notification = builder.build()
            startForeground(1, notification)

        } else {

            val builder = NotificationCompat.Builder(this)
                    .setContentTitle("I am running in the background")
                    .setContentText("I am running in the background")
                    .setPriority(NotificationCompat.PRIORITY_LOW)

            val notification = builder.build()

            startForeground(1, notification)
        }
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val numberArray = intent!!.getStringArrayExtra("numberArray")
        m_smsListener = SmsListener(numberArray)
        val filter = IntentFilter()
        filter.addAction("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(m_smsListener, filter)

        super.onStartCommand(intent, flags, startId)
        return Service.START_NOT_STICKY
    }

//    override fun onDestroy() {
////        super.onDestroy()
//    }
    override fun onDestroy() {
        println("Hey we are dyeing")
        unregisterReceiver(m_smsListener)
        this.stopForeground(true)
        stopSelf()
        super.onDestroy()
    }
//    fun unregisterSNSListener() {
//        println("Unregistering")
//        unregisterReceiver(m_smsListener)
//    }
//    companion object {
//        fun unregisterReceiverCompanion() {
//            BackGroundService.unregisterReceiver()
//        }
//    }

}
