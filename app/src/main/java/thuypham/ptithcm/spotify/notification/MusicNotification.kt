package thuypham.ptithcm.spotify.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.app.NotificationCompat.MediaStyle
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.service.BroadcastNotificationService
import thuypham.ptithcm.spotify.util.*


class MusicNotification {
    private lateinit var notification: Notification

    //    private fun getBitmapFromURL(src: String?): Bitmap? {
//        return try {
//            val url = URL(src)
//            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
//            connection.doInput = true
//            connection.connect()
//            val input: InputStream = connection.getInputStream()
//            BitmapFactory.decodeStream(input)
//        } catch (e: IOException) { // Log exception
//            Log.d("ErrorGetBitmap", e.message.toString())
//            null
//        }
//    }


    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    fun createNotification(context: Context, song: Song, playButton: Int, pos: Int, size: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManagerCompat =
                NotificationManagerCompat.from(context)
            val mediaSessionCompat = MediaSessionCompat(context, "tag")

            // Get image url of image song
//            val icon = getBitmapFromURL(song.imageURL)
            val pendingIntentPrevious: PendingIntent?
            val icPrev: Int
            if (pos == 0) {
                pendingIntentPrevious = null
                icPrev = 0
            } else {
                val intentPrevious =
                    Intent(context, BroadcastNotificationService::class.java).setAction(ACT_PREV)
                pendingIntentPrevious = PendingIntent.getBroadcast(
                    context, 0,
                    intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT
                )
                icPrev = R.drawable.ic_prev
            }
            val intentPlay = Intent(context, BroadcastNotificationService::class.java)
                .setAction(ACT_PLAY)
            val pendingIntentPlay = PendingIntent.getBroadcast(
                context, 0,
                intentPlay, PendingIntent.FLAG_UPDATE_CURRENT
            )

            // Button next
            val pendingIntentNext: PendingIntent?
            val icNext: Int
            if (pos == size) {
                pendingIntentNext = null
                icNext = 0
            } else {
                val intentNext = Intent(context, BroadcastNotificationService::class.java)
                    .setAction(ACT_NEXT)
                pendingIntentNext = PendingIntent.getBroadcast(
                    context, 0,
                    intentNext, PendingIntent.FLAG_UPDATE_CURRENT
                )
                icNext = R.drawable.ic_next1
            }

            // Button next
            val pendingIntentCancel: PendingIntent?
            val icCancel: Int
            if (pos == size) {
                pendingIntentCancel = null
                icCancel = 0
            } else {
                val intentNext = Intent(context, BroadcastNotificationService::class.java)
                    .setAction(ACT_EXIT)
                pendingIntentCancel = PendingIntent.getBroadcast(
                    context, 0,
                    intentNext, PendingIntent.FLAG_UPDATE_CURRENT
                )
                icCancel = R.drawable.ic_cancel
            }

            //create notification
            notification = NotificationCompat.Builder(context, CHANEL_ID)
                .setSmallIcon(R.drawable.ic_music)
                .setContentTitle(song.songName)
                .setContentText(song.artistName)
                .setLargeIcon(null)
                .setOnlyAlertOnce(true) //show notification for only first time
                .setShowWhen(false)
                .addAction(icPrev, "Previous", pendingIntentPrevious)
                .addAction(playButton, "Play", pendingIntentPlay)
                .addAction(icNext, "Next", pendingIntentNext)
                .addAction(icCancel, "Cancel", pendingIntentCancel)
                .setStyle(
                    MediaStyle().setShowActionsInCompactView(0, 1, 2, 3)
                        .setMediaSession(mediaSessionCompat.sessionToken)
                )
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()
            notificationManagerCompat.notify(1, notification)
        }
    }
}