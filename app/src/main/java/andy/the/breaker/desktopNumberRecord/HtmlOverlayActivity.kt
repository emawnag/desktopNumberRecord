package andy.the.breaker.desktopNumberRecord.viewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import android.webkit.WebViewClient
import java.io.File

class HtmlOverlayActivity : ComponentActivity() {

    companion object {
        fun start(ctx: Context) {
            ctx.startActivity(Intent(ctx, HtmlOverlayActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // index.html 事先就被 WarehouseManagement 寫在 filesDir
        val htmlFile = File(filesDir, "index.html")
        val htmlUrl  = "file://${htmlFile.absolutePath}"

        setContent {
            FullScreenWeb(htmlUrl)
        }
    }
}

@Composable
fun FullScreenWeb(url: String) {
    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.allowFileAccess = true
                settings.allowFileAccessFromFileURLs = true     // 讓本地檔能請外部 CDN
                settings.allowUniversalAccessFromFileURLs = true
                settings.domStorageEnabled = true

                webViewClient = WebViewClient()   // 不跳外部瀏覽器
                loadUrl(url)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
