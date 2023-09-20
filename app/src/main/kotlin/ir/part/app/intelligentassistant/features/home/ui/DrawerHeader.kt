package ir.part.app.intelligentassistant.features.home.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_1
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
    Column(
        modifier
            .fillMaxHeight()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = 36.dp,
                    top = 32.dp,
                    start = 24.dp,
                    end = 78.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = AIResource.drawable.ic_vira),
                contentDescription = null
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = stringResource(id = AIResource.string.app_name_farsi),
                style = MaterialTheme.typography.h6,
                color = Color_Text_1
            )
        }
        DrawerBody(
            title = stringResource(id = AIResource.string.lbl_invite_friends),
            onItemClick = { inviteFriendOnclick() }
        )
        DrawerBody(
            title = stringResource(id = AIResource.string.lbl_about_vira),
            onItemClick = { aboutUsOnClick() }
        )
    }

}

@Composable
fun DrawerBody(
    modifier: Modifier = Modifier,
    title: String,
    onItemClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = 12.dp,
                bottom = 12.dp,
                end = 16.dp,
                start = 24.dp
            )
            .clickable { onItemClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(3f),
            style = MaterialTheme.typography.subtitle1
        )
        Image(
            painter = painterResource(id = AIResource.drawable.ic_next),
            contentDescription = null,
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