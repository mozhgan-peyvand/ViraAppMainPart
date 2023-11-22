package ai.ivira.app.utils.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

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

fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

fun Context.hasRecordAudioPermission(): Boolean {
    return hasPermission(Manifest.permission.RECORD_AUDIO)
}

fun Context.hasNotificationPermission(): Boolean {
    return !isSdkVersion33orHigher() || hasPermission(Manifest.permission.POST_NOTIFICATIONS)
}