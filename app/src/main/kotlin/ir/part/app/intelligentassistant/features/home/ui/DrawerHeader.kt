package ir.part.app.intelligentassistant.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ir.part.app.intelligentassistant.utils.ui.theme.Color_BG
import ir.part.app.intelligentassistant.utils.ui.theme.IntelligentAssistantTheme
import ir.part.app.intelligentassistant.R as AIResource


@Composable
fun DrawerHeader(
    aboutUsOnClick: () -> Unit,
    inviteFriendOnclick: () -> Unit
) {
    DrawerHeaderBody(
        aboutUsOnClick = { aboutUsOnClick() },
        inviteFriendOnclick = { inviteFriendOnclick() }
    )

}

@Composable
private fun DrawerHeaderBody(
    modifier: Modifier = Modifier,
    aboutUsOnClick: () -> Unit,
    inviteFriendOnclick: () -> Unit
) {
    val context = LocalContext.current

    Column(modifier.fillMaxHeight()) {
        Row(
            modifier = modifier
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = modifier.size(80.dp),
                shape = CircleShape, color = Color_BG
            ) {}
            Column(modifier = modifier.padding(5.dp)) {
                Text(text = stringResource(id = AIResource.string.app_name_farsi))
                Text(text = stringResource(id = AIResource.string.lbl_assistant_smart_voice))
            }
        }
        DrawerBody(
            title = stringResource(id = AIResource.string.lbl_about_us),
            onItemClick = { aboutUsOnClick() }
        )
        DrawerBody(
            title = stringResource(id = AIResource.string.lbl_invite_friends),
            onItemClick = { inviteFriendOnclick() }
        )

    }

}

@Composable
fun DrawerBody(
    modifier: Modifier = Modifier,
    title: String,
    onItemClick: () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            Modifier
                .padding(top = 10.dp, start = 15.dp, bottom = 20.dp)
                .clickable { onItemClick() }
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, bottom = 20.dp)
                .height(2.dp)
                .background(
                    Color.LightGray
                )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DrawerLayoutPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Rtl,
        ) {
            DrawerHeader(aboutUsOnClick = {}, inviteFriendOnclick = {})
        }
    }
}