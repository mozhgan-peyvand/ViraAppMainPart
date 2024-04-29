package ai.ivira.app.features.login.ui.mobile

import ai.ivira.app.R
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiIdle
import ai.ivira.app.utils.ui.UiLoading
import ai.ivira.app.utils.ui.UiSuccess
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Primary_300
import ai.ivira.app.utils.ui.theme.Color_Primary_Opacity_15
import ai.ivira.app.utils.ui.theme.Color_Red_Opacity_15
import ai.ivira.app.utils.ui.widgets.HorizontalLoadingCircles
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ChangeUserConfirmationBottomSheet(
    viewModel: ChangeUserConfirmationViewModel,
    cancelAction: () -> Unit,
    onSuccessCallback: () -> Unit,
    onErrorCallback: (UiError) -> Unit
) {
    val uiState by viewModel.uiViewState.collectAsStateWithLifecycle(UiIdle)

    val density = LocalDensity.current
    var loadingHeight by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.uiViewState.collect { uiState ->
            when (uiState) {
                is UiError -> onErrorCallback(uiState)
                UiSuccess -> onSuccessCallback()
                UiIdle,
                UiLoading -> Unit
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(id = R.string.lbl_change_account),
            style = MaterialTheme.typography.h6
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.msg_change_account),
            style = MaterialTheme.typography.body1
        )

        Spacer(modifier = Modifier.height(28.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                contentPadding = PaddingValues(14.dp),
                elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                onClick = {
                    safeClick {
                        viewModel.cleanPreviousUserData()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color_Primary_Opacity_15,
                    contentColor = Color_Primary_300
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (uiState is UiLoading) {
                    HorizontalLoadingCircles(
                        radius = 10,
                        count = 3,
                        padding = 15,
                        color = Color_Primary_300,
                        modifier = Modifier.height(with(density) { loadingHeight.toDp() })
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.lbl_accept),
                        style = MaterialTheme.typography.button
                    )
                }
            }

            Spacer(modifier = Modifier.size(8.dp))

            Button(
                contentPadding = PaddingValues(14.dp),
                elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                onClick = {
                    safeClick {
                        viewModel.resetCleanPreviousUserDataRequest()
                        cancelAction()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color_Red_Opacity_15,
                    contentColor = MaterialTheme.colors.onError
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(id = R.string.lbl_cancel),
                    style = MaterialTheme.typography.button,
                    modifier = Modifier.onGloballyPositioned {
                        loadingHeight = it.size.height
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewChangeUserConfirmationBottomSheet() {
    ViraPreview {
        ChangeUserConfirmationBottomSheet(
            cancelAction = {},
            onErrorCallback = {},
            onSuccessCallback = {},
            viewModel = hiltViewModel()
        )
    }
}