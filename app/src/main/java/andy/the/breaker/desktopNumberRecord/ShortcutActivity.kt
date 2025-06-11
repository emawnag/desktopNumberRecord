package andy.the.breaker.desktopNumberRecord

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class ShortcutActivity : Activity() {
    private lateinit var warehouse: WarehouseManagement
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        warehouse = WarehouseManagement(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val number = intent.getIntExtra("shortcut_number", -1)

        if (number != -1) {
            val msg = "你點到了數字 $number 的捷徑"
            Toast.makeText(this, "🎯 $msg", Toast.LENGTH_SHORT).show()

            val logText = when (number) {
                10 -> "ENTER"
                11 -> "CLEAR"
                else -> "<$number>"
            }

            // 取得 GPS 位置後再 log
            getCurrentLocation { location ->
                val gps = if (location != null) {
                    "(${location.latitude}, ${location.longitude})"
                } else {
                    "(未知位置)"
                }

                warehouse.logClick(logText, gps)
                finish()
            }
        } else {
            Toast.makeText(this, "⚠️ 沒有取得捷徑編號", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun getCurrentLocation(callback: (Location?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "⚠️ 沒有定位權限", Toast.LENGTH_SHORT).show()
            callback(null)
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                callback(location)
            }
            .addOnFailureListener {
                callback(null)
            }
    }
}
