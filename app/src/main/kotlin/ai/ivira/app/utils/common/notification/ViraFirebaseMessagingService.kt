package ai.ivira.app.utils.common.notification

import ai.ivira.app.BuildConfig
import ai.ivira.app.R
import ai.ivira.app.utils.common.orZero
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class ViraFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        val data = message.data
        if (data.isNotEmpty()) {
            val title = data["title"] ?: ""
            val body = data["body"] ?: ""
            val type = data["type"]
            val version = data["versionNumber"]?.toLong().orZero()
            when (type) {
                FireBaseNotificationType.NewVersion.value -> {
                    if (version > BuildConfig.VERSION_CODE) {
                        showNewVersionNotification(title, body)
                    }
                }
            }
        }
    }

    private fun showNewVersionNotification(title: String, messageBody: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.SHARE_URL))
        val pendingIntent = PendingIntent.getActivity(
            this,
            OPEN_MARKET_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder =
            NotificationCompat.Builder(this, NEW_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_vira)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NEW_NOTIFICATION_CHANNEL_ID,
                NEW_NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(
            NotificationId,
            notificationBuilder.build()
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.tag(TAG).d(token)
    }

    companion object {
        const val TAG = "ViraFirebaseMessaging"
        private const val NEW_NOTIFICATION_CHANNEL_ID = "vira"
        private const val NotificationId = 0
        private const val OPEN_MARKET_REQUEST_CODE = 0
        private const val NEW_NOTIFICATION_CHANNEL_NAME = "viraNewVersion"
    }
}