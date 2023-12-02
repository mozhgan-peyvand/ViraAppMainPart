package ai.ivira.app.features.home.ui.home.version

import ai.ivira.app.R
import ai.ivira.app.features.home.ui.home.version.model.ReleaseNoteView
import ai.ivira.app.features.home.ui.home.version.model.VersionView
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.theme.Color_Card_Stroke
import ai.ivira.app.utils.ui.theme.Color_Primary
import ai.ivira.app.utils.ui.theme.Color_Primary_200
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.theme.labelMedium
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun UpdateItem(item: VersionView) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.lbl_app_changes),
                style = MaterialTheme.typography.button,
                color = Color_White,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = stringResource(id = R.string.lbl_version_title)
                    .plus(" ")
                    .plus(item.versionName),
                style = MaterialTheme.typography.labelMedium,
                color = Color_White,
                modifier = Modifier
                    .background(Color_Primary_200, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp)
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        item.releaseNote.forEach { note ->
            TextWithDot(note.title)
        }

        Spacer(modifier = Modifier.size(16.dp))

        Divider(
            color = Color_Card_Stroke,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )
    }
}

@Composable
private fun TextWithDot(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = stringResource(id = R.string.lbl_bullet),
            color = Color_Primary
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = text,
            color = Color_Text_1,
            style = MaterialTheme.typography.caption
        )
    }
}

@ViraDarkPreview
@Composable
private fun UpdateItemPreview() {
    ViraPreview {
        val release = ReleaseNoteView(
            0,
            "Test"
        )

        val item = VersionView(
            name = "Name",
            isForce = false,
            releaseNote = listOf(release, release, release, release),
            versionName = "Version Name",
            versionNumber = 500
        )

        UpdateItem(item)
    }
}