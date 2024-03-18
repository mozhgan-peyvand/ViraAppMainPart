package ai.ivira.app.features.hamahang.data.entity

import ai.ivira.app.utils.data.TrackTime
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HamahangTrackingFileEntity(
    @PrimaryKey
    val token: String,
    val title: String,
    val inputFilePath: String,
    val speaker: String,
    val processEstimation: Int?,
    @Embedded("insert")
    val insertAt: TrackTime,
    @Embedded("lastFailure")
    val lastFailure: TrackTime?
)