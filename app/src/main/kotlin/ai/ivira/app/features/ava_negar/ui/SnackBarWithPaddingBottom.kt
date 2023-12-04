package ai.ivira.app.features.ava_negar.ui

import ai.ivira.app.utils.ui.theme.Color_On_Surface_Inverse
import ai.ivira.app.utils.ui.theme.Color_Surface_Inverse
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SnackBarWithPaddingBottom(
    snackbarHostState: SnackbarHostState,
    shouldShowOverItems: Boolean,
    paddingValue: Float
) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .bottomAlignSnackBar(shouldShowOverItems, paddingValue),
        snackbar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color_Surface_Inverse, RoundedCornerShape(5.dp))
            ) {
                Text(
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    text = it.message,
                    style = MaterialTheme.typography.body2,
                    color = Color_On_Surface_Inverse
                )
            }
        }
    )
}

@Composable
fun SnackBar(
    snackbarHostState: SnackbarHostState,
    paddingBottom: Dp = 0.dp,
    maxLine: Int = 1
) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = paddingBottom),
        snackbar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color_Surface_Inverse, RoundedCornerShape(5.dp))
            ) {
                Text(
                    maxLines = maxLine,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    text = it.message,
                    style = MaterialTheme.typography.body2,
                    color = Color_On_Surface_Inverse
                )
            }
        }
    )
}

// todo move it to its correct place
fun Modifier.bottomAlignSnackBar(
    implementPadding: Boolean,
    paddingValue: Float
) = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    layout(constraints.maxWidth, constraints.maxHeight) {
        val marginFromBottom = if (implementPadding) paddingValue else 400f
        placeable.place(0, (constraints.maxHeight - marginFromBottom).toInt(), 10f)
    }
}