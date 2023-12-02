package ai.ivira.app.features.home.ui.home.version.sheets

import ai.ivira.app.R
import ai.ivira.app.R.string
import ai.ivira.app.features.home.ui.home.version.UpdateItem
import ai.ivira.app.features.home.ui.home.version.model.ReleaseNoteView
import ai.ivira.app.features.home.ui.home.version.model.VersionView
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Primary
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.widgets.ViraImage
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun UpdateBottomSheet(
    item: List<VersionView>,
    onUpdateClick: () -> Unit,
    onLaterClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val height by remember(configuration.screenHeightDp) {
        mutableStateOf(configuration.screenHeightDp.dp * 0.7f)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = height)
            .padding(horizontal = 32.dp, vertical = 16.dp)
    ) {
        ViraImage(
            drawable = R.drawable.ic_vira_update,
            contentDescription = null
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = stringResource(id = R.string.lbl_new_vira_version),
            style = MaterialTheme.typography.h6,
            color = Color_White
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = stringResource(id = R.string.lbl_new_features_released_please_update_app),
            style = MaterialTheme.typography.body2,
            color = Color_Text_1,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.size(28.dp))

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(item) { versionView ->
                UpdateItem(versionView)
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                contentPadding = PaddingValues(vertical = 16.dp),
                onClick = {
                    safeClick { onLaterClick() }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(id = string.lbl_show_later),
                    style = MaterialTheme.typography.button,
                    color = Color_White
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            TextButton(
                contentPadding = PaddingValues(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color_Primary),
                onClick = {
                    safeClick { onUpdateClick() }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(id = string.lbl_update_app),
                    style = MaterialTheme.typography.button,
                    color = Color_White
                )
            }
        }
    }
}

@ViraDarkPreview
@Composable
private fun UpdateBottomSheetPreview() {
    ViraPreview {
        val release = ReleaseNoteView(
            0,
            "Test"
        )

        val item = VersionView(
            name = "",
            isForce = false,
            releaseNote = listOf(release, release, release, release),
            versionName = "",
            versionNumber = 111
        )
        UpdateBottomSheet(
            item = listOf(item, item, item),
            onUpdateClick = {},
            onLaterClick = {}
        )
    }
}