package ai.ivira.app.features.home.ui.home.version.sheets

import ai.ivira.app.R
import ai.ivira.app.features.home.ui.home.version.ChangelogItem
import ai.ivira.app.features.home.ui.home.version.model.ChangelogView
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Primary_Opacity_15
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.theme.labelMedium
import ai.ivira.app.utils.ui.widgets.ViraImage
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
fun ChangelogBottomSheet(
    item: List<ChangelogView>,
    onUnderstoodClick: () -> Unit
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
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        ViraImage(
            drawable = R.drawable.ic_vira_update_changelog,
            contentDescription = null
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = stringResource(id = R.string.lbl_latest_changes),
            style = MaterialTheme.typography.h6,
            color = Color_White
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = stringResource(id = R.string.lbl_new_added_features_to_vira),
            style = MaterialTheme.typography.labelMedium,
            color = Color_Text_1,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
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
                ChangelogItem(versionView)
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        TextButton(
            contentPadding = PaddingValues(vertical = 12.dp),
            colors = ButtonDefaults.textButtonColors(backgroundColor = Color_Primary_Opacity_15),
            onClick = {
                safeClick { onUnderstoodClick() }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.lbl_understood),
                style = MaterialTheme.typography.button,
                color = Color_White
            )
        }
    }
}

@ViraDarkPreview
@Composable
private fun ChangelogBottomSheetPreview() {
    ViraPreview {
        val item = ChangelogView(
            versionName = "",
            versionCode = 111,
            releaseNotesTitles = listOf()
        )
        ChangelogBottomSheet(
            item = listOf(item, item, item),
            onUnderstoodClick = {}
        )
    }
}