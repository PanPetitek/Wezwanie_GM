import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

package com.example.wezwanie_gm.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.wezwanie_gm.utils.NetworkUtils
import com.example.wezwanie_gm.utils.NotificationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val sharedPrefs = applicationContext.getSharedPreferences("wezwanie_prefs", Context.MODE_PRIVATE)
            val lastKnownCount = sharedPrefs.getInt("wezwanie_count", 0)
            val currentCount = NetworkUtils.getPendingWezwaniaCountSuspend()

            if (currentCount > lastKnownCount) {
                NotificationUtils.showAlarmNotification(applicationContext)
            }
            sharedPrefs.edit().putInt("wezwanie_count", currentCount).apply()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
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
