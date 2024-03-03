package ai.ivira.app.features.imazh.data

import androidx.room.TypeConverter

class ImazhListConvertor {
    @TypeConverter
    fun fromString(stringListString: String): List<String> {
        if (stringListString.isEmpty()) return emptyList()
        return stringListString.split(",").map { it }
    }

    @TypeConverter
    fun toString(stringList: List<String>): String {
        return stringList.joinToString(separator = ",")
    }
}