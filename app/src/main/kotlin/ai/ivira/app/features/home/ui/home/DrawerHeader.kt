package ai.ivira.app.features.home.ui.home

import ai.ivira.app.R
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClickable
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Cyan_200
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun DrawerHeader(
    aboutUsOnClick: () -> Unit,
    inviteFriendOnclick: () -> Unit,
    onUpdateClick: () -> Unit
) {
    DrawerHeaderBody(
        aboutUsOnClick = aboutUsOnClick,
        inviteFriendOnclick = inviteFriendOnclick,
        onUpdateClick = onUpdateClick
    )
}

@Composable
private fun DrawerHeaderBody(
    aboutUsOnClick: () -> Unit,
    inviteFriendOnclick: () -> Unit,
    onUpdateClick: () -> Unit,
    modifier: Modifier = Modifier
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
            title = stringResource(id = R.string.lbl_about_us),
            icon = R.drawable.ic_info,
            onItemClick = { aboutUsOnClick() }
        )

        DrawerBody(
            title = stringResource(id = R.string.lbl_update_app),
            icon = R.drawable.ic_update,
            onItemClick = { onUpdateClick() }
        )
    }
}

@Composable
fun DrawerBody(
    title: String,
    @DrawableRes icon: Int,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .safeClickable {
                onItemClick()
            }
            .padding(top = 12.dp, bottom = 12.dp, end = 16.dp, start = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ViraIcon(
            drawable = icon,
            contentDescription = null,
            tint = Cyan_200
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

@ViraDarkPreview
@Composable
private fun DrawerLayoutPreview() {
    ViraPreview {
        DrawerHeader(aboutUsOnClick = {}, inviteFriendOnclick = {}, onUpdateClick = {})
    }
}