package andy.the.breaker.testempty1
import org.json.JSONObject
import org.json.JSONArray
data class Result(val average_GPS: Pair<Double, Double>, val money: Int)
class GPSMoneyParser {
    fun parse(jsonStr: String): List<Map<String, Any>> {
        val json = JSONObject(jsonStr)
        val logs = json.getJSONArray("logs")

        val results = mutableListOf<Map<String, Any>>()
        val digitBuffer = mutableListOf<Int>()
        val gpsBuffer = mutableListOf<Pair<Double, Double>>()

        var recording = true

        for (i in 0 until logs.length()) {
            val entry = logs.getJSONObject(i)
            val name = entry.getString("name")
            val gps = entry.getString("gps")
                .removePrefix("(").removeSuffix(")")
                .split(", ")
                .let { it[0].toDouble() to it[1].toDouble() }

            when {
                name.startsWith("<") && name.endsWith(">") -> {
                    if (recording) {
                        digitBuffer.add(name.removePrefix("<").removeSuffix(">").toInt())
                        gpsBuffer.add(gps)
                    }
                }

                name == "CLEAR" -> {
                    digitBuffer.clear()
                    gpsBuffer.clear()
                    recording = true // reset so we can start again
                }

                name == "ENTER" -> {
                    if (digitBuffer.isNotEmpty()) {
                        val avgLat = gpsBuffer.map { it.first }.average()
                        val avgLon = gpsBuffer.map { it.second }.average()
                        val money = digitBuffer.joinToString("").toInt()

                        results.add(
                            mapOf(
                                "average_GPS" to "($avgLat, $avgLon)",
                                "money" to money
                            )
                        )
                        digitBuffer.clear()
                        gpsBuffer.clear()
                    }
                    recording = true
                }
            }
        }

        return results
    }
}