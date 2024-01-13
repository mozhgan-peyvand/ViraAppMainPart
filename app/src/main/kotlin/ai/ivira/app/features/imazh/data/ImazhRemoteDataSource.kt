package ai.ivira.app.features.imazh.data

import ai.ivira.app.features.imazh.data.entity.TextToImageRequestNetwork
import ai.ivira.app.features.imazh.data.entity.TextToImageResult
import ai.ivira.app.utils.data.api_result.ApiResult
import javax.inject.Inject

class ImazhRemoteDataSource @Inject constructor(
    private val imazhService: ImazhService
) {
    init {
        System.loadLibrary("vira")
    }

    suspend fun sendTextToImage(
        photoDescribe: TextToImageRequestNetwork
    ): ApiResult<TextToImageResult> {
        return when (val result = imazhService.sendTextToImage(
            photoDescribe = photoDescribe,
            apiKey = sai(),
            url = bi()
        )) {
            is ApiResult.Success -> ApiResult.Success(result.data.data)
            is ApiResult.Error -> ApiResult.Error(result.error)
        }
    }

    private external fun sai(): String
    private external fun bi(): String
}