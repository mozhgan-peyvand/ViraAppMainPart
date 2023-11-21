package ai.ivira.app.utils.ui

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES

fun isSdkVersion33orHigher(): Boolean =
    VERSION.SDK_INT >= VERSION_CODES.TIRAMISU