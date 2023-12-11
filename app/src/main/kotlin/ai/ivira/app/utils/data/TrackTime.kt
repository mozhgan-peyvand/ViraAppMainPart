package ai.ivira.app.utils.data

import androidx.room.ColumnInfo

// ColumnInfo is used because this class is embedded in some entities
data class TrackTime(
    @ColumnInfo("SystemTime")
    val systemTime: Long,
    @ColumnInfo("BootTime")
    val bootTime: Long
)