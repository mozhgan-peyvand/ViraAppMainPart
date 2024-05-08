package ai.ivira.app.features.config.ui

import ai.ivira.app.R
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed class TileItem(
    val key: String,
    val available: Boolean,
    @DrawableRes val iconRes: Int,
    @StringRes val titleRes: Int,
    val unavailableStateMessage: String
) {
    class Avanegar(
        key: String,
        available: Boolean,
        unavailableStateMessage: String
    ) : TileItem(
        key = key,
        available = available,
        iconRes = R.drawable.img_ava_negar,
        titleRes = R.string.lbl_ava_negar,
        unavailableStateMessage = unavailableStateMessage
    )

    class Avasho(
        key: String,
        available: Boolean,
        unavailableStateMessage: String
    ) : TileItem(
        key = key,
        available = available,
        iconRes = R.drawable.img_ava_sho,
        titleRes = R.string.lbl_avasho,
        unavailableStateMessage = unavailableStateMessage
    )

    class Imazh(
        key: String,
        available: Boolean,
        unavailableStateMessage: String
    ) : TileItem(
        key = key,
        available = available,
        iconRes = R.drawable.img_imazh,
        titleRes = R.string.lbl_imazh,
        unavailableStateMessage = unavailableStateMessage
    )

    class Hamahang(
        key: String,
        available: Boolean,
        unavailableStateMessage: String
    ) : TileItem(
        key = key,
        available = available,
        iconRes = R.drawable.img_hamahang,
        titleRes = R.string.lbl_hamahang,
        unavailableStateMessage = unavailableStateMessage
    )
}