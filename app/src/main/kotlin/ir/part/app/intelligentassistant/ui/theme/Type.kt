package ir.part.app.intelligentassistant.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ir.part.app.intelligentassistant.R

// Set of Material typography styles to start with
val Typography = Typography(
    h1 = TextStyle(
        fontSize = 57.sp,
        fontWeight = FontWeight(500),
        fontFamily = FontFamily(Font(R.font.bahij_thesansarabic_semibold)),
        lineHeight = 64.sp
    ),
    h2 = TextStyle(
        fontSize = 45.sp,
        fontWeight = FontWeight(500),
        fontStyle = FontStyle.Normal,
        fontFamily = FontFamily(Font(R.font.bahij_thesansarabic_semibold)),
        lineHeight = 52.sp
    ),
    h3 = TextStyle(
        fontSize = 36.sp,
        fontWeight = FontWeight(500),
        fontStyle = FontStyle.Normal,
        fontFamily = FontFamily(Font(R.font.bahij_thesansarabic_semibold)),
        lineHeight = 44.sp
    ),
    h4 = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight(400),
        fontStyle = FontStyle.Normal,
        fontFamily = FontFamily(Font(R.font.bahij_thesansarabic_plain)),
        lineHeight = 40.sp
    ),
    h5 = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight(400),
        fontStyle = FontStyle.Normal,
        fontFamily = FontFamily(Font(R.font.bahij_thesansarabic_plain)),
        lineHeight = 32.sp
    ),
    h6 = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight(400),
        fontStyle = FontStyle.Normal,
        fontFamily = FontFamily(Font(R.font.bahij_thesansarabic_plain)),
        lineHeight = 28.sp
    ),
    subtitle1 = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight(600),
        fontStyle = FontStyle.Normal,
        fontFamily = FontFamily(Font(R.font.bahij_thesansarabic_bold)),
        lineHeight = 24.sp
    ),
    subtitle2 = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight(600),
        fontStyle = FontStyle.Normal,
        fontFamily = FontFamily(Font(R.font.bahij_thesansarabic_bold)),
        lineHeight = 20.sp
    ),
    body1 = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight(400),
        fontStyle = FontStyle.Normal,
        fontFamily = FontFamily(Font(R.font.bahij_helvetica_neue_roman)),
        lineHeight = 24.sp
    ),
    body2 = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight(400),
        fontStyle = FontStyle.Normal,
        fontFamily = FontFamily(Font(R.font.bahij_helvetica_neue_roman)),
        lineHeight = 20.sp
    ),
    button = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight(600),
        fontStyle = FontStyle.Normal,
        fontFamily = FontFamily(Font(R.font.bahij_thesansarabic_bold)),
        lineHeight = 20.sp
    ),
    caption = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight(400),
        fontStyle = FontStyle.Normal,
        fontFamily = FontFamily(Font(R.font.bahij_helvetica_neue_roman)),
        lineHeight = 16.sp
    ),
    overline = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight(600),
        fontStyle = FontStyle.Normal,
        fontFamily = FontFamily(Font(R.font.bahij_thesansarabic_bold)),
        lineHeight = 16.sp
    )
)

val Typography.labelMedium: TextStyle
    get() = overline.copy(
        fontSize = 12.sp,
        fontFamily = FontFamily(Font(R.font.bahij_thesansarabic_plain)),
        fontWeight = FontWeight(400)
    )
