package ir.part.app.intelligentassistant.ui.screen.update

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import ir.part.app.intelligentassistant.ui.theme.IntelligentAssistantTheme

@Composable
fun ForceUpdateScreen(
    onUpdateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .size(200.dp)
                .padding(start = 16.dp, end = 16.dp, top = 32.dp),
            painter = painterResource(id = R.drawable.img_update),
            contentDescription = null
        )

        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(id = R.string.lbl_update)
        )
        Text(
            modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            text = stringResource(id = R.string.msg_update)
        )


        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 32.dp),
            onClick = onUpdateClick
        ) {
            Text(text = stringResource(id = R.string.lbl_update_app))
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