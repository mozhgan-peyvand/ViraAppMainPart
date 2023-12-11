package ai.ivira.app.features.ava_negar.data.entity

import ai.ivira.app.utils.data.TrackTime
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AvanegarTrackingFileEntity(
    @PrimaryKey
    val token: String,
    val processEstimation: Int?,
    val filePath: String,
    val title: String,
    @Embedded("insert")
    val insertAt: TrackTime,
    @Embedded("lastFailure")
    val lastFailure: TrackTime?
)