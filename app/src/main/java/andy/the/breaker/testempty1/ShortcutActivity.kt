package andy.the.breaker.testempty1

import android.app.Activity
import android.os.Bundle
import android.widget.Toast

class ShortcutActivity : Activity() {
    private lateinit var warehouse: WarehouseManagement
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        warehouse = WarehouseManagement(this)
        Toast.makeText(this, "🎯 你點到了捷徑！", Toast.LENGTH_SHORT).show()
        warehouse.logClick("測試訊息A")
        finish()
    }
}
