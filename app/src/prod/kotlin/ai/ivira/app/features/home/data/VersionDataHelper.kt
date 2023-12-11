package ai.ivira.app.features.home.data

import javax.inject.Inject

class VersionDataHelper @Inject constructor() {

    init {
        System.loadLibrary("vira")
    }

    fun gwu(): String = cgwu()

    fun gw(): String = cgw()

    fun up(): String = cup()

    fun gws(): String = cgws()

    private external fun cgwu(): String
    private external fun cgw(): String
    private external fun cup(): String
    private external fun cgws(): String
}