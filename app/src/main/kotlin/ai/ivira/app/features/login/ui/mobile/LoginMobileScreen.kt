package ai.ivira.app.features.login.ui.mobile

import ai.ivira.app.R
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheet
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetContent
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetDefaults
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetState
import ai.ivira.app.designsystem.bottomsheet.rememberViraBottomSheetState
import ai.ivira.app.designsystem.components.textfield.ViraOutlinedTextField
import ai.ivira.app.designsystem.components.textfield.ViraOutlinedTextFieldDefaults.DefaultHelperIcon
import ai.ivira.app.designsystem.components.textfield.ViraOutlinedTextFieldDefaults.DefaultHelperText
import ai.ivira.app.designsystem.components.textfield.ViraOutlinedTextFieldDefaults.DefaultLabel
import ai.ivira.app.designsystem.components.textfield.ViraOutlinedTextFieldDefaults.DefaultLeadingIcon
import ai.ivira.app.designsystem.components.textfield.ViraOutlinedTextFieldDefaults.DefaultPlaceholder
import ai.ivira.app.features.ava_negar.ui.SnackBarWithPaddingBottom
import ai.ivira.app.features.ava_negar.ui.record.widgets.ClickableTextWithDashUnderline
import ai.ivira.app.features.home.ui.HomeScreenRoutes
import ai.ivira.app.features.login.ui.LoginScreenRoutes
import ai.ivira.app.features.login.ui.mobile.LoginMobileBottomSheetType.LoginRequired
import ai.ivira.app.features.login.ui.otp.LoginTimerState
import ai.ivira.app.features.login.ui.otp.OtpTimerSharedViewModel
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiIdle
import ai.ivira.app.utils.ui.UiLoading
import ai.ivira.app.utils.ui.UiSuccess
import ai.ivira.app.utils.ui.formatDuration
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.showMessage
import ai.ivira.app.utils.ui.theme.Color_Primary
import ai.ivira.app.utils.ui.theme.Color_Primary_300
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.widgets.HorizontalLoadingCircles
import ai.ivira.app.utils.ui.widgets.ViraImage
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.input.InputTransformation
import androidx.compose.foundation.text2.input.TextFieldBuffer
import androidx.compose.foundation.text2.input.TextFieldCharSequence
import androidx.compose.foundation.text2.input.TextFieldLineLimits
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.rememberTextFieldState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ai.ivira.app.designsystem.theme.R as ThemeR

@Composable
fun LoginMobileRoute(
    navController: NavController,
    fromSplash: Boolean
) {
    // we need the current to not be empty
    val parentEntry = navController.currentBackStackEntry!!
    LoginMobileScreen(
        navigateToOtpScreen = {
            navController.navigate(LoginScreenRoutes.LoginOtpScreen.createRoute(it))
        },
        navigateToTermsOfServiceScreen = {
            navController.navigate(HomeScreenRoutes.TermsOfServiceScreen.route)
        },
        fromSplash = fromSplash,
        mobileViewModel = hiltViewModel(),
        otpTimerViewModel = hiltViewModel(parentEntry)
    )
}

@Composable
private fun LoginMobileScreen(
    fromSplash: Boolean,
    mobileViewModel: LoginMobileViewModel,
    otpTimerViewModel: OtpTimerSharedViewModel,
    navigateToOtpScreen: (phoneNumber: String) -> Unit,
    navigateToTermsOfServiceScreen: () -> Unit
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val snackBarState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackBarState)
    val coroutineScope = rememberCoroutineScope()
    val uiState by mobileViewModel.uiViewState.collectAsStateWithLifecycle(UiIdle)
    val isRequestAllowed by mobileViewModel.isRequestAllowed.collectAsStateWithLifecycle(uiState !is UiLoading)

    val phoneNumber = mobileViewModel.phoneNumber

    var selectedSheet by rememberSaveable { mutableStateOf(LoginRequired) }
    val sheetState = rememberViraBottomSheetState()
    val loginRequiredIsShown by mobileViewModel.loginRequiredIsShown
    val timerState by otpTimerViewModel.timerState.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        otpTimerViewModel.checkTimerFromSharePref()
        onDispose {
            otpTimerViewModel.saveTimerToSharePref()
        }
    }

    LaunchedEffect(loginRequiredIsShown, fromSplash) {
        if (!fromSplash) {
            mobileViewModel.setLoginRequiredShowed()
        } else if (!loginRequiredIsShown) {
            selectedSheet = LoginRequired
            sheetState.show()
        }
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is UiError -> {
                if (state.isSnack) {
                    showMessage(
                        snackBarState,
                        coroutineScope,
                        state.message
                    )
                }
            }
            UiSuccess -> {
                navigateToOtpScreen(phoneNumber.text.toString())
                otpTimerViewModel.startTimer()
            }
            UiLoading -> {
                focusManager.clearFocus()
            }
            UiIdle -> {}
        }
    }

    LoginMobileScreenUI(
        phoneNumberTextState = phoneNumber,
        isRequestAllowed = (isRequestAllowed) && (timerState !is LoginTimerState.Start),
        isLoading = uiState is UiLoading,
        timerState = timerState,
        scrollState = scrollState,
        isError = uiState is UiError,
        onConfirmClick = mobileViewModel::sendOTP,
        onTermsOfServiceClick = navigateToTermsOfServiceScreen,
        scaffoldState = scaffoldState,
        snackBarState = snackBarState,
        sheetState = sheetState,
        selectedSheet = selectedSheet,
        onLoginRequiredConfirmed = mobileViewModel::setLoginRequiredShowed
    )
}

@Composable
private fun LoginMobileScreenUI(
    phoneNumberTextState: TextFieldState,
    isRequestAllowed: Boolean,
    isLoading: Boolean,
    timerState: LoginTimerState,
    scrollState: ScrollState,
    isError: Boolean,
    selectedSheet: LoginMobileBottomSheetType,
    scaffoldState: ScaffoldState,
    snackBarState: SnackbarHostState,
    onConfirmClick: () -> Unit,
    onTermsOfServiceClick: () -> Unit,
    sheetState: ViraBottomSheetState,
    onLoginRequiredConfirmed: () -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackBarWithPaddingBottom(
                snackbarHostState = snackBarState,
                shouldShowOverItems = true,
                paddingValue = 450f
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) { paddingValues ->

        val helperIcon: @Composable () -> Unit = remember {
            {
                DefaultHelperIcon(drawable = R.drawable.ic_error_circle)
            }
        }

        val helperText: @Composable () -> Unit = remember(isError) {
            {
                DefaultHelperText(R.string.msg_error_phone_number_validation)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
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
                text = stringResource(id = R.string.lbl_login_signup_title),
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth()
            )

            Spacer(modifier = Modifier.height(50.dp))

            ViraOutlinedTextField(
                state = phoneNumberTextState,
                inputTransformation = PhoneNumberTransformation,
                lineLimits = TextFieldLineLimits.SingleLine,
                cursorBrush = SolidColor(Color_Text_2),
                isError = isError,
                label = { DefaultLabel(R.string.lbl_phone_number) },
                placeholder = { DefaultPlaceholder(R.string.lbl_phone_number_placeholder) },
                leadingIcon = { DefaultLeadingIcon(R.drawable.ic_mobile) },
                helperIcon = if (isError) helperIcon else null,
                helperText = if (isError) helperText else null,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            when (timerState) {
                is LoginTimerState.Start -> {
                    TimerContent(
                        time = buildString {
                            append(stringResource(id = R.string.lbl_reminding_time))
                            append("       ")
                            append(formatDuration(timerState.currentTime))
                        }
                    )
                }
                else -> {}
            }

            ClickableTextWithDashUnderline(
                textRes = R.string.msg_terms_and_conditions,
                startIndex = 25,
                endIndex = 39,
                textStyle = MaterialTheme.typography.body2,
                substringTextStyle = MaterialTheme.typography.body2.copy(color = Color_Primary),
                onClick = onTermsOfServiceClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            ConfirmButton(
                enabled = isRequestAllowed,
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth(),
                onClick = onConfirmClick
            )
        }
    }
    if (sheetState.showBottomSheet) {
        ViraBottomSheet(
            sheetState = sheetState,
            isDismissibleOnDrag = false,
            isDismissibleOnTouchOutside = false,
            properties = ViraBottomSheetDefaults.properties(shouldDismissOnBackPress = false)
        ) {
            ViraBottomSheetContent(targetState = selectedSheet) {
                when (selectedSheet) {
                    LoginRequired -> {
                        LoginRequiredBottomSheet(
                            onConfirmClick = {
                                onLoginRequiredConfirmed()
                                sheetState.hide()
                            }
                        )
                    }
                }
            }
        }
    }
}

// Duplicate 2
@Composable
private fun TimerContent(time: String) {
    Text(
        text = time,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        style = MaterialTheme.typography.body2.copy(
            fontFamily = FontFamily(
                Font(ThemeR.font.bahij_helvetica_neue_vira_edition_roman)
            )
        ),
        color = Color_Text_3,
        textAlign = TextAlign.Center
    )
}

private object PhoneNumberTransformation : InputTransformation {
    override val keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
    override fun transformInput(
        originalValue: TextFieldCharSequence,
        valueWithChanges: TextFieldBuffer
    ) {
        if (!valueWithChanges.asCharSequence().isDigitsOnly() || valueWithChanges.length > 11) {
            valueWithChanges.revertAllChanges()
        }
    }
}

// Duplicate 2
@Composable
private fun ConfirmButton(
    enabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
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
            disabledBackgroundColor = if (!enabled && !isLoading) {
                MaterialTheme.colors.onSurface
                    .copy(alpha = 0.12f)
                    .compositeOver(MaterialTheme.colors.surface)
            } else {
                Color_Primary
            }
        ),
        enabled = enabled,
        modifier = modifier.padding(bottom = 20.dp)
    ) {
        if (isLoading) {
            HorizontalLoadingCircles(
                radius = 10,
                count = 3,
                padding = 15,
                color = Color_Primary_300,
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
private fun LoginMobileScreenPreview() {
    ViraPreview {
        LoginMobileScreenUI(
            phoneNumberTextState = rememberTextFieldState(),
            isRequestAllowed = false,
            isLoading = false,
            timerState = LoginTimerState.End,
            scrollState = rememberScrollState(),
            isError = false,
            scaffoldState = rememberScaffoldState(),
            snackBarState = SnackbarHostState(),
            onConfirmClick = {},
            onTermsOfServiceClick = {},
            selectedSheet = LoginRequired,
            sheetState = rememberViraBottomSheetState(),
            onLoginRequiredConfirmed = {}
        )
    }
}