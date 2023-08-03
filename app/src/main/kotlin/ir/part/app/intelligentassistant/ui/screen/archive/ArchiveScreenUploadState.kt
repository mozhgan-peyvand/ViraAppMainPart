package ir.part.app.intelligentassistant.ui.screen.archive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.part.app.intelligentassistant.R


@Composable
fun UploadFileSectionInProgress(
    fileName: String,
    loading: Float,
    percent: String,
    isSavingFile: Boolean,
    onRetryCLick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface.copy(0.8f))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(0.7f)
                .padding(end = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.lbl_uploading_file))

                Text(
                    text = fileName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            LoadingIndicator(
                modifier = Modifier,
                loading = loading,
                percent = percent,
                isSavingFile = isSavingFile
            )
        }

        Column(modifier = Modifier.weight(0.3f)) {
            Row {
                IconButton(onClick = { onRetryCLick() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_retry),
                        contentDescription = null
                    )
                }

                IconButton(onClick = { onCancelClick() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = null
                    )
                }
            }
        }
    }

}

@Composable
fun UploadFileSectionFailure(
    fileName: String,
    onRetryCLick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(0.7f)
                .padding(end = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.msg_failure_in_upload))

                Text(
                    text = fileName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(modifier = Modifier.padding(top = 8.dp)) {
                Icon(
                    modifier = modifier.padding(end = 8.dp),
                    painter = painterResource(id = R.drawable.ic_failure_network),
                    contentDescription = null
                )
                Text(
                    fontSize = 14.sp,
                    text = stringResource(id = R.string.msg_try_again)
                )
            }
        }

        Column(modifier = Modifier.weight(0.3f)) {
            Row {
                IconButton(onClick = { onRetryCLick() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_retry),
                        contentDescription = null
                    )
                }

                IconButton(onClick = { onCancelClick() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
fun UploadFileSectionSuccess() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            modifier = Modifier.padding(end = 16.dp),
            painter = painterResource(R.drawable.ic_tick_circle),
            contentDescription = null
        )
        Text(text = stringResource(id = R.string.msg_upload_is_successfull))
    }

}

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    loading: Float,
    percent: String,
    isSavingFile: Boolean
) {

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier
    ) {
        if (isSavingFile)
            LinearProgressIndicator(
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .height(13.dp)
            )
        else
            LinearProgressIndicator(
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .height(13.dp),
                progress = loading,
            )


        Text(
            modifier = Modifier.padding(bottom = 1.dp),
            fontSize = 8.sp,
            text = percent
        )
    }
}

@Composable
fun UploadFileSection(
    modifier: Modifier = Modifier,
    uploadFileStatus: UploadFileStatus,
    fileName: String,
    percent: String,
    loading: Float,
    isSavingFile: Boolean,
    onRetryCLick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        when (uploadFileStatus) {
            is UploadSuccess -> UploadFileSectionSuccess()

            is UploadFailure -> {
                UploadFileSectionFailure(
                    fileName = fileName,
                    onRetryCLick = { onRetryCLick() },
                    onCancelClick = { onCancelClick() })
            }

            is UploadInProgress -> {
                UploadFileSectionInProgress(
                    fileName = fileName,
                    loading = loading,
                    percent = percent,
                    isSavingFile = isSavingFile,
                    onRetryCLick = { onRetryCLick() },
                    onCancelClick = { onCancelClick() }
                )
            }

            is UploadIdle -> {}
        }
    }
}