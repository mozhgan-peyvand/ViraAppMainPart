package ai.ivira.app.features.home.ui

import ai.ivira.app.R
import ai.ivira.app.utils.ui.safeClickable
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.theme.ViraTheme
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import androidx.annotation.DrawableRes
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

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
            ViraImage(
                drawable = R.drawable.ic_vira,
                contentDescription = null
            )
            Spacer(modifier = Modifier.size(8.dp))

            ViraImage(
                drawable = R.drawable.ic_app_name,
                contentDescription = null
            )
        }
        DrawerBody(
            title = stringResource(id = R.string.lbl_invite_friends),
            icon = R.drawable.ic_envelope,
            onItemClick = { inviteFriendOnclick() }
        )
        DrawerBody(
            title = stringResource(id = R.string.lbl_about_vira),
            icon = R.drawable.ic_info,
            onItemClick = { aboutUsOnClick() }
        )
    }
}

@Composable
fun DrawerBody(
    modifier: Modifier = Modifier,
    title: String,
    @DrawableRes icon: Int,
    onItemClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 12.dp, end = 16.dp, start = 24.dp)
            .safeClickable {
                onItemClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        ViraIcon(
            drawable = icon,
            contentDescription = null,
            tint = Color_Text_3
        )

        Spacer(modifier = Modifier.size(12.dp))

        Text(
            text = title,
            modifier = Modifier.weight(3f),
            style = MaterialTheme.typography.subtitle1,
            color = Color_Text_1
        )
        ViraImage(
            drawable = R.drawable.ic_next,
            contentDescription = null
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF070707)
@Composable
private fun DrawerLayoutPreview() {
    ViraTheme {
        CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Rtl
        ) {
            DrawerHeader(aboutUsOnClick = {}, inviteFriendOnclick = {})
        }
    }
}