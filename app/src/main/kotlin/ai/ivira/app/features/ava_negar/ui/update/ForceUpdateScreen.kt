package ai.ivira.app.features.ava_negar.ui.update

import ai.ivira.app.R
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Card
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.widgets.ViraImage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun ForceUpdateScreen(
    onUpdateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(27.dp),
        modifier = modifier
            .fillMaxWidth()
            .background(Color_Card),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.subtitle1,
            color = Color_White,
            text = stringResource(id = R.string.lbl_update)
        )

        ViraImage(
            drawable = R.drawable.img_update,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .size(200.dp)
        )

        Text(
            modifier = Modifier.padding(horizontal = 33.dp),
            style = MaterialTheme.typography.body2,
            color = Color_Text_1,
            text = stringResource(id = R.string.msg_update)
        )

        Button(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 38.dp),
            onClick = {
                safeClick {
                    onUpdateClick()
                }
            }
        ) {
            Text(
                text = stringResource(id = R.string.lbl_update_app),
                style = MaterialTheme.typography.button,
                color = Color_White
            )
        }
    }
}

@ViraDarkPreview
@Composable
private fun ForceUpdateScreenPreview() {
    ViraPreview {
        ForceUpdateScreen(onUpdateClick = {})
    }
}