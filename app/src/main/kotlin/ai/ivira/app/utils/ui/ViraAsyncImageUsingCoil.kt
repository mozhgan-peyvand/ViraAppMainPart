package ai.ivira.app.utils.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Scale

@Composable
fun ViraAsyncImageUsingCoil(
    urlPath: String,
    contentDescription: String,
    onResultCallBack: (AsyncImagePainter.State) -> Unit,
    imageBuilder: ImageRequest.Builder.(urlPath: String) -> ImageRequest.Builder,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest
            .Builder(LocalContext.current)
            .imageBuilder(urlPath)
            .diskCacheKey(urlPath)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCacheKey(urlPath)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .crossfade(300)
            .scale(Scale.FILL)
            .allowHardware(false)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        onState = onResultCallBack,
        contentScale = ContentScale.FillWidth
    )
}