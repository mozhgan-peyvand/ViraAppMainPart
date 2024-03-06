package ai.ivira.app.features.imazh.data.entity

import ai.ivira.app.utils.data.TrackTime
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ImazhTrackingFileEntity(
    @PrimaryKey
    val token: String,
    val keywords: List<String>,
    val englishKeywords: List<String>,
    val prompt: String,
    val englishPrompt: String,
    val style: String,
    val processEstimation: Int?,
    @Embedded("insert")
    val insertAt: TrackTime,
    @Embedded("lastFailure")
    val lastFailure: TrackTime?
)