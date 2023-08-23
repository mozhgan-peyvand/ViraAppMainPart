package ir.part.app.intelligentassistant.features.ava_negar.ui.update

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Card
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_1
import ir.part.app.intelligentassistant.utils.ui.theme.Color_White
import ir.part.app.intelligentassistant.utils.ui.theme.IntelligentAssistantTheme

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

        Image(
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .size(200.dp),
            painter = painterResource(id = R.drawable.img_update),
            contentDescription = null
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
            onClick = onUpdateClick
        ) {
            Text(
                text = stringResource(id = R.string.lbl_update_app),
                style = MaterialTheme.typography.button,
                color = Color_White
            )
        }
    }
}

@Preview
@Composable
private fun ForceUpdateScreenPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ForceUpdateScreen({})
        }
    }
}