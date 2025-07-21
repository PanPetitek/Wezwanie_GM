package com.example.wezwanie_gm.utils
import com.example.wezwanie_gm.models.Wezwanie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import android.content.Context


object NetworkUtils {

    /** Domyślne IP – nadpisywane w Ustawieniach. */
    private var serverIp: String = "192.168.31.182"

    fun setServerIp(ip: String) {
        serverIp = ip
    }

    private val wezwaniaUrl: String
        get() = "http://$serverIp/wezwania/wezwania_get.php"

    fun fetchWezwania(
        rola: String,
        onSuccess: (List<Wezwanie>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val conn = URL(wezwaniaUrl).openConnection() as HttpURLConnection
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                    throw RuntimeException("HTTP ${conn.responseCode}")
                }

                val jsonText = conn.inputStream.bufferedReader().use { it.readText() }
                val json = JSONArray(jsonText)

                val list = (0 until json.length()).map { i ->
                    val o = json.getJSONObject(i)
                    Wezwanie(
                        id = o.getInt("id"),
                        nazwaMaszyny = o.getString("nazwaMaszyny"),
                        data = o.getString("data"),
                        godzina = o.getString("godzina"),
                        zaakceptowane = o.getBoolean("zaakceptowane"),
                        typZgloszenia = o.getString("typZgloszenia"),
                        ktoZaakceptowal = o.getString("ktoZaakceptowal")
                    )
                }

                val filtered = list.filter { it.typZgloszenia == rola }
                withContext(Dispatchers.Main) { onSuccess(filtered) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError(e) }
            }
        }
    }

    fun fetchHistoria(
        context: Context,
        rola: String,
        onSuccess: (List<Wezwanie>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // dynamiczne IP z SharedPreferences
                val sharedPrefs = context.getSharedPreferences("wezwanie_prefs", Context.MODE_PRIVATE)
                val serverIp = sharedPrefs.getString("ip", "") ?: ""
                val url = URL("http://$serverIp/wezwania/get_historia.php")

                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 5000
                conn.readTimeout = 5000

                if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                    throw RuntimeException("HTTP ${conn.responseCode}")
                }

                val jsonText = conn.inputStream.bufferedReader().use { it.readText() }
                val json = JSONArray(jsonText)

                val list = (0 until json.length()).map { i ->
                    val o = json.getJSONObject(i)
                    Wezwanie(
                        id = o.getInt("id"),
                        nazwaMaszyny = o.getString("nazwaMaszyny"),
                        data = o.getString("data"),
                        godzina = o.getString("godzina"),
                        zaakceptowane = o.getBoolean("zaakceptowane"),
                        typZgloszenia = o.getString("typZgloszenia"),
                        ktoZaakceptowal = o.getString("ktoZaakceptowal")
                    )
                }

                val filtered = list.filter { it.typZgloszenia == rola }
                withContext(Dispatchers.Main) { onSuccess(filtered) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError(e) }
            }
        }
    }


    fun zatwierdzWezwanie(
        id: Int,
        kto: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("http://$serverIp/wezwania/zaakceptuj_wezwanie.php")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                val body = "id=$id&ktoZaakceptowal=$kto"
                conn.outputStream.use { it.write(body.toByteArray()) }

                if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                    throw RuntimeException("HTTP ${conn.responseCode}")
                }

                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError(e) }
            }
        }
    }
}
