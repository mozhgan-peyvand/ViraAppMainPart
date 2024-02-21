package ai.ivira.app.features.imazh.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ImazhProcessedFileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val imagePath: String,
    val filePath: String,
    val keywords: List<String>,
    val englishKeywords: List<String>,
    val prompt: String,
    val englishPrompt: String,
    val negativePrompt: String,
    val englishNegativePrompt: String,
    val style: String,
    val createdAt: Long
)