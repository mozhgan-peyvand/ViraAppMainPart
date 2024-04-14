package ai.ivira.app.utils.data.tracker

import ai.ivira.app.utils.data.TrackTime

interface TrackableItem {
    val trackingToken: String
    val trackingProcessEstimation: Int?
    val trackingInsertAt: TrackTime
    val trackingLastFailure: TrackTime?
}