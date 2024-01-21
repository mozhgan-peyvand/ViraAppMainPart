package ai.ivira.app.features.imazh.ui.newImageDescriptor.component

import ai.ivira.app.R
import ai.ivira.app.features.imazh.ui.newImageDescriptor.model.ImazhKeywordChipType
import ai.ivira.app.features.imazh.ui.newImageDescriptor.model.ImazhKeywordView
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.theme.Color_On_Surface
import ai.ivira.app.utils.ui.theme.Color_Primary_200
import ai.ivira.app.utils.ui.theme.Color_Primary_Opacity_15
import ai.ivira.app.utils.ui.theme.Color_Surface_Container_High
import ai.ivira.app.utils.ui.widgets.ViraIcon
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ImazhKeywordItem(
    value: ImazhKeywordView,
    type: ImazhKeywordChipType,
    onClick: () -> Unit
) {
    val backgroundColor by remember(type) {
        mutableStateOf(
            if (type == ImazhKeywordChipType.DefaultSelected) {
                Color_Primary_Opacity_15
            } else {
                Color_Surface_Container_High
            }
        )
    }

    val showIcon by remember(type) {
        mutableStateOf(type != ImazhKeywordChipType.Normal)
    }

    val tintIcon by remember(type) {
        mutableStateOf(
            if (type == ImazhKeywordChipType.DefaultSelected) {
                Color_On_Surface
            } else {
                Color_Primary_200
            }
        )
    }

    val textStartPadding by remember(showIcon) {
        mutableStateOf(if (showIcon) 0.dp else 12.dp)
    }

    Chip(
        modifier = Modifier.padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ChipDefaults.chipColors(
            contentColor = backgroundColor,
            backgroundColor = backgroundColor
        ),
        leadingIcon = {
            AnimatedVisibility(
                visible = showIcon,
                enter = scaleIn(),
                exit = scaleOut(animationSpec = spring(stiffness = Spring.StiffnessHigh))
            ) {
                ViraIcon(
                    drawable = R.drawable.ic_close,
                    tint = tintIcon,
                    contentDescription = null,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        },
        onClick = {
            onClick()
        }
    ) {
        Text(
            text = value.keywordName,
            style = MaterialTheme.typography.button,
            color = Color_On_Surface,
            modifier = Modifier.padding(end = 12.dp, start = textStartPadding)
        )
    }
}

@ViraDarkPreview
@Composable
private fun ImazhKeywordItemPreview() {
    ViraPreview {
        ImazhKeywordItem(
            value = ImazhKeywordView(
                keywordName = "سفید",
                farsiKeyword = "سفید",
                englishKeyword = "white"
            ),
            type = ImazhKeywordChipType.Normal,
            onClick = {}
        )
    }
}