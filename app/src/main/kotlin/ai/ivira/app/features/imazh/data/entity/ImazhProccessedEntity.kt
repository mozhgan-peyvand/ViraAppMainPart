package ai.ivira.app.features.imazh.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ImazhProcessedEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val imagePath: String,
    val keywords: List<String>,
    val prompt: String,
    val negativePrompt: String,
    val style: String
)