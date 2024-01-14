package ai.ivira.app.features.imazh.data

import ai.ivira.app.R
import androidx.annotation.DrawableRes

enum class ImazhImageStyle(
    val key: String,
    val viewName: String,
    @DrawableRes val iconRes: Int
) {
    None(key = "none", viewName = "هیچکدام", iconRes = R.drawable.img_style_none),
    Cinematic(key = "cinematic", viewName = "واقعی", iconRes = R.drawable.img_style_cinematic),
    DigitalArt(
        key = "digital_art",
        viewName = "دیجیتال آرت",
        iconRes = R.drawable.img_style_digital_art
    ),
    Comic(key = "comic", viewName = "کُمیک", iconRes = R.drawable.img_style_comic),
    Anime(key = "anime", viewName = "اَنیمه", iconRes = R.drawable.img_style_anime),
    Origami(key = "origami", viewName = "کاغذی", iconRes = R.drawable.img_style_origami),
    ThreeD(key = "3d", viewName = "۳بُعدی", iconRes = R.drawable.img_style_three_d),
    Watercolor(key = "watercolor", viewName = "آبرنگ", iconRes = R.drawable.img_style_watercolor),
    Pencil(key = "pencil", viewName = "سیاه\u200Cقلم", iconRes = R.drawable.img_style_pencil),
    PixelArt(key = "pixel_art", viewName = "پیکسلی", iconRes = R.drawable.img_style_pixelart),
    LowPoly(key = "lowpoly", viewName = "لوپلی", iconRes = R.drawable.img_style_lowpoly)
}