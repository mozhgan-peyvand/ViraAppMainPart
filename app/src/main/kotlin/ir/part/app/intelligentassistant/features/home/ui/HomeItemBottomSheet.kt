package ir.part.app.intelligentassistant.features.home.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.utils.ui.safeClick
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Primary_200
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Primary_Opacity_15
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_1
import ir.part.app.intelligentassistant.utils.ui.theme.Color_White
import ir.part.app.intelligentassistant.utils.ui.widgets.ViraImage

@Composable
fun HomeItemBottomSheet(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    title: String,
    textBody: String,
    action: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ViraImage(
                drawable = iconRes,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                color = Color_Text_1,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
        Text(
            text = textBody,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
            color = Color_White
        )
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color_Primary_Opacity_15,
                contentColor = Color_Primary_200
            ),
            shape = RoundedCornerShape(8.dp),
            elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
            onClick = {
                safeClick {
                    action()
                }
            }
        ) {
            Text(
                text = stringResource(id = R.string.lbl_understood),
                style = MaterialTheme.typography.button
            )
        }
    }
}