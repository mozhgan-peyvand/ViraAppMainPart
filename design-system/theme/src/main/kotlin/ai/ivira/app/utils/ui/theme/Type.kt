package ai.ivira.app.utils.ui.theme

import ai.ivira.app.designsystem.theme.R
import androidx.compose.material.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// TODO: after checking all text in app, set includeFontPadding=false
// Set of Material typography styles to start with
val Typography = Typography(
    h1 = TextStyle(
        fontSize = 57.sp,
        fontWeight = FontWeight(500),
        fontFamily = FontFamily(Font(R.font.bahij_thesansarabic_semibold)),
        lineHeight = 64.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),
    h2 = TextStyle(
        fontSize = 45.sp,
        fontWeight = FontWeight(500),
        fontStyle = FontStyle.Normal,
        fontFamily = FontFamily(Font(R.font.bahij_thesansarabic_semibold)),
        lineHeight = 52.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),
    h3 = TextStyle(
        fontSize = 36.sp,
        fontWeight = FontWeight(500),
        fontStyle = FontStyle.Normal,
        fontFamily = FontFamily(Font(R.font.bahij_thesansarabic_semibold)),
        lineHeight = 44.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),
    h4 = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight(400),
        fontStyle = FontStyle.Normal,
        fontFamily = FontFamily(Font(R.font.bahij_thesansarabic_plain)),
        lineHeight = 40.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),
    h5 = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight(400),
        fontStyle = FontStyle.Normal,
        fontFamily = FontFamily(Font(R.font.bahij_thesansarabic_plain)),
        lineHeight = 32.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),
    h6 = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight(400),
        fontStyle = FontStyle.Normal,
        fontFamily = FontFamily(Font(R.font.bahij_thesansarabic_plain)),
        lineHeight = 28.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),
    subtitle1 = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight(600),
        fontStyle = FontStyle.Normal,
        fontFamily = FontFamily(Font(R.font.bahij_thesansarabic_bold)),
        lineHeight = 24.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),
    subtitle2 = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight(600),
        fontStyle = FontStyle.Normal,
        fontFamily = FontFamily(Font(R.font.bahij_thesansarabic_bold)),
        lineHeight = 20.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),
    body1 = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight(400),
        fontStyle = FontStyle.Normal,
        fontFamily = FontFamily(Font(R.font.bahij_helvetica_neue_roman)),
        lineHeight = 24.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),
    body2 = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight(400),
        fontStyle = FontStyle.Normal,
        fontFamily = FontFamily(Font(R.font.bahij_helvetica_neue_roman)),
        lineHeight = 20.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),
    button = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight(600),
        fontStyle = FontStyle.Normal,
        fontFamily = FontFamily(Font(R.font.bahij_thesansarabic_bold)),
        lineHeight = 20.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),
    caption = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight(400),
        fontStyle = FontStyle.Normal,
        fontFamily = FontFamily(
            Font(R.font.bahij_helvetica_neue_vira_edition_roman)
        ), // used also in record
        lineHeight = 16.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    ),
    overline = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight(600),
        fontStyle = FontStyle.Normal,
        fontFamily = FontFamily(Font(R.font.bahij_thesansarabic_bold)),
        lineHeight = 16.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    )
)

val Typography.labelMedium: TextStyle
    get() = overline.copy(
        fontSize = 12.sp,
        fontFamily = FontFamily(Font(R.font.bahij_thesansarabic_plain)),
        fontWeight = FontWeight(400),
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    )

/*
displayLarge -> h1
displayMedium -> h2
displaySmall -> h3
headLineLarge -> N/A
headlineMedium -> h4
headlineSmall -> h5
titleLarge -> h6
titleMedium -> subtitle1
titleSmall -> subtitle2
bodyLarge -> body1
bodyMedium -> body2
bodySmall -> caption
labelLarge -> button
labelMedium -> N/A
labelSmall -> overline
* */