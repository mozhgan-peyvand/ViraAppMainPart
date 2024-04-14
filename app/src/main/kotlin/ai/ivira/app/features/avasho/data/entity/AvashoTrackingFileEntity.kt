package ai.ivira.app.features.avasho.data.entity

import ai.ivira.app.utils.data.TrackTime
import ai.ivira.app.utils.data.tracker.TrackableItem
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
) : TrackableItem {
    override val trackingToken: String get() = token
    override val trackingProcessEstimation: Int? get() = processEstimation
    override val trackingInsertAt: TrackTime get() = insertAt
    override val trackingLastFailure: TrackTime? get() = lastFailure
}