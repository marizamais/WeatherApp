package gr.novidea.shared.utils

import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import eu.cpm.connector.cpm.CpmControllerClient
import eu.cpm.connector.cpm.CpmServiceListener
import gr.novidea.shared.R


class CpmHandler(
    private val activity: AppCompatActivity,
    private val TAG: String,
    private val onInitialized: (cpm: CpmControllerClient) -> Unit
) {

    fun initialize(permissions: MutableList<String> = mutableListOf()) {

        val pendingPermissions = mutableListOf<String>()

        permissions.add(activity.getString(R.string.cpm_permission))
        permissions.add("android.permission.WRITE_EXTERNAL_STORAGE")

        permissions.forEach {
            val permission = ContextCompat.checkSelfPermission(activity, it)
            if (permission == PackageManager.PERMISSION_DENIED) {
                pendingPermissions.add(it)
            }
        }

        if (pendingPermissions.isEmpty()) {
            initializeCpm()
        } else {
            ActivityCompat.requestPermissions(activity, pendingPermissions.toTypedArray(), 100)
        }
    }

    fun initializeCpm() {
        CpmControllerClient.instance().initialise(activity.applicationContext,
            cpmServiceListener = object : CpmServiceListener {
                override fun onIntegratorServiceConnected() {
                    Log.i(TAG, "onIntegratorServiceConnected")

                    val cpm = CpmControllerClient.instance()
                    onInitialized(cpm)
                }
            })
    }
}