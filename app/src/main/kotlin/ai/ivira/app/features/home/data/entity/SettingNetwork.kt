package ai.ivira.app.features.home.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SettingNetwork(
    val name: String,
    val value: SettingValueNetwork
) {
    fun toVersionEntity() = VersionEntity(
        name = name,
        isForce = value.isForce == 1,
        versionName = value.versionName,
        versionNumber = value.versionNumber
    )
}