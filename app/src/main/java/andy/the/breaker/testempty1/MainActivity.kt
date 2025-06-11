package andy.the.breaker.testempty1

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import andy.the.breaker.testempty1.ui.theme.Testempty1Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Testempty1Theme {
                Scaffold { padding ->
                    Column(
                        modifier = Modifier
                            .padding(padding)
                            .padding(16.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "手動新增捷徑（數字 0~9）",
                            style = MaterialTheme.typography.titleLarge
                        )

                        for (i in 0..9) {
                            AddShortcutButton(number = i) {
                                createPinnedShortcut(i)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createPinnedShortcut(i: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val shortcutManager = getSystemService(ShortcutManager::class.java)

            val shortcutIntent = Intent(this, ShortcutActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                putExtra("shortcut_number", i)
            }

            val shortcut = ShortcutInfo.Builder(this, "pinned_shortcut_$i")
                .setShortLabel("數字$i")
                .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
                .setIntent(shortcutIntent)
                .build()

            if (shortcutManager?.isRequestPinShortcutSupported == true) {
                val pinnedShortcutCallbackIntent =
                    shortcutManager.createShortcutResultIntent(shortcut)
                val successCallback = PendingIntent.getBroadcast(
                    this, i, pinnedShortcutCallbackIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )

                shortcutManager.requestPinShortcut(shortcut, successCallback.intentSender)
            } else {
                Toast.makeText(this, "裝置不支援 Pinned Shortcuts", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "需要 Android 8.0 以上", Toast.LENGTH_SHORT).show()
        }
    }
}
@Composable
fun AddShortcutButton(number: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text("新增數字 $number 捷徑")
    }
}
