package andy.the.breaker.testempty1

import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import andy.the.breaker.testempty1.ui.theme.Testempty1Theme
import kotlin.jvm.java

class MainActivity : ComponentActivity() {
    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Testempty1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }*/
    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 顯示 Toast 訊息
        Toast.makeText(this, "你的訊息內容", Toast.LENGTH_SHORT).show()

        // 關閉 Activity
        finish()
    }*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Testempty1Theme {
                AddShortcutScreen { createShortcut() }
            }
        }
    }
    private fun createShortcut() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val shortcutManager = getSystemService(ShortcutManager::class.java)
/*
            val shortcutIntent = Intent(this, ShortcutReceiver::class.java).apply {
                action = Intent.ACTION_VIEW
            }
*/
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