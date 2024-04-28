package ai.ivira.app.features.login.ui.otp

import ai.ivira.app.R
import ai.ivira.app.features.home.ui.HomeScreenRoutes
import ai.ivira.app.features.login.ui.LoginScreenRoutes
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiIdle
import ai.ivira.app.utils.ui.UiLoading
import ai.ivira.app.utils.ui.UiStatus
import ai.ivira.app.utils.ui.UiSuccess
import ai.ivira.app.utils.ui.formatDuration
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Primary
import ai.ivira.app.utils.ui.theme.Color_Primary_200
import ai.ivira.app.utils.ui.theme.Color_Primary_Opacity_15
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.widgets.OtpCodeTextField
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
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
    val parentEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(LoginScreenRoutes.LoginMobileScreen.route)
    }
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
        otpViewModel = hiltViewModel(),
        otpTimerViewModel = hiltViewModel(parentEntry)
    )
}

@Composable
private fun LoginOtpScreen(
    navigateToHomeScreen: () -> Unit,
    navigateToMobileScreen: () -> Unit,
    mobile: String,
    otpViewModel: LoginOtpViewModel,
    otpTimerViewModel: OtpTimerSharedViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
    val scrollState = rememberScrollState()
    val uiState by otpViewModel.uiViewState.collectAsStateWithLifecycle()
    val resendOtpViewState by otpViewModel.resendOtpViewState.collectAsStateWithLifecycle(
        initialValue = UiIdle
    )
    val timerState by otpTimerViewModel.timerState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    BackHandler {} // Disabling back click

    DisposableEffect(Unit) {
        onDispose {
            otpTimerViewModel.saveTimerToSharePref()
        }
    }

    LaunchedEffect(Unit) {
        otpViewModel.resendOtpViewState.collect { state ->
            when (state) {
                UiSuccess -> {
                    otpTimerViewModel.startTimer()
                }
                UiIdle,
                UiLoading -> {
                    focusManager.clearFocus()
                }
                is UiError -> {
                    // TODO: show snackBar
                    val message = (state as? UiError)?.message
                        ?: context.getString(R.string.msg_there_is_a_problem)
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

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
                otpViewModel.clearUiState()
            }
            UiSuccess -> {
                navigateToHomeScreen()
            }
        }
    }

    LoginOtpScreenUI(
        scaffoldState = scaffoldState,
        otpCodeTextValue = otpViewModel.otpTextValue,
        mobile = mobile,
        isLoading = uiState is UiLoading,
        resendOtpViewState = resendOtpViewState,
        timerState = timerState,
        scrollState = scrollState,
        focusRequester = focusRequester,
        otpCodeOnTextChange = { newText ->
            otpViewModel.changeOtp(newText)
        },
        verifyOtpButtonClick = otpViewModel::verifyOtpRequest,
        changMobileAction = navigateToMobileScreen,
        sendOtpAgainAction = otpViewModel::resendOtp
    )
}

@Composable
private fun LoginOtpScreenUI(
    otpCodeTextValue: String,
    mobile: String,
    focusRequester: FocusRequester,
    isLoading: Boolean,
    resendOtpViewState: UiStatus,
    timerState: LoginTimerState,
    scrollState: ScrollState,
    otpCodeOnTextChange: (String) -> Unit,
    verifyOtpButtonClick: () -> Unit,
    changMobileAction: () -> Unit,
    sendOtpAgainAction: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        scaffoldState = scaffoldState
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState),
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
            OtpCodeSection(
                otp = otpCodeTextValue,
                onOtpChange = { otpCodeOnTextChange(it) },
                focusRequester = focusRequester,
                isError = false, // TODO: Add isError logic
                modifier = Modifier.fillMaxWidth()
            )

            when (timerState) {
                is LoginTimerState.Start -> {
                    Spacer(modifier = Modifier.weight(1f))
                    TimerContent(
                        time = buildString {
                            append(stringResource(id = R.string.lbl_reminding_time))
                            append("       ")
                            append(formatDuration(timerState.currentTime))
                        }
                    )
                }
                LoginTimerState.End -> {
                    TryAgainForOtpButton(
                        isLoading = resendOtpViewState is UiLoading,
                        sendOtpAgainAction = sendOtpAgainAction
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
                LoginTimerState.NotStart -> {}
            }
            ConfirmButton(
                isLoading = isLoading,
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = verifyOtpButtonClick
            )
        }
    }
}

// Duplicate 1
@Composable
private fun TimerContent(time: String, modifier: Modifier = Modifier) {
    Text(
        text = time,
        style = MaterialTheme.typography.body2.copy(
            fontFamily = FontFamily(
                Font(ThemeR.font.bahij_helvetica_neue_vira_edition_roman)
            )
        ),
        color = Color_Text_3,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 26.dp)
    )
}

@Composable
private fun TryAgainForOtpButton(
    isLoading: Boolean,
    sendOtpAgainAction: () -> Unit
) {
    val density = LocalDensity.current
    var lottieHeight by rememberSaveable { mutableIntStateOf(0) }

    Button(
        modifier = Modifier
            .padding(top = 15.dp, start = 22.dp, end = 22.dp)
            .defaultMinSize(minHeight = 36.dp),
        contentPadding = PaddingValues(vertical = 14.dp),
        onClick = { if (!isLoading) safeClick { sendOtpAgainAction() } },
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color_Primary_Opacity_15,
            contentColor = Color_Primary_200
        )
    ) {
        if (isLoading) {
            LoadingLottie(
                modifier = Modifier.height(with(density) { lottieHeight.toDp() })
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 9.dp)
                    .onGloballyPositioned {
                        lottieHeight = it.size.height
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                ViraIcon(
                    drawable = R.drawable.ic_retry,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 10.dp)
                )
                Text(
                    text = stringResource(id = R.string.lbl_otp_send_again),
                    style = MaterialTheme.typography.overline,
                    color = Color_Primary_200
                )
            }
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
private fun OtpCodeSection(
    otp: String,
    isError: Boolean,
    onOtpChange: (String) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.lbl_otp_enter_code),
            color = Color_Text_1,
            style = MaterialTheme.typography.body1
        )

        Spacer(modifier = Modifier.height(12.dp))

        OtpCodeTextField(
            otp = otp,
            isError = isError,
            onOtpChange = onOtpChange,
            focusRequester = focusRequester,
            otpSize = LoginOtpViewModel.OTP_SIZE
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
            resendOtpViewState = UiIdle,
            scrollState = rememberScrollState(),
            sendOtpAgainAction = {},
            timerState = LoginTimerState.End,
            focusRequester = FocusRequester(),
            otpCodeOnTextChange = {},
            verifyOtpButtonClick = {},
            changMobileAction = {},
            scaffoldState = rememberScaffoldState()
        )
    }
}