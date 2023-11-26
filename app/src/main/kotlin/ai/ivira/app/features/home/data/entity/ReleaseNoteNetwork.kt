package ai.ivira.app.features.home.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReleaseNoteNetwork(
    val type: Int,
    val title: String
) {
    fun toReleaseNoteEntity(id: Int) = ReleaseNoteEntity(
        id = 0,
        versionNumber = id,
        type = type,
        title = title
    )
}