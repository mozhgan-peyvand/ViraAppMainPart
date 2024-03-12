package ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets

import ai.ivira.app.R
import ai.ivira.app.features.imazh.data.ImazhImageStyle
import ai.ivira.app.features.imazh.ui.newImageDescriptor.component.ImazhStyleItem
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Primary
import ai.ivira.app.utils.ui.theme.Color_White
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ImazhSelectStyleBottomSheet(
    styles: List<ImazhImageStyle>,
    selectedStyle: ImazhImageStyle,
    confirmSelectionCallBack: (ImazhImageStyle) -> Unit
) {
    val configuration = LocalConfiguration.current
    val unconfirmedSelectedStyle = rememberSaveable(selectedStyle) { mutableStateOf(selectedStyle) }
    val height by remember(configuration.screenHeightDp) {
        mutableStateOf(configuration.screenHeightDp.dp * 0.72f)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = height)
            .padding(bottom = 20.dp, top = 32.dp)
            .padding(horizontal = 12.dp)
    ) {
        Text(
            text = stringResource(id = R.string.lbl_image_style),
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        FlowRow(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp, top = 12.dp)
                .weight(1f)
        ) {
            styles.forEach { style ->
                Spacer(modifier = Modifier.width(4.dp))
                ImazhStyleItem(
                    style = style,
                    isSelected = style == unconfirmedSelectedStyle.value,
                    showBorderOnSelection = true,
                    onItemClick = {
                        unconfirmedSelectedStyle.value = style
                    }
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }

        Button(
            contentPadding = PaddingValues(14.dp),
            elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                safeClick { unconfirmedSelectedStyle.value?.let { confirmSelectionCallBack(it) } }
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color_Primary,
                contentColor = Color_White
            ),
            enabled = true,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.lbl_accept),
                style = MaterialTheme.typography.button
            )
        }
    }
}

@Preview
@Composable
fun PreviewImazhSelectStyleBottomSheet() {
    ViraPreview {
        ImazhSelectStyleBottomSheet(
            styles = ImazhImageStyle.values().toList(),
            selectedStyle = ImazhImageStyle.None,
            confirmSelectionCallBack = {}
        )
    }
}