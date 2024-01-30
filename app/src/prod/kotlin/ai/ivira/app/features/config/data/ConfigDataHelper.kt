package ai.ivira.app.features.config.data

import javax.inject.Inject

class ConfigDataHelper @Inject constructor() {
    init {
        System.loadLibrary("vira")
    }

    private fun tileConfigSuffix() = "/service/barjavand@3/data/vira/1.0.0?subSchema=tiles"
    fun gp() = cgp()
    fun ap() = cap() + tileConfigSuffix()

    private external fun cgp(): String
    private external fun cap(): String
}