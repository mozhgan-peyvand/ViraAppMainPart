package ai.ivira.app.features.login.ui.mobile

import ai.ivira.app.R
import ai.ivira.app.features.ava_negar.ui.record.widgets.ClickableTextWithDashUnderline
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiIdle
import ai.ivira.app.utils.ui.UiLoading
import ai.ivira.app.utils.ui.UiSuccess
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Card
import ai.ivira.app.utils.ui.theme.Color_Primary
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import android.widget.Toast
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
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
fun LoginMobileRoute(
    navController: NavController
) {
    LoginMobileScreen(
        navigateToOtpScreen = {}
    )
}

@Composable
private fun LoginMobileScreen(
    navigateToOtpScreen: (phoneNumber: String) -> Unit,
    viewModel: LoginMobileViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val uiState by viewModel.uiViewState.collectAsStateWithLifecycle()
    val phoneNumber by viewModel.phoneNumber.collectAsStateWithLifecycle()
    val requestAllowed by viewModel.requestAllowed.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        when (uiState) {
            is UiError -> {
                val message = (uiState as? UiError)?.message
                    ?: context.getString(R.string.msg_there_is_a_problem)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                viewModel.clearUiState()
            }
            UiSuccess -> {
                navigateToOtpScreen(phoneNumber)
            }
            UiIdle,
            UiLoading -> {
                focusManager.clearFocus()
            }
        }
    }

    LoginMobileScreenUI(
        phoneNumber = phoneNumber,
        confirmEnabled = requestAllowed,
        isLoading = uiState is UiLoading,
        scrollState = scrollState,
        focusRequester = focusRequester,
        onPhoneNumberChange = viewModel::changePhoneNumber,
        onConfirmClick = viewModel::sendOTP
    )
}

@Composable
private fun LoginMobileScreenUI(
    phoneNumber: String,
    confirmEnabled: Boolean,
    isLoading: Boolean,
    scrollState: ScrollState,
    focusRequester: FocusRequester,
    onPhoneNumberChange: (String) -> Unit,
    onConfirmClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) { paddingValues ->
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

            PhoneNumberTextField(
                phoneNumber = phoneNumber,
                onPhoneNumberChange = onPhoneNumberChange,
                focusRequester = focusRequester,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            ClickableTextWithDashUnderline(
                textRes = R.string.msg_terms_and_conditions,
                startIndex = 25,
                endIndex = 39,
                textStyle = MaterialTheme.typography.body2,
                substringTextStyle = MaterialTheme.typography.body2.copy(color = Color_Primary),
                onClick = {}, // TODO: Add navigateCallback to TermsAndConditionsScreen here
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            ConfirmButton(
                enabled = confirmEnabled,
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth(),
                onClick = onConfirmClick
            )
        }
    }
}

@Composable
private fun PhoneNumberTextField(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color_Card, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ViraIcon(
            drawable = R.drawable.ic_mobile,
            contentDescription = null,
            tint = Color_Text_3,
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.size(12.dp))

        Column(
            modifier = modifier
                .weight(1f)
                .padding(vertical = 6.dp)
        ) {
            Text(
                text = stringResource(id = R.string.lbl_phone_number),
                color = Color_Text_3,
                style = MaterialTheme.typography.caption
            )

            BasicTextField(
                value = phoneNumber,
                singleLine = true,
                onValueChange = onPhoneNumberChange,
                cursorBrush = SolidColor(MaterialTheme.colors.primary),
                textStyle = MaterialTheme.typography.body1.copy(
                    color = Color_Text_2,
                    fontFamily = FontFamily(
                        Font(ThemeR.font.bahij_helvetica_neue_vira_edition_roman)
                    )
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
        }
    }
}

@Composable
private fun TermsAndConditionsText(
    style: TextStyle,
    onTagClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tag = "TermsAndConditions"
    val fullText = stringResource(id = R.string.msg_terms_and_conditions)

    val annotatedText = buildAnnotatedString {
        withStyle(style = style.toSpanStyle().copy(color = MaterialTheme.colors.onPrimary)) {
            val part1 = fullText.substring(startIndex = 0, endIndex = 25)
            append(part1)
        }

        pushStringAnnotation(
            tag = tag,
            annotation = tag
        )
        withStyle(style = style.toSpanStyle().copy(color = MaterialTheme.colors.primary)) {
            val part2 = fullText.substring(startIndex = 25, endIndex = 39)
            append(part2)
        }

        withStyle(style = style.toSpanStyle().copy(color = MaterialTheme.colors.onPrimary)) {
            val part3 = fullText.substring(startIndex = 39)
            append(part3)
        }

        pop()
    }

    ClickableText(
        text = annotatedText,
        onClick = { offset ->
            annotatedText.getStringAnnotations(
                tag = tag,
                start = offset,
                end = offset
            ).firstOrNull()?.let { _ ->
                onTagClick()
            }
        },
        modifier = modifier
    )
}

// Duplicate 2
@Composable
private fun ConfirmButton(
    onClick: () -> Unit,
    enabled: Boolean,
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
private fun LoginMobileScreenPreview() {
    ViraPreview {
        LoginMobileScreenUI(
            phoneNumber = "(189) 969-1810",
            confirmEnabled = false,
            isLoading = false,
            scrollState = rememberScrollState(),
            focusRequester = FocusRequester(),
            onPhoneNumberChange = {},
            onConfirmClick = {}
        )
    }
}