package ai.ivira.app.features.config.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ConfigHamahangNetwork(
    val speakers: List<ConfigObjectNetwork<ConfigHamahangSpeakerNetwork>>
) {
    fun toConfigHamahangEntity(): ConfigHamahangEntity {
        return ConfigHamahangEntity(
            speakers = speakers.map { it.toConfigHamahangSpeakerEntity() }
        )
    }
}

@JsonClass(generateAdapter = true)
data class ConfigHamahangSpeakerNetwork(
    val status: Boolean
)