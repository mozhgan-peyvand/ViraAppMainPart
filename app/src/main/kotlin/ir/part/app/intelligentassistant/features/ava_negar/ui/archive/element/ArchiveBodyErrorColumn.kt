package ir.part.app.intelligentassistant.features.ava_negar.ui.archive.element

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Primary_300
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Primary_Opacity_15
import ir.part.app.intelligentassistant.utils.ui.theme.IntelligentAssistantTheme

@Composable
fun ArchiveBodyErrorColumn(
    isNetworkAvailable: Boolean,
    onTryAgainClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        ErrorMessage(
            isNetworkAvailable = isNetworkAvailable,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(1f)
                .align(CenterVertically)
        )

        if (isNetworkAvailable) {
            TextButton(
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color_Primary_Opacity_15
                ),
                contentPadding = PaddingValues(
                    start = 28.dp, end = 26.dp, top = 10.dp, bottom = 10.dp
                ),
                onClick = { onTryAgainClick() }
            ) {
                Row(
                    verticalAlignment = CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.lbl_try_again),
                        style = MaterialTheme.typography.button,
                        color = Color_Primary_300
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    Icon(
                        painter = painterResource(id = R.drawable.ic_retry),
                        contentDescription = null,
                        tint = Color_Primary_300
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ArchiveBodyErrorColumnPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ArchiveBodyErrorColumn(
                isNetworkAvailable = true,
                onTryAgainClick = {}
            )
        }
    }
}
