package ir.part.app.intelligentassistant.features.ava_negar.ui.record

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ir.part.app.intelligentassistant.R

@Composable
fun RecordingAnimation(
    isRecording: Boolean,
    onRecordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                block = { anim, _ -> scale = anim.scale; angle = anim.angle },
                animationSpec = tween(durationMillis = 300, easing = EaseOut),
            )

            animate(
                typeConverter = VoiceRecordingCircleAnimation.twoWayConverter(),
                initialValue = VoiceRecordingCircleAnimation(scale = 0.8f, angle = 0f),
                targetValue = VoiceRecordingCircleAnimation(scale = 0.7f, angle = 15f),
                block = { anim, _ -> scale = anim.scale; angle = anim.angle },
                animationSpec = tween(durationMillis = 300, easing = EaseOut),
            )
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.then(
            if (!isRecording) {
                Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onRecordClick() }
                )
            } else {
                Modifier
            }
        )
    ) {
        AnimatedVisibility(
            visible = isRecording,
            enter = scaleIn(animationSpec = tween(500), initialScale = 0.6f),
            exit = scaleOut(animationSpec = tween(500), targetScale = 0.6f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_outside_circle),
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
            Image(
                painter = painterResource(id = R.drawable.ic_inside_circle),
                contentDescription = null,
                modifier = Modifier
                    .size(121.dp)
                    .scale(scale)
                    .rotate(angle)
            )
        }

        Image(
            painter = painterResource(id = R.drawable.img_recording),
            contentDescription = if (isRecording) null else stringResource(R.string.desc_start_recording),
            modifier = Modifier
                .size(89.dp)
        )
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