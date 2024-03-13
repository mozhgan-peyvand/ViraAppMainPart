package ai.ivira.app.features.hamahang.ui.new_audio

import ai.ivira.app.R
import androidx.annotation.DrawableRes

enum class HamahangSpeakerView(
    val viewName: String,
    @DrawableRes val iconRes: Int
) {
    Khiabani(viewName = "خیابانی", iconRes = R.drawable.img_speaker_khiabani),
    FerdowsiPour(viewName = "فردوسی پور", iconRes = R.drawable.img_speaker_ferdowsi_pour),
    Modiri(viewName = "مدیری", iconRes = R.drawable.img_speaker_modiri),
    Hatami(viewName = "حاتمی", iconRes = R.drawable.img_speaker_hatami),
    Chavoshi(viewName = "چاوشی", iconRes = R.drawable.img_speaker_chavoshi),
    Golzar(viewName = "گلزار", iconRes = R.drawable.img_speaker_golzar);

    companion object {
        fun findByName(name: String): HamahangSpeakerView {
            return HamahangSpeakerView.entries.first { it.name == name }
        }
    }
}