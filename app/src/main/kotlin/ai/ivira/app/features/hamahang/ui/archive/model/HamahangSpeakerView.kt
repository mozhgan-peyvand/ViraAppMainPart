package ai.ivira.app.features.hamahang.ui.archive.model

import ai.ivira.app.R
import androidx.annotation.DrawableRes

enum class HamahangSpeakerView(
    val viewName: String,
    val serverName: String, // TODO: needs to be in data layer
    @DrawableRes val iconRes: Int
) {
    Khiabani(
        viewName = "خیابانی",
        serverName = "Khiabani",
        iconRes = R.drawable.img_speaker_khiabani
    ),
    FerdowsiPour(
        viewName = "فردوسی پور",
        serverName = "Adel",
        iconRes = R.drawable.img_speaker_ferdowsi_pour
    ),
    Modiri(
        viewName = "مدیری",
        serverName = "Modiri",
        iconRes = R.drawable.img_speaker_modiri
    ),
    Hatami(
        viewName = "حاتمی",
        serverName = "Leyla.Hatami",
        iconRes = R.drawable.img_speaker_hatami
    ),
    Chavoshi(
        viewName = "چاوشی",
        serverName = "Chavoshi",
        iconRes = R.drawable.img_speaker_chavoshi
    ),
    Golzar(
        viewName = "گلزار",
        serverName = "Golzar",
        iconRes = R.drawable.img_speaker_golzar
    );

    companion object {
        fun findByServerName(serverName: String): HamahangSpeakerView {
            return HamahangSpeakerView.entries.first { it.serverName == serverName }
        }

        fun defaultSpeakers(): List<HamahangSpeakerView> {
            return listOf(FerdowsiPour, Hatami, Modiri, Khiabani, Chavoshi, Golzar)
        }
    }
}