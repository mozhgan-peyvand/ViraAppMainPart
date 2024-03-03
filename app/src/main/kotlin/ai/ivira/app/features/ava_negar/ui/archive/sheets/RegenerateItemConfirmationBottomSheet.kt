package ai.ivira.app.features.ava_negar.ui.archive.sheets

import ai.ivira.app.R
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Primary_300
import ai.ivira.app.utils.ui.theme.Color_Primary_Opacity_15
import ai.ivira.app.utils.ui.theme.Color_Red_Opacity_15
import ai.ivira.app.utils.ui.theme.Color_Text_1
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

/**
 * Regeneration request would not be cancelled if bottomSheet is closed.
 * After request completion, either success or error response received, bottomSheet will be closed.
 * Background interaction is on.
 * */
@Composable
fun RegenerateItemConfirmationBottomSheet(
    cancelAction: () -> Unit,
    regenerateAction: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var lottieHeight by rememberSaveable { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(id = R.string.lbl_regenerate_image),
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(id = R.string.msg_regenerate_image),
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(bottom = 28.dp)
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                contentPadding = PaddingValues(14.dp),
                elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                modifier = Modifier.weight(1f),
                onClick = {
                    safeClick {
                        regenerateAction()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color_Primary_Opacity_15,
                    contentColor = Color_Primary_300
                ),
                shape = RoundedCornerShape(8.dp)

            ) {
                if (isLoading) {
                    LoadingLottie(
                        modifier = Modifier.height(with(density) { lottieHeight.toDp() })
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.lbl_generate_image),
                        style = MaterialTheme.typography.button,
                        color = Color_Text_1,
                        modifier = Modifier.onGloballyPositioned {
                            lottieHeight = it.size.height
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.size(8.dp))

            Button(
                contentPadding = PaddingValues(14.dp),
                elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onClick = {
                    safeClick {
                        cancelAction()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color_Red_Opacity_15,
                    contentColor = MaterialTheme.colors.onError
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.lbl_cancel_2),
                    style = MaterialTheme.typography.button
                )
            }
        }
    }
}

// Duplicate 3
@Composable
private fun LoadingLottie(
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(resId = R.raw.lottie_loading_2)
    )
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = modifier
    )
}