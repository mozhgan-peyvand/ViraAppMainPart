package ai.ivira.app.features.config.data

import javax.inject.Inject

class ConfigDataHelper @Inject constructor() {
    init {
        System.loadLibrary("vira")
    }

    val barjavandBaseUrl: String get() = "${cap()}/service/barjavand@3/data/vira/2.0.0"
    val tilesUrl get() = "${barjavandBaseUrl}?subSchema=tiles"
    val lastUpdateUrl get() = "${barjavandBaseUrl}?subSchema=lastUpdate"
    val versionsUrl get() = "${barjavandBaseUrl}?subSchema=versions"

    fun gp() = cgp()

    private external fun cgp(): String
    private external fun cap(): String
}