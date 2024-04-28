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
import ai.ivira.app.utils.ui.theme.Color_Primary_Opacity_15
import ai.ivira.app.utils.ui.theme.Color_Red
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.theme.Cyan_200
import ai.ivira.app.utils.ui.widgets.HorizontalLoadingCircles
import ai.ivira.app.utils.ui.widgets.OtpCodeTextField
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ai.ivira.app.designsystem.theme.R as ThemeR

@Composable
fun LoginOtpRoute(
    navController: NavController,
    mobile: String
) {
    // upon navigating to home, recomposition which is caused by jetpack navigation's
    // animation causes getBackStackEntry to throw exception (cause mobile is removed from stack)
    // so we check if that screen is actually in stack
    val parentEntry = remember(navController.currentBackStackEntry) {
        runCatching {
            navController.getBackStackEntry(LoginScreenRoutes.LoginMobileScreen.route)
        }.getOrNull()
    }

    if (parentEntry != null) {
        LoginOtpScreen(
            navigateToHomeScreen = {
                navController.navigate(route = HomeScreenRoutes.Home.createRoute()) {
                    popUpTo(route = LoginScreenRoutes.LoginMobileScreen.route) { inclusive = true }
                }
            },
            navigateToMobileScreen = { navController.navigateUp() },
            mobile = mobile,
            otpViewModel = hiltViewModel(),
            otpTimerViewModel = hiltViewModel(parentEntry)
        )
    } else {
        Box(modifier = Modifier.fillMaxSize())
    }
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
    val otpIsInvalid by otpViewModel.otpIsInvalid
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
        otpIsInvalid = otpIsInvalid,
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
    otpIsInvalid: Boolean,
    resendOtpViewState: UiStatus,
    timerState: LoginTimerState,
    scrollState: ScrollState,
    otpCodeOnTextChange: (String) -> Unit,
    verifyOtpButtonClick: () -> Unit,
    changMobileAction: () -> Unit,
    sendOtpAgainAction: () -> Unit,
    scaffoldState: ScaffoldState
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(44.dp))

                ViraImage(
                    drawable = R.drawable.ic_app_logo_name_linear,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = stringResource(id = R.string.lbl_otp_status),
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = mobile,
                        style = MaterialTheme.typography.subtitle1.copy(
                            fontFamily = FontFamily(Font(ThemeR.font.bahij_helvetica_neue_vira_edition_roman)),
                            fontWeight = FontWeight(600)
                        )
                    )

                    Spacer(modifier = Modifier.width(15.dp))

                    SectionActionButton(
                        stringRes = R.string.lbl_modification,
                        iconRes = R.drawable.ic_edit,
                        action = changMobileAction
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OtpCodeSection(
                    otp = otpCodeTextValue,
                    onOtpChange = { otpCodeOnTextChange(it) },
                    focusRequester = focusRequester,
                    isError = otpIsInvalid,
                    modifier = Modifier.fillMaxWidth()
                )

                if (timerState == LoginTimerState.End) {
                    Spacer(modifier = Modifier.height(16.dp))

                    TryAgainForOtpButton(
                        isLoading = resendOtpViewState is UiLoading,
                        tryAgainAction = sendOtpAgainAction,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth()
                    )
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                if (timerState is LoginTimerState.Start) {
                    Text(
                        text = buildString {
                            append(stringResource(id = R.string.lbl_reminding_time))
                            append("       ")
                            append(formatDuration(timerState.currentTime))
                        },
                        style = MaterialTheme.typography.body2.copy(
                            fontFamily = FontFamily(
                                Font(ThemeR.font.bahij_helvetica_neue_vira_edition_roman)
                            )
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                ConfirmButton(
                    isLoading = isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = verifyOtpButtonClick
                )
            }
        }
    }
}

// Duplicate 2
@Composable
private fun SectionActionButton(
    @StringRes stringRes: Int,
    @DrawableRes iconRes: Int,
    action: () -> Unit,
    modifier: Modifier = Modifier,
    verticalPadding: Dp = 8.dp,
    horizontalPadding: Dp = 0.dp
) {
    Row(
        modifier = modifier
            .sizeIn(minWidth = 80.dp, minHeight = 32.dp)
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = Color_Primary_Opacity_15)
            .clickable {
                safeClick(event = action)
            }
            .padding(vertical = verticalPadding, horizontal = horizontalPadding)
            .padding(start = 8.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ViraIcon(
            drawable = iconRes,
            tint = Cyan_200,
            contentDescription = null
        )

        Text(
            text = stringResource(id = stringRes),
            style = MaterialTheme.typography.overline.copy(color = Cyan_200)
        )
    }
}

@Composable
private fun TryAgainForOtpButton(
    isLoading: Boolean,
    tryAgainAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var loadingHeight by rememberSaveable { mutableIntStateOf(0) }
    var loadingWidth by rememberSaveable { mutableIntStateOf(0) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .sizeIn(minWidth = 80.dp, minHeight = 40.dp)
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = Color_Primary_Opacity_15)
            .clickable(
                enabled = !isLoading,
                onClick = {
                    safeClick(event = tryAgainAction)
                }
            )
            .padding(vertical = 10.dp, horizontal = 70.dp)
    ) {
        if (isLoading) {
            HorizontalLoadingCircles(
                radius = 8,
                count = 3,
                padding = 15,
                color = Cyan_200,
                modifier = Modifier
                    .height(with(density) { loadingHeight.toDp() })
                    .width(with(density) { loadingWidth.toDp() })
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.onGloballyPositioned {
                    loadingHeight = it.size.height
                    loadingWidth = it.size.width
                }
            ) {
                ViraIcon(
                    drawable = R.drawable.ic_retry,
                    tint = Cyan_200,
                    contentDescription = null
                )

                Text(
                    text = stringResource(id = R.string.lbl_otp_send_again),
                    style = MaterialTheme.typography.overline.copy(color = Cyan_200)
                )
            }
        }
    }
}

@Composable
private fun OtpCodeSection(
    otp: String,
    isError: Boolean,
    onOtpChange: (String) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
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

        Spacer(modifier = Modifier.height(8.dp))

        if (isError) {
            Text(
                text = stringResource(id = R.string.msg_otp_code_error),
                color = Color_Red,
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Composable
private fun ConfirmButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var loading by rememberSaveable { mutableIntStateOf(0) }

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
            HorizontalLoadingCircles(
                radius = 8,
                count = 3,
                padding = 15,
                color = Color_White,
                modifier = Modifier.height(with(density) { loading.toDp() })
            )
        } else {
            Text(
                text = stringResource(id = R.string.lbl_accept),
                style = MaterialTheme.typography.button,
                color = Color_Text_1,
                modifier = Modifier.onGloballyPositioned {
                    loading = it.size.height
                }
            )
        }
    }
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
            scaffoldState = rememberScaffoldState(),
            otpIsInvalid = false
        )
    }
}