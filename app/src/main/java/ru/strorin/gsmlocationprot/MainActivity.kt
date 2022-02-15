package ru.strorin.gsmlocationprot

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.CellIdentityGsm
import android.telephony.CellIdentityLte
import android.telephony.CellIdentityWcdma
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            queryLocation()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestPermission() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                    queryLocation()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                } else -> {
                // No location access granted.
            }
            }
        }

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED-> queryLocation()

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
            //
            }
            else -> locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE
                )
            )
        }

    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun queryLocation() {
        val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val cellLocation = telephonyManager.allCellInfo
            Log.d("TESTTEST", "len = ${cellLocation.size}")
            for (cell in cellLocation) {
                Log.d("TESTTEST", cell.cellIdentity.javaClass.simpleName)
                when (cell.cellIdentity) {
                    is CellIdentityGsm -> {
                        (cell.cellIdentity as CellIdentityGsm).apply {
                            val l = if (lac != Int.MAX_VALUE) lac else null
                            val c = if (cid != Int.MAX_VALUE) cid else null
                            setText(l, c, mccString, mncString, mobileNetworkOperator)
                        }
                    }
                    is CellIdentityLte -> {
                        (cell.cellIdentity as CellIdentityLte).apply {
                            val l = if (tac != Int.MAX_VALUE) tac else null
                            val c = if (ci != Int.MAX_VALUE) ci else null
                            setText(l, c, mccString, mncString, mobileNetworkOperator)
                        }
                    }
                    is CellIdentityWcdma -> {
                        (cell.cellIdentity as CellIdentityWcdma).apply {
                            val l = if (lac != Int.MAX_VALUE) lac else null
                            val c = if (cid != Int.MAX_VALUE) cid else null
                            setText(l, c, mccString, mncString, mobileNetworkOperator)
                        }
                    }
                    else -> {

                    }
                }
            }

        } else {
            requestPermission()
        }
    }

    private fun setText(lac: Int?, cid: Int?, mccString: String?,
                        mncString: String?, mobileNetworkOperator: String?) {
        lac?.let {
            findViewById<TextView>(R.id.lac).apply {
                text = "$text lac = $lac"
            }
        }
        cid?.let {
            findViewById<TextView>(R.id.cid).apply {
                text = "$text cid = $cid"
            }
        }
        mccString?.let {
            findViewById<TextView>(R.id.mcc).apply {
                text = "$text mcc = $mccString"
            }
        }
        mncString?.let {
            findViewById<TextView>(R.id.mnc).apply {
                text = "$text mnc = $mncString"
            }
        }
        mobileNetworkOperator?.let {
            findViewById<TextView>(R.id.mno).apply {
                text = "$text operator = $it"
            }
        }
    }
}