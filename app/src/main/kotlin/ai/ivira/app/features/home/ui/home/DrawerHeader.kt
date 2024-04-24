package ai.ivira.app.features.home.ui.home

import ai.ivira.app.R
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClickable
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.theme.Cyan_200
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import ai.ivira.app.designsystem.theme.R as ThemeR

@Composable
fun DrawerHeader(
    userMobile: String?,
    aboutUsOnClick: () -> Unit,
    inviteFriendOnclick: () -> Unit,
    onUpdateClick: () -> Unit,
    onTermsOfServiceClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    DrawerHeaderBody(
        userMobile = userMobile.orEmpty(),
        aboutUsOnClick = aboutUsOnClick,
        inviteFriendOnclick = inviteFriendOnclick,
        onUpdateClick = onUpdateClick,
        onTermsOfServiceClick = onTermsOfServiceClick,
        onLogoutClick = onLogoutClick
    )
}

@Composable
private fun DrawerHeaderBody(
    userMobile: String,
    aboutUsOnClick: () -> Unit,
    inviteFriendOnclick: () -> Unit,
    onUpdateClick: () -> Unit,
    onTermsOfServiceClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxHeight()) {
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 24.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ViraImage(
                drawable = R.drawable.ic_vira,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.size(12.dp))

            ViraImage(
                drawable = R.drawable.ic_app_name,
                contentDescription = null
            )
        }

        if (userMobile.isNotBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 24.dp,
                        vertical = 12.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(color = Cyan_200, shape = CircleShape)
                        .clip(CircleShape)
                        .padding(6.dp)
                ) {
                    ViraIcon(
                        drawable = R.drawable.ic_profile_edit,
                        contentDescription = null,
                        tint = MaterialTheme.colors.background,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = userMobile,
                    color = Color_White,
                    style = MaterialTheme.typography.body1.copy(
                        fontFamily = FontFamily(Font(ThemeR.font.bahij_helvetica_neue_vira_edition_roman))
                    )
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
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

        DrawerBody(
            title = stringResource(id = R.string.lbl_terms_of_service),
            icon = R.drawable.ic_terms_of_service,
            onItemClick = { onTermsOfServiceClick() }
        )

        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        )

        DrawerBody(
            title = stringResource(id = R.string.lbl_logout),
            icon = R.drawable.ic_logout,
            onItemClick = { onLogoutClick() }
        )

        Spacer(modifier = Modifier.height(24.dp))
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
        DrawerHeader(
            userMobile = "",
            aboutUsOnClick = {},
            inviteFriendOnclick = {},
            onUpdateClick = {},
            onTermsOfServiceClick = {},
            onLogoutClick = {}
        )
    }
}