package ai.ivira.app.features.config.data

import javax.inject.Inject

class ConfigDataHelper @Inject constructor() {
    init {
        System.loadLibrary("vira")
    }

    private fun tileConfigSuffix() = "/service/barjavand@3/data/vira/1.0.0?subSchema=tiles"
    fun ud() = cud()
    fun ad() = cad() + tileConfigSuffix()
    fun pd() = cpd()

    private external fun cud(): String
    private external fun cad(): String
    private external fun cpd(): String
}