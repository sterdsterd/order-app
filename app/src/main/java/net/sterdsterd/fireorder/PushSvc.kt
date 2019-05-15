package net.sterdsterd.fireorder

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationManager
import android.R
import android.app.Notification
import android.app.Notification.DEFAULT_SOUND
import android.app.Notification.DEFAULT_VIBRATE
import android.app.NotificationChannel
import android.os.Build
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.provider.SyncStateContract
import android.util.Log
import androidx.core.app.NotificationCompat


class PushSvc : FirebaseMessagingService() {
    private val TAG = "FirebaseService"

    /**
     * FirebaseInstanceIdService is deprecated.
     * this is new on firebase-messaging:17.1.0
     */
    override fun onNewToken(token: String?) {
        Log.d(TAG, "new Token: $token")
    }

    /**
     * this method will be triggered every time there is new FCM Message.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        val title = data.get("title")
        val body = data.get("body")

        if(remoteMessage.data != null) {
            sendNotification(title!!, body!!)
            Log.d("arst", body)
        }
    }

    private fun sendNotification(title: String, body: String) {

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("Notification", body)
        }

        val CHANNEL_ID = "orderNotification"
        val CHANNEL_NAME = "주문 현황"
        val description = "주문 현황을 알려드립니다."
        val importance = NotificationManager.IMPORTANCE_HIGH

        var notificationManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            channel.description = description
            channel.enableLights(true)
            channel.lightColor = Color.CYAN
            channel.enableVibration(true)
            channel.setShowBadge(true)
            notificationManager.createNotificationChannel(channel)
        }

        var pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        var notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.sym_def_app_icon))
            .setSmallIcon(R.mipmap.sym_def_app_icon)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(notificationSound)
            .setContentIntent(pendingIntent)

        notificationManager.notify(0, notificationBuilder.build())
    }

}