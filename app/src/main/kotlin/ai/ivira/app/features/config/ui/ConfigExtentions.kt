package ai.ivira.app.features.config.ui

import ai.ivira.app.features.config.data.TileConfigEntity

fun TileConfigEntity.toTileItem() = when (stringToKey(name)) {
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
    null -> null
}

private enum class Keys(val key: String) {
    Avanegar("avanegar"),
    Avasho("avasho")
}

private fun stringToKey(str: String) = when (str) {
    Keys.Avanegar.key -> Keys.Avanegar
    Keys.Avasho.key -> Keys.Avasho
    else -> null
}