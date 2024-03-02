package ai.ivira.app.designsystem.bottomsheet

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
fun <S> ViraBottomSheetContent(
    targetState: S,
    contentAlignment: Alignment = Alignment.TopStart,
    contentKey: (targetState: S) -> Any? = { it },
    transitionSpec: AnimatedContentTransitionScope<S>.() -> ContentTransform = {
        (fadeIn(animationSpec = tween(220, delayMillis = 90)) +
            scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90)))
            .togetherWith(fadeOut(animationSpec = tween(90)))
    },
    content: @Composable (S) -> Unit
) {
    val transition = updateTransition(targetState = targetState, "bottomSheet")
    transition.AnimatedContent(
        transitionSpec = transitionSpec,
        contentAlignment = contentAlignment,
        contentKey = contentKey
    ) { state ->
        content(state)
    }
}