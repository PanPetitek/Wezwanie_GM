
package com.example.wezwanie_gm.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.example.wezwanie_gm.MainActivity
import com.example.wezwanie_gm.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONArray



object NotificationUtils {

    private const val CHANNEL_ID = "wezwanie_channel_id"
    private const val CHANNEL_NAME = "Wezwanie Alerty"
    private const val NOTIFICATION_ID = 1

    fun showAlarmNotification(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val uri = Uri.parse("android.resource://${context.packageName}/raw/notification_sound")
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            channel.setSound(uri, attributes)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(0, 500, 1000, 500)
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_wezwania)
            .setContentTitle("Nowe wezwanie")
            .setContentText("Nowe wezwanie oczekuje na potwierdzenie")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 1000, 500))

        manager.notify(NOTIFICATION_ID, builder.build())

        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(longArrayOf(0, 500, 1000, 500), -1)
    }

    suspend fun getPendingWezwaniaCountSuspend(): Int {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(""http://" + serverIp + "/wezwania/get_wezwania.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val wezwaniaJson = JSONArray(response)

                // Liczymy wezwania, które NIE są zaakceptowane
                var count = 0
                for (i in 0 until wezwaniaJson.length()) {
                    val obj = wezwaniaJson.getJSONObject(i)
                    val zaakceptowana = obj.getInt("zaakceptowana")
                    if (zaakceptowana == 0) {
                        count++
                    }
                }
                count
            } catch (e: Exception) {
                0
            }
        }
    }
}
