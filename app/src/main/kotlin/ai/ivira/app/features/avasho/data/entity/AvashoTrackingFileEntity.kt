package ai.ivira.app.features.avasho.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AvashoTrackingFileEntity(
    @PrimaryKey
    val token: String,
    val title: String,
    val processEstimation: Int?,
    val createdAt: Long,
    val bootElapsedTime: Long,
    @Embedded
    val lastFailure: AvashoLastTrackFailure?
)

data class AvashoLastTrackFailure(
    val lastFailedRequest: Long,
    val lastTrackedBootElapsed: Long
)