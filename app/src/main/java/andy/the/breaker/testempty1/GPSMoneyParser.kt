package andy.the.breaker.testempty1
import android.os.Bundle
import org.json.JSONObject
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class Result(
    val average_GPS: Pair<Double, Double>,
    val money: Int,
    val average_datetime: LocalDateTime,
    val humanLook: Pair<String, JSONObject>?
)
class GPSMoneyParser(private val warehouse: WarehouseManagement) {
    suspend fun parse(jsonStr: String): List<Result> {
        val json = JSONObject(jsonStr)
        val logs = json.getJSONArray("logs")

        val results = mutableListOf<Result>()
        val digitBuffer = mutableListOf<Int>()
        val gpsBuffer = mutableListOf<Pair<Double, Double>>()
        val timeBuffer = mutableListOf<LocalDateTime>()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        var recording = true

        for (i in 0 until logs.length()) {
            val entry = logs.getJSONObject(i)
            val name = entry.getString("name")
            val gps = entry.getString("gps")
                .removePrefix("(").removeSuffix(")")
                .split(", ")
                .let { it[0].toDouble() to it[1].toDouble() }

            val datetime = entry.getString("datetime").let {
                LocalDateTime.parse(it, formatter)
            }

            when {
                name.startsWith("<") && name.endsWith(">") -> {
                    if (recording) {
                        digitBuffer.add(name.removePrefix("<").removeSuffix(">").toInt())
                        gpsBuffer.add(gps)
                        timeBuffer.add(datetime)
                    }
                }

                name == "CLEAR" -> {
                    digitBuffer.clear()
                    gpsBuffer.clear()
                    timeBuffer.clear()
                    recording = true
                }

                name == "ENTER" -> {
                    if (digitBuffer.isNotEmpty()) {
                        val avgLat = gpsBuffer.map { it.first }.average()
                        val avgLon = gpsBuffer.map { it.second }.average()
                        val money = digitBuffer.joinToString("").toInt()

                        //val humanLook = getAddressFromCoordinates(avgLat, avgLon)
                        val input = avgLat to avgLon
                        val cachedHL = warehouse.getHL(input)
                        val humanLook = cachedHL ?: getAddressFromCoordinates(avgLat, avgLon)?.also {
                            warehouse.putHL(input, it)
                        }


                        val baseTime = timeBuffer.first()
                        val avgSeconds = timeBuffer.map { Duration.between(baseTime, it).seconds }.average().toLong()
                        val avgDatetime = baseTime.plusSeconds(avgSeconds)

                        results.add(
                            Result(
                                average_GPS = avgLat to avgLon,
                                money = money,
                                average_datetime = avgDatetime,
                                humanLook = humanLook
                            )
                        )

                        digitBuffer.clear()
                        gpsBuffer.clear()
                        timeBuffer.clear()
                    }
                    recording = true
                }
            }
        }

        return results
    }

    suspend fun getAddressFromCoordinates(lat: Double, lon: Double): Pair<String, JSONObject>? {
        return withContext(Dispatchers.IO) {
            try {
                val baseUrl = "https://nominatim.openstreetmap.org/reverse"
                val params = mapOf(
                    "format" to "json",
                    "lat" to lat.toString(),
                    "lon" to lon.toString(),
                    "zoom" to "18",
                    "addressdetails" to "1"
                )
                val queryString = params.map { (key, value) ->
                    "${URLEncoder.encode(key, StandardCharsets.UTF_8)}=${URLEncoder.encode(value, StandardCharsets.UTF_8)}"
                }.joinToString("&")

                val url = URL("$baseUrl?$queryString")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                // 🔥 使用 Firefox 的 UA
                connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0"
                )

                connection.inputStream.bufferedReader().use { reader ->
                    val response = reader.readText()
                    val json = JSONObject(response)
                    val displayName = json.getString("display_name")
                    val address = json.getJSONObject("address")
                    return@withContext Pair(displayName, address)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            }
        }
    }

}