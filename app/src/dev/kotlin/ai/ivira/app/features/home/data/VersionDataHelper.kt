package ai.ivira.app.features.home.data

import javax.inject.Inject

class VersionDataHelper @Inject constructor() {
    init {
        System.loadLibrary("vira")
    }

    fun gwu(): String = cgwu()

    fun gw(): String = cgwd()

    fun up(): String = cupd()

    fun gwp(): String = cgwpd()

    fun gws(): String = cgws()

    private external fun cgwu(): String
    private external fun cgwd(): String
    private external fun cupd(): String
    private external fun cgwpd(): String
    private external fun cgws(): String
}