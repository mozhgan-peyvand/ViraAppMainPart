package ir.part.app.intelligentassistant.features.ava_negar.ui.archive.element

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.utils.ui.safeClick
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Primary_300
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Primary_Opacity_15
import ir.part.app.intelligentassistant.utils.ui.theme.IntelligentAssistantTheme

@Composable
fun ArchiveBodyErrorGrid(
    isNetworkAvailable: Boolean,
    onTryAgainClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {

        ErrorMessage(
            isNetworkAvailable = isNetworkAvailable,
            textAlign = TextAlign.Center
        )

        if (isNetworkAvailable) {
            Spacer(modifier = Modifier.size(8.dp))

            TextButton(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color_Primary_Opacity_15
                ),
                contentPadding = PaddingValues(
                    start = 28.dp,
                    end = 24.dp,
                    top = 12.dp,
                    bottom = 12.dp
                ),
                onClick = {
                    safeClick {
                        onTryAgainClick()
                    }
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
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
private fun ArchiveBodyErrorPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ArchiveBodyErrorGrid(
                isNetworkAvailable = true,
                onTryAgainClick = {}
            )
        }
    }
}
