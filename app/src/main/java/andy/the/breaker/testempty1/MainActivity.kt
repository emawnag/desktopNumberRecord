package andy.the.breaker.testempty1

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import andy.the.breaker.testempty1.ui.theme.Testempty1Theme
import java.io.File
import kotlin.jvm.java

class MainActivity : ComponentActivity() {
    private lateinit var warehouse: WarehouseManagement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        warehouse = WarehouseManagement(this) // 初始化

        setContent {
            var fileContent by remember { mutableStateOf("尚未讀取") }

            Testempty1Theme {
                Scaffold { padding ->
                    Column(
                        modifier = Modifier
                            .padding(padding)
                            .padding(16.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(onClick = {
                            shortcutIndex = 0
                            createNextShortcut()
                        }, modifier = Modifier.fillMaxWidth()) {
                            Text("新增捷徑")
                        }

                        Button(
                            onClick = {
                                val msg = warehouse.writeToFileIfNotExists("my_file.txt", "這是測試內容")
                                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("寫入（若不存在）")
                        }

                        Button(
                            onClick = {
                                fileContent = warehouse.readFromFile("main.json")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("讀取")
                        }

                        Text("讀取內容：$fileContent")
                    }
                }
            }
        }
    }

    /*private fun createShortcut() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val shortcutManager = getSystemService(ShortcutManager::class.java)
            val shortcutIntent = Intent(this, ShortcutActivity::class.java).apply {
                action = Intent.ACTION_VIEW
            }
            val shortcut = ShortcutInfo.Builder(this, "my_shortcut")
                .setShortLabel("快捷Toast")
                .setLongLabel("點我來Toast一個訊息")
                .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
                .setIntent(shortcutIntent)
                .build()

            shortcutManager?.dynamicShortcuts = listOf(shortcut)
            Toast.makeText(this, "已新增捷徑！", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "需要 Android 8.0 以上", Toast.LENGTH_SHORT).show()
        }
    }*/

    /*private fun createShortcuts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val shortcutManager = getSystemService(ShortcutManager::class.java)

            val shortcuts = mutableListOf<ShortcutInfo>()

            for (i in 0..9) {
                val shortcutIntent = Intent(this, ShortcutActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    putExtra("shortcut_number", i)
                }

                val shortcut = ShortcutInfo.Builder(this, "shortcut_$i")
                    .setShortLabel("數字$i")
                    .setLongLabel("這是數字 $i 的捷徑")
                    .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
                    .setIntent(shortcutIntent)
                    .build()

                shortcuts.add(shortcut)
            }

            shortcutManager?.dynamicShortcuts = shortcuts
            Toast.makeText(this, "已新增 0~9 的捷徑！", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "需要 Android 8.0 以上", Toast.LENGTH_SHORT).show()
        }
    }*/

    /*private fun createPinnedShortcuts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val shortcutManager = getSystemService(ShortcutManager::class.java)

            for (i in 0..9) {
                val shortcutIntent = Intent(this, ShortcutActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    putExtra("shortcut_number", i)
                }

                val shortcutInfo = ShortcutInfo.Builder(this, "pinned_shortcut_$i")
                    .setShortLabel("數字$i")
                    .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
                    .setIntent(shortcutIntent)
                    .build()

                if (shortcutManager?.isRequestPinShortcutSupported == true) {
                    val pinnedShortcutCallbackIntent = shortcutManager.createShortcutResultIntent(shortcutInfo)
                    val successCallback = PendingIntent.getBroadcast(this, 0, pinnedShortcutCallbackIntent, PendingIntent.FLAG_IMMUTABLE)

                    shortcutManager.requestPinShortcut(shortcutInfo, successCallback.intentSender)
                }
            }
        } else {
            Toast.makeText(this, "需要 Android 8.0 以上", Toast.LENGTH_SHORT).show()
        }
    }*/
    private var shortcutIndex = 0

    private fun createNextShortcut() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val shortcutManager = getSystemService(ShortcutManager::class.java)

            if (shortcutIndex > 9) {
                Toast.makeText(this, "已全部新增完畢！", Toast.LENGTH_SHORT).show()
                return
            }

            val i = shortcutIndex
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
                val pinnedShortcutCallbackIntent = shortcutManager.createShortcutResultIntent(shortcut)
                val successCallback = PendingIntent.getBroadcast(this, i, pinnedShortcutCallbackIntent, PendingIntent.FLAG_IMMUTABLE)

                // 註冊一個廣播接收器，當使用者新增完成後繼續下一個
                val receiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        unregisterReceiver(this)
                        shortcutIndex++
                        createNextShortcut()
                    }
                }

                registerReceiver(receiver, IntentFilter(ShortcutManager.ACTION_PINNED_SHORTCUT_RESULT))
                shortcutManager.requestPinShortcut(shortcut, successCallback.intentSender)
            } else {
                Toast.makeText(this, "裝置不支援 Pinned Shortcuts", Toast.LENGTH_SHORT).show()
            }
        }
    }



}

@Composable
fun AddShortcutScreen(onAddShortcut: () -> Unit) {
    Button(
        onClick = onAddShortcut,
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
            .height(80.dp) // 讓按鈕變高
    ) {
        Text(
            text = "新增捷徑",
            style = MaterialTheme.typography.titleLarge // 或用 fontSize = 24.sp 也可以
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Testempty1Theme {
        Greeting("Android")
    }
}