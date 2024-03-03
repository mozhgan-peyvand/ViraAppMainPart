package ai.ivira.app.features.imazh.data.entity

data class ImazhArchiveFilesEntity(
    val processed: List<ImazhProcessedFileEntity>,
    val tracking: List<ImazhTrackingFileEntity>
)