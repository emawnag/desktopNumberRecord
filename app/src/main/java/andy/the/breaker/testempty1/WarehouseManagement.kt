package andy.the.breaker.testempty1

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WarehouseManagement(private val context: Context) {
    fun getHL(input: Pair<Double, Double>): Pair<String, JSONObject>? {
        val file = File(context.filesDir, "cacheHL.json")
        if (!file.exists()) return null

        val json = JSONObject(file.readText())
        val key = "${input.first},${input.second}"
        return if (json.has(key)) {
            val obj = json.getJSONObject(key)
            val name = obj.getString("name")
            val data = obj.getJSONObject("data")
            Pair(name, data)
        } else {
            null
        }
    }

    fun putHL(input: Pair<Double, Double>, humanLook: Pair<String, JSONObject>): Int {
        return try {
            val file = File(context.filesDir, "cacheHL.json")
            val json = if (file.exists()) JSONObject(file.readText()) else JSONObject()

            val key = "${input.first},${input.second}"
            val obj = JSONObject().apply {
                put("name", humanLook.first)
                put("data", humanLook.second)
            }
            json.put(key, obj)

            file.writeText(json.toString())
            1
        } catch (e: Exception) {
            0
        }
    }

    fun logClick(clickName: String, currentGPSlocation: String) {
        val fileName = "main.json"
        val file = File(context.filesDir, fileName)

        // 初始化 JSON 結構（如果檔案不存在）
        if (!file.exists()) {
            val initJson = JSONObject()
            initJson.put("logs", JSONArray())
            file.writeText(initJson.toString())
        }

        // 讀取並解析現有 JSON
        val jsonStr = file.readText()
        val jsonObj = JSONObject(jsonStr)
        val logsArray = jsonObj.getJSONArray("logs")

        // 建立新的 click 物件
        val clickObject = JSONObject()
        clickObject.put("name", clickName)
        clickObject.put("datetime", getCurrentDateTime())
        clickObject.put("gps", currentGPSlocation)

        // 加到 logs array 中
        logsArray.put(clickObject)

        // 寫回檔案
        file.writeText(jsonObj.toString())
    }

    private fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }


    /**
     * 將內容附加寫入指定檔案（無論檔案是否存在）
     * @param fileName 檔名
     * @param content 要附加的內容
     * @return 回傳狀態訊息
     */
    fun appendToFile(fileName: String, content: String): String {
        return try {
            val file = File(context.filesDir, fileName)
            file.appendText(content)
            "內容已成功附加到檔案：$fileName"
        } catch (e: Exception) {
            "附加內容失敗：${e.message}"
        }
    }

    /**
     * 移除指定檔案（若存在）
     * @param fileName 檔名
     * @return 回傳狀態訊息
     */
    fun removeFileByName(fileName: String): String {
        return try {
            val file = File(context.filesDir, fileName)
            if (file.exists()) {
                file.delete()
                "檔案已成功刪除：$fileName"
            } else {
                "檔案不存在：$fileName"
            }
        } catch (e: Exception) {
            "刪除檔案失敗：${e.message}"
        }
    }


    /**
     * 寫入資料到指定檔案（如果檔案不存在才寫入）
     * @param fileName 檔名
     * @param content 要寫入的內容
     * @return 回傳狀態訊息
     */
    fun writeToFileIfNotExists(fileName: String, content: String): String {
        val file = File(context.filesDir, fileName)
        return if (!file.exists()) {
            file.writeText(content)
            "檔案已建立並寫入內容"
        } else {
            "檔案已存在，無需重寫"
        }
    }

    /**
     * 寫入資料到指定檔案（如果檔案已存在則覆寫）
     * @param fileName 檔名
     * @param content 要寫入的內容
     * @return 回傳狀態訊息
     */
    fun writeToFileOverwrite(fileName: String, content: String): String {
        val file = File(context.filesDir, fileName)
        file.writeText(content)
        return "檔案已寫入（若已存在則已覆寫）"
    }


    /**
     * 讀取指定檔案內容
     * @param fileName 檔名
     * @return 檔案內容或錯誤訊息
     */
    fun readFromFile(fileName: String): String {
        val file = File(context.filesDir, fileName)
        return if (file.exists()) {
            file.readText()
        } else {
            "找不到檔案！"
        }
    }
}
