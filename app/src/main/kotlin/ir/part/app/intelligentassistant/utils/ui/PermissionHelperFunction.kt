package ir.part.app.intelligentassistant.utils.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat

fun isPermissionDeniedPermanently(activity: Activity, permission: String): Boolean {
    return !ActivityCompat.shouldShowRequestPermissionRationale(
        activity,
        permission
    )
}

fun navigateToAppSettings(activity: Activity) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", activity.packageName, null)
    intent.data = uri
    activity.startActivity(intent)
}

