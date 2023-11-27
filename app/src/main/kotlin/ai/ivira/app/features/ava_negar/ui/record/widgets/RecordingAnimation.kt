package ai.ivira.app.features.ava_negar.ui.record.widgets

import ai.ivira.app.R
import ai.ivira.app.utils.ui.safeClickable
import ai.ivira.app.utils.ui.widgets.ViraImage
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun RecordingAnimation(
    isRecording: Boolean,
    onRecordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var scale by remember(isRecording) { mutableFloatStateOf(0.7f) }
    var angle by remember(isRecording) {
        mutableFloatStateOf(0f)
    }

    LaunchedEffect(isRecording) {
        while (isRecording) {
            animate(
                initialValue = 15f,
                targetValue = -30f,
                animationSpec = tween(durationMillis = 300, easing = EaseOut),
                block = { value, _ -> angle = value }
            )

            animate(
                typeConverter = VoiceRecordingCircleAnimation.twoWayConverter(),
                initialValue = VoiceRecordingCircleAnimation(scale = 0.7f, angle = -30f),
                targetValue = VoiceRecordingCircleAnimation(scale = 0.8f, angle = 0f),
                block = { anim, _ ->
                    scale = anim.scale
                    angle = anim.angle
                },
                animationSpec = tween(durationMillis = 300, easing = EaseOut)
            )

            animate(
                typeConverter = VoiceRecordingCircleAnimation.twoWayConverter(),
                initialValue = VoiceRecordingCircleAnimation(scale = 0.8f, angle = 0f),
                targetValue = VoiceRecordingCircleAnimation(scale = 0.7f, angle = 15f),
                block = { anim, _ ->
                    scale = anim.scale
                    angle = anim.angle
                },
                animationSpec = tween(durationMillis = 300, easing = EaseOut)
            )
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(CircleShape)
            .then(
                if (isRecording) {
                    Modifier
                } else {
                    Modifier.semantics {
                        contentDescription = context.getString(R.string.desc_start_recording)
                    }
                }
            )
            .safeClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onRecordClick() }
            )
    ) {
        AnimatedVisibility(
            visible = isRecording,
            enter = scaleIn(animationSpec = tween(500), initialScale = 0.6f),
            exit = scaleOut(animationSpec = tween(500), targetScale = 0.6f)
        ) {
            ViraImage(
                drawable = R.drawable.ic_outside_circle,
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .scale(scale)
                    .rotate(angle)
            )
        }

        AnimatedVisibility(
            visible = isRecording,
            enter = scaleIn(animationSpec = tween(100)),
            exit = scaleOut(animationSpec = tween(100))
        ) {
            ViraImage(
                drawable = R.drawable.ic_inside_circle,
                contentDescription = null,
                modifier = Modifier
                    .size(121.dp)
                    .scale(scale)
                    .rotate(angle)
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(89.dp)
        ) {
            ViraImage(
                drawable = R.drawable.ic_recording_circle,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
            AnimatedContent(
                targetState = isRecording,
                label = "recordingIcon",
                contentAlignment = Alignment.Center,
                transitionSpec = {
                    ContentTransform(
                        targetContentEnter = fadeIn(tween(200))
                            .plus(slideInVertically(tween(200), initialOffsetY = { it / 3 })),
                        initialContentExit = slideOutVertically(
                            tween(400),
                            targetOffsetY = { it / 3 * 2 })
                            .plus(scaleOut(tween(200)))
                    )
                }
            ) { isRecording ->
                if (isRecording) {
                    ViraImage(
                        drawable = R.drawable.ic_pause_2,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    ViraImage(
                        drawable = R.drawable.ic_mic_2,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

private data class VoiceRecordingCircleAnimation(
    val scale: Float,
    val angle: Float
) {
    companion object {
        fun twoWayConverter() =
            object : TwoWayConverter<VoiceRecordingCircleAnimation, AnimationVector2D> {
                override val convertFromVector: (AnimationVector2D) -> VoiceRecordingCircleAnimation
                    get() = { VoiceRecordingCircleAnimation(it.v1, it.v2) }
                override val convertToVector: (VoiceRecordingCircleAnimation) -> AnimationVector2D
                    get() = { AnimationVector2D(it.scale, it.angle) }
            }
    }
}