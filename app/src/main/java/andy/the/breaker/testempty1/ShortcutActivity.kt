package andy.the.breaker.testempty1

import android.app.Activity
import android.os.Bundle
import android.widget.Toast

class ShortcutActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Toast.makeText(this, "🎯 你點到了捷徑！", Toast.LENGTH_SHORT).show()

        finish()
    }
}
