package ai.ivira.app.utils.ui

import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.platform.inspectable
import androidx.compose.ui.semantics.Role

private var lastEventTimeMs: Long = 0
fun Modifier.safeClickable(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    indication: Indication?,
    interactionSource: MutableInteractionSource,
    role: Role? = null,
    onClick: () -> Unit
) = inspectable(
    inspectorInfo = debugInspectorInfo {
        name = "safeClickable"
        properties["enabled"] = enabled
        properties["onClickLabel"] = onClickLabel
        properties["indication"] = indication
        properties["interactionSource"] = interactionSource
        properties["role"] = role
        properties["onClick"] = onClick
    }
) {
    Modifier.clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        onClick = { safeClick { onClick() } },
        role = role,
        indication = indication,
        interactionSource = interactionSource
    )
}

fun Modifier.safeClickable(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
) = inspectable(
    inspectorInfo = debugInspectorInfo {
        name = "safeClickable"
        properties["enabled"] = enabled
        properties["onClickLabel"] = onClickLabel
        properties["role"] = role
        properties["onClick"] = onClick
    }
) {
    Modifier.clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        onClick = { safeClick { onClick() } },
        role = role
    )
}

fun safeClick(event: () -> Unit) {
    if (System.currentTimeMillis() - lastEventTimeMs >= 300L) {
        lastEventTimeMs = System.currentTimeMillis()
        event.invoke()
    }
}