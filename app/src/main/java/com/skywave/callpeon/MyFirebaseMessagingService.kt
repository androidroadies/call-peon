package com.skywave.callpeon

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val ADMIN_CHANNEL_ID = "admin_channel"
    private val TAG = "MyFirebaseMessagingService"
    private val SUBSCRIBE_TO = "userABC"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val intent = Intent(this, MainActivity::class.java)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID: Int = Random().nextInt(3000)
        Log.i(TAG, "remoteMessage============>>>>>>: ${remoteMessage.data}")

        /*
        Apps targeting SDK 26 or above (Android O) must implement notification channels and add its notifications
        to at least one of them. Therefore, confirm if version is Oreo or higher, then setup notification channel
      */if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels(notificationManager)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0, intent,
//            PendingIntent.FLAG_ONE_SHOT
//        )
        var pendingIntent: PendingIntent? = null
        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_MUTABLE)
        }
        val largeIcon = BitmapFactory.decodeResource(
            resources,
            R.drawable.app_icon
        )

//        val notificationSoundUri: Uri =
//            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + R.raw.sound)
//            Settings.System.DEFAULT_RINGTONE_URI
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.app_icon)
                .setLargeIcon(largeIcon)
                .setContentTitle(remoteMessage.data["title"])
                .setContentText(remoteMessage.data["message"])
                .setAutoCancel(true)
//                .setSound(notificationSoundUri, AudioManager.STREAM_ALARM)
                .setContentIntent(pendingIntent)
        //Set notification color to match your app color template
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.color = resources.getColor(R.color.black)
        }
        notificationManager.notify(notificationID, notificationBuilder.build())
        if (remoteMessage.data["title"]!!.isNotEmpty()) {
            var mediaPlayer: MediaPlayer? = MediaPlayer.create(this, R.raw.sound)
            if (!mediaPlayer?.isPlaying!!) {
                mediaPlayer?.start()
            } else {
//                mediaPlayer.stop()
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels(notificationManager: NotificationManager?) {
        val adminChannelName: CharSequence = "New notification"
        val adminChannelDescription = "Device to device notification"
//        val sound =
//            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/raw/sound.mp3")
//        val attributes = AudioAttributes.Builder()
//            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//            .build()
        val adminChannel = NotificationChannel(
            ADMIN_CHANNEL_ID,
            adminChannelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
//        adminChannel.setSound(sound, attributes)
        notificationManager?.createNotificationChannel(adminChannel)
    }

    override fun onNewToken(p0: String) {
        // Once the token is generated, subscribe to topic with the userId
        FirebaseMessaging.getInstance().subscribeToTopic(SUBSCRIBE_TO)
        Log.i(TAG, "onTokenRefresh completed with token============>>>>>>   : $p0")
    }

}