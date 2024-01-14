package ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets

import ai.ivira.app.R
import ai.ivira.app.features.imazh.data.ImazhImageStyle
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Primary
import ai.ivira.app.utils.ui.theme.Color_Primary_Opacity_15
import ai.ivira.app.utils.ui.theme.Color_Surface_Container_High
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.theme.labelMedium
import ai.ivira.app.utils.ui.widgets.ViraImage
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun SelectStyleBottomSheet(
    styles: List<ImazhImageStyle>,
    selectedStyle: ImazhImageStyle,
    confirmSelectionCallBack: (ImazhImageStyle) -> Unit
) {
    val configuration = LocalConfiguration.current
    val unconfirmedSelectedStyle = rememberSaveable(selectedStyle) { mutableStateOf(selectedStyle) }
    val height by remember(configuration.screenHeightDp) {
        mutableStateOf(configuration.screenHeightDp.dp * 0.64f)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = height)
            .padding(bottom = 20.dp, top = 32.dp)
            .padding(horizontal = 16.dp)
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
                StyleItem(
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

@Composable
fun StyleItem(
    style: ImazhImageStyle,
    isSelected: Boolean,
    showBorderOnSelection: Boolean,
    onItemClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val titleOverFlow by remember(isSelected) {
        mutableStateOf(if (isSelected) TextOverflow.Clip else TextOverflow.Ellipsis)
    }
    val shape = remember { RoundedCornerShape(8.dp) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .background(
                color = if (isSelected) Color_Primary_Opacity_15 else Color_Surface_Container_High,
                shape = shape
            )
            .then(
                if (showBorderOnSelection && isSelected) {
                    Modifier.border(
                        border = BorderStroke(width = 1.dp, color = Color_Primary),
                        shape = shape
                    )
                } else {
                    Modifier
                }
            )
            .clip(shape)
            .then(if (onItemClick != null) Modifier.clickable { safeClick { onItemClick() } } else Modifier)
            .padding(6.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            ViraImage(
                drawable = style.iconRes,
                contentDescription = style.key,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            if (style == ImazhImageStyle.None) {
                ViraImage(
                    drawable = R.drawable.ic_forbidden,
                    contentDescription = null,
                    modifier = Modifier.zIndex(1f)
                )
            }
        }
        Text(
            text = style.viewName,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            modifier = Modifier.then(if (isSelected) Modifier.basicMarquee() else Modifier),
            textAlign = TextAlign.Center,
            overflow = titleOverFlow
        )
    }
}

@Preview
@Composable
fun PreviewImageStyleItem() {
    ViraPreview {
        StyleItem(
            style = ImazhImageStyle.Cinematic,
            isSelected = false,
            showBorderOnSelection = true,
            onItemClick = {}
        )
    }
}

@Preview
@Composable
fun PreviewSelectStyleBottomSheet() {
    ViraPreview {
        SelectStyleBottomSheet(
            styles = ImazhImageStyle.values().toList(),
            selectedStyle = ImazhImageStyle.None,
            confirmSelectionCallBack = {}
        )
    }
}