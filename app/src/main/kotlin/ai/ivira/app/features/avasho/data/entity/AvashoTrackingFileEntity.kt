package ai.ivira.app.features.avasho.data.entity

import ai.ivira.app.utils.data.TrackTime
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AvashoTrackingFileEntity(
    @PrimaryKey
    val token: String,
    val title: String,
    val text: String,
    val processEstimation: Int?,
    @Embedded("insert")
    val insertAt: TrackTime,
    @Embedded("lastFailure")
    val lastFailure: TrackTime?
)