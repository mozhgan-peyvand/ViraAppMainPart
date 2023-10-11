package ai.ivira.app.features.ava_negar.data.entity

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
    val createdAt: Long,
    val bootElapsedTime: Long,
    @Embedded
    val lastFailure: LastTrackFailure?
)

data class LastTrackFailure(
    val lastFailedRequest: Long,
    val lastTrackedBootElapsed: Long
)