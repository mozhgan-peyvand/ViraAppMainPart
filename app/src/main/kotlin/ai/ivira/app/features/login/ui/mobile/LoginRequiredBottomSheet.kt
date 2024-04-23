package ai.ivira.app.features.login.ui.mobile

import ai.ivira.app.R
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_White
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun LoginRequiredBottomSheet(
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    topStart = 10f,
                    topEnd = 10f,
                    bottomEnd = 0f,
                    bottomStart = 0f
                )
            )
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(id = R.string.lbl_create_new_account),
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 5.dp)
        )
        Text(
            text = stringResource(id = R.string.msg_create_new_account),
            style = MaterialTheme.typography.body1
        )

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            contentPadding = PaddingValues(14.dp),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                safeClick {
                    onConfirmClick()
                }
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color_White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.lbl_understood),
                style = MaterialTheme.typography.button
            )
        }
    }
}