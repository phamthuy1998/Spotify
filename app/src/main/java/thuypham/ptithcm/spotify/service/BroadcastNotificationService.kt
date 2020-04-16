package thuypham.ptithcm.spotify.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import thuypham.ptithcm.spotify.util.NOTIFICATION
import thuypham.ptithcm.spotify.util.NOTIFICATION_ACT

class BroadcastNotificationService : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.sendBroadcast(Intent(NOTIFICATION).putExtra(NOTIFICATION_ACT, intent?.action))
    }
}