package ai.ivira.app.features.config.data

import javax.inject.Inject

class ConfigDataHelper @Inject constructor() {
    init {
        System.loadLibrary("vira")
    }

    val barjavandBaseUrl: String get() = "${cad()}/service/barjavand@3/data/vira/2.0.0"
    val tilesUrl get() = "${barjavandBaseUrl}?subSchema=tiles"
    val lastUpdateUrl get() = "${barjavandBaseUrl}?subSchema=lastUpdate"
    val versionsUrl get() = "${barjavandBaseUrl}?subSchema=versions"
    val hamahangUrl get() = "${barjavandBaseUrl}?subSchema=hamahang"

    fun ud() = cud()
    fun pd() = cpd()

    private external fun cud(): String
    private external fun cad(): String
    private external fun cpd(): String
}