package andy.the.breaker.testempty1

import android.app.Activity
import android.os.Bundle
import android.widget.Toast

class ShortcutActivity : Activity() {
    private lateinit var warehouse: WarehouseManagement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        warehouse = WarehouseManagement(this)

        // 取得 shortcut_number
        val number = intent.getIntExtra("shortcut_number", -1)

        if (number != -1) {
            val msg = "你點到了數字 $number 的捷徑"
            Toast.makeText(this, "🎯 $msg", Toast.LENGTH_SHORT).show()

            // 根據 number 決定要 log 什麼訊息
            val logText = when (number) {
                1 -> "測試訊息A"
                2 -> "測試訊息B"
                3 -> "測試訊息C"
                4 -> "測試訊息D"
                5 -> "測試訊息E"
                else -> "點了數字 $number"
            }

            warehouse.logClick(logText)
        } else {
            Toast.makeText(this, "⚠️ 沒有取得捷徑編號", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}
