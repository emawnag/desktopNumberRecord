package andy.the.breaker.desktopNumberRecord

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import andy.the.breaker.desktopNumberRecord.ui.theme.Testempty1Theme
import android.Manifest
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.content.FileProvider
import andy.the.breaker.desktopNumberRecord.viewer.HtmlOverlayActivity
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {
    private lateinit var warehouse: WarehouseManagement
    private lateinit var gpsMoneyParser : GPSMoneyParser
    val genHTML :GenHTML = GenHTML()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        warehouse = WarehouseManagement(this) // 初始化
        gpsMoneyParser=GPSMoneyParser(warehouse)
        setContent {
            var fileContent by remember { mutableStateOf("尚未讀取") }
            val scrollState = rememberScrollState()
            val coroutineScope = rememberCoroutineScope()

            Testempty1Theme {
                Scaffold { padding ->
                    Column(
                        modifier = Modifier
                            .padding(padding)
                            .padding(16.dp)
                            .fillMaxSize()
                            .verticalScroll(scrollState),  // 加這行讓整個 Column 可 scroll
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "手動新增捷徑（數字 0~9）",
                            style = MaterialTheme.typography.titleLarge
                        )

                        for (i in 0..11) {
                            AddShortcutButton(number = i) {
                                createPinnedShortcut(i)
                            }
                        }

                        Button(
                            onClick = {
                                fileContent = warehouse.readFromFile("cacheHL.json")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("讀取")
                        }

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    val raw = warehouse.readFromFile("main.json")
                                    val parsed = gpsMoneyParser.parse(raw)  // suspend function
                                    fileContent = parsed.toString()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("解析GPS金額")
                        }

                        Text("讀取內容：$fileContent")

                        ShareJsonButton(this@MainActivity,warehouse)

                        Button(
                            onClick = {
                                requestLocationPermission(this@MainActivity)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("取得GPS權限")
                        }

                        Button(
                            onClick = {
                                warehouse.removeFileByName("main.json")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("移除主資料庫")
                        }

                        Button(
                            onClick = { HtmlOverlayActivity.start(this@MainActivity) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("打開內嵌瀏覽器")
                        }
                        //writeToFileIfNotExists
                        Button(
                            onClick = { warehouse.writeToFileIfNotExists("index.html","<html><body><h1>123</h1></body></html>")},
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("測試加idxhtml")
                        }

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                val raw = warehouse.readFromFile("main.json")
                                val parsed = genHTML.genBasicHtml(gpsMoneyParser.parse(raw))
                                warehouse.writeToFileOverwrite("index.html",parsed)
                                    HtmlOverlayActivity.start(this@MainActivity)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("生成檢視檔")
                        }
                    }
                }
            }
        }
    }

    fun requestLocationPermission(activity: Activity) {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION

        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), 101)
        } else {
            Toast.makeText(activity, "✅ 已經有 GPS 權限", Toast.LENGTH_SHORT).show()
        }
    }


    private fun createPinnedShortcut(i: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val shortcutManager = getSystemService(ShortcutManager::class.java)

            val shortcutIntent = Intent(this, ShortcutActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                putExtra("shortcut_number", i)
            }

            val label = if (i == 10) {
                "ENTER"
            } else if (i == 11) {
                "CLEAR"
            } else {
                "數字$i"
            }

            val shortcut = ShortcutInfo.Builder(this, "pinned_shortcut_$i")
                .setShortLabel(label)
                .setIcon(ShortcutIconFactory.create(this, i))
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
@Composable
fun ShareJsonButton(context: Context,warehouse:WarehouseManagement) {
    var fileContent by remember { mutableStateOf("") }

    Column {
        Button(
            onClick = {
                fileContent = warehouse.readFromFile("main.json")

                // 建立一個暫存 txt 檔案
                val file = File(context.cacheDir, "shared_content.txt").apply {
                    writeText(fileContent)
                }

                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",  // 記得 AndroidManifest.xml 裡要配置 provider
                    file
                )

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                context.startActivity(Intent.createChooser(shareIntent, "分享 JSON 給..."))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("分享 JSON")
        }

        Text("讀取內容：$fileContent")
    }
}
