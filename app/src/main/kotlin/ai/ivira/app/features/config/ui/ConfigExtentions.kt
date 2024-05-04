package ai.ivira.app.features.config.ui

import ai.ivira.app.features.config.data.model.ConfigTileEntity

fun ConfigTileEntity.toTileItem() = when (stringToKey(name)) {
    Keys.Avanegar -> TileItem.Avanegar(
        key = name,
        available = status,
        unavailableStateMessage = message
    )
    Keys.Avasho -> TileItem.Avasho(
        key = name,
        available = status,
        unavailableStateMessage = message
    )
    Keys.Imazh -> TileItem.Imazh(
        key = name,
        available = status,
        unavailableStateMessage = message
    )
    null -> null
}

private enum class Keys(val key: String) {
    Avanegar("avanegar"),
    Avasho("avasho"),
    Imazh("imazh")
}

private fun stringToKey(str: String) = when (str) {
    Keys.Avanegar.key -> Keys.Avanegar
    Keys.Avasho.key -> Keys.Avasho
    Keys.Imazh.key -> Keys.Imazh
    else -> null
}