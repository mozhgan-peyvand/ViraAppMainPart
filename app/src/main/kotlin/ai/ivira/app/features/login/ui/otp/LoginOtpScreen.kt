package ai.ivira.app.features.login.ui.otp

import ai.ivira.app.R
import ai.ivira.app.features.home.ui.HomeScreenRoutes
import ai.ivira.app.features.login.ui.LoginScreenRoutes
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiIdle
import ai.ivira.app.utils.ui.UiLoading
import ai.ivira.app.utils.ui.UiSuccess
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Card
import ai.ivira.app.utils.ui.theme.Color_Primary
import ai.ivira.app.utils.ui.theme.Color_Primary_200
import ai.ivira.app.utils.ui.theme.Color_Primary_Opacity_15
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import ai.ivira.app.designsystem.theme.R as ThemeR

@Composable
fun LoginOtpRoute(
    navController: NavController,
    mobile: String
) {
    LoginOtpScreen(
        navigateToHomeScreen = {
            navController.navigate(
                route = HomeScreenRoutes.Home.createRoute()
            ) {
                popUpTo(route = LoginScreenRoutes.LoginMobileScreen.route) { inclusive = true }
            }
        },
        navigateToMobileScreen = { navController.navigateUp() },
        mobile = mobile,
        viewModel = hiltViewModel()
    )
}

@Composable
private fun LoginOtpScreen(
    navigateToHomeScreen: () -> Unit,
    navigateToMobileScreen: () -> Unit,
    mobile: String,
    viewModel: LoginOtpViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
    val uiState by viewModel.uiViewState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    BackHandler {} // Disabling back click

    LaunchedEffect(key1 = uiState) {
        when (uiState) {
            UiIdle,
            UiLoading -> {
                focusManager.clearFocus()
            }
            is UiError -> {
                val message = (uiState as? UiError)?.message
                    ?: context.getString(R.string.msg_there_is_a_problem)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                viewModel.clearUiState()
            }
            UiSuccess -> {
                navigateToHomeScreen()
            }
        }
    }

    LoginOtpScreenUI(
        scaffoldState = scaffoldState,
        otpCodeTextValue = viewModel.otpTextValue,
        mobile = mobile,
        isLoading = uiState is UiLoading,
        focusRequester = focusRequester,
        otpCodeOnTextChange = { newText ->
            viewModel.changeOtp(newText)
        },
        verifyOtpButtonClick = viewModel::sendOtpRequest,
        changMobileAction = navigateToMobileScreen
    )
}

@Composable
private fun LoginOtpScreenUI(
    otpCodeTextValue: String,
    mobile: String,
    focusRequester: FocusRequester,
    isLoading: Boolean,
    otpCodeOnTextChange: (String) -> Unit,
    verifyOtpButtonClick: () -> Unit,
    changMobileAction: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        scaffoldState = scaffoldState
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ViraImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth()
                        .padding(top = 44.dp),
                    drawable = R.drawable.ic_app_logo_name_linear,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.lbl_otp_status),
                    style = MaterialTheme.typography.body1, color = Color_Text_1,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = modifier,
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = mobile,
                        style = MaterialTheme.typography.subtitle1,
                        color = Color_Text_1,
                        modifier = Modifier.padding(end = 15.dp)
                    )
                    Button(
                        onClick = {
                            safeClick {
                                changMobileAction()
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color_Primary_Opacity_15,
                            contentColor = Color_Primary_200
                        )
                    ) {
                        Row(
                            modifier = modifier,
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ViraIcon(
                                drawable = R.drawable.ic_edit,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 15.dp)
                            )
                            Text(
                                text = stringResource(id = R.string.lbl_modification),
                                style = MaterialTheme.typography.subtitle1
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(42.dp))
                OtpTextField(
                    otp = otpCodeTextValue,
                    onOtpChange = { otpCodeOnTextChange(it) },
                    focusRequester = focusRequester
                )
            }
            ConfirmButton(
                isLoading = isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                onClick = verifyOtpButtonClick
            )
        }
    }
}

// Duplicate 3
@Composable
private fun ConfirmButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var lottieHeight by rememberSaveable { mutableIntStateOf(0) }

    Button(
        contentPadding = PaddingValues(vertical = 14.dp),
        onClick = {
            safeClick(event = onClick)
        },
        colors = ButtonDefaults.buttonColors(
            disabledBackgroundColor = if (!isLoading) {
                MaterialTheme.colors.onSurface
                    .copy(alpha = 0.12f)
                    .compositeOver(MaterialTheme.colors.surface)
            } else {
                Color_Primary
            }
        ),
        modifier = modifier.padding(bottom = 20.dp)
    ) {
        if (isLoading) {
            LoadingLottie(
                modifier = Modifier.height(with(density) { lottieHeight.toDp() })
            )
        } else {
            Text(
                text = stringResource(id = R.string.lbl_accept),
                style = MaterialTheme.typography.button,
                color = Color_Text_1,
                modifier = Modifier.onGloballyPositioned {
                    lottieHeight = it.size.height
                }
            )
        }
    }
}

@Composable
private fun OtpTextField(
    otp: String,
    onOtpChange: (String) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color_Card, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.lbl_otp_enter_code),
            color = Color_Text_3,
            style = MaterialTheme.typography.caption
        )
        BasicTextField(
            value = otp,
            singleLine = true,
            onValueChange = onOtpChange,
            cursorBrush = SolidColor(MaterialTheme.colors.primary),
            textStyle = MaterialTheme.typography.body1.copy(
                color = Color_Text_2,
                fontFamily = FontFamily(
                    Font(ThemeR.font.bahij_helvetica_neue_vira_edition_roman)
                )
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )
    }
}

// Duplicate 2
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

@ViraDarkPreview
@Composable
private fun LoginOtpScreenPreview() {
    ViraPreview {
        LoginOtpScreenUI(
            otpCodeTextValue = "",
            mobile = "",
            isLoading = false,
            focusRequester = FocusRequester(),
            otpCodeOnTextChange = {},
            verifyOtpButtonClick = {},
            changMobileAction = {},
            scaffoldState = rememberScaffoldState()
        )
    }
}