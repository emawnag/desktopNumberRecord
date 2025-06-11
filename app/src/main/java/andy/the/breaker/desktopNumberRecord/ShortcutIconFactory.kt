package andy.the.breaker.desktopNumberRecord

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Icon
import androidx.core.content.ContextCompat

// ShortcutIconFactory.kt
class ShortcutIconFactory {
    companion object {
    /**
     * 把 0-11 轉成 Icon：
     * 0-9 → 數字
     * 10  → V
     * 11  → X
     */
    fun create(context: Context, i: Int): Icon {
        val size = context.resources
            .getDimensionPixelSize(R.dimen.dynamic_shortcut_icon_size) // 給個 48dp or 56dp 就好

        // 1. 建立空白 bitmap
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 2. 背景（圓形或方形看你爽）
        Paint().apply {
            color = ContextCompat.getColor(context, R.color.shortcut_bg) // 自己挑顏色
            isAntiAlias = true
            canvas.drawRoundRect(
                RectF(0f, 0f, size.toFloat(), size.toFloat()),
                size / 5f, size / 5f, this
            )
        }

        // 3. 寫字
        val label = when (i) {
            in 0..9 -> i.toString()
            10 -> "V"
            11 -> "X"
            else -> "?"
        }
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = size * 0.6f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        }
        // y 座標要扣 baseline，所以算 bounds
        val bounds = Rect()
        paint.getTextBounds(label, 0, label.length, bounds)
        val y = size / 2f - bounds.exactCenterY()
        canvas.drawText(label, size / 2f, y, paint)

        return Icon.createWithBitmap(bitmap)
    }
}}
