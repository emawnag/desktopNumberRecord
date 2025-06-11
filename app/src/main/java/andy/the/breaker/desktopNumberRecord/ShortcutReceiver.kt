package andy.the.breaker.desktopNumberRecord
//我猜這個檔案被遺棄了
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ShortcutReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            Toast.makeText(context, "嗨！這是從捷徑點進來的Toast！", Toast.LENGTH_SHORT).show()
        }
    }
}
