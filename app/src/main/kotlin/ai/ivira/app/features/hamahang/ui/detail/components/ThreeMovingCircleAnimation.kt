package ai.ivira.app.features.hamahang.ui.detail.components

import android.view.animation.AccelerateDecelerateInterpolator
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.delay

@Composable
fun ThreeMovingCircleAnimation(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .alpha(0.4f)
    ) {
        var currentStep by rememberSaveable { mutableStateOf(Step.Step1) }

        LaunchedEffect(isPlaying) {
            val animDelay = 1500L
            val stopDelay = 2000L
            while (isPlaying) {
                currentStep = currentStep.nextStep()
                delay(animDelay + stopDelay)
            }
        }

        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        val transition = updateTransition(targetState = currentStep, label = "TwoCircleAnimation")
        val centerAnim: @Composable Transition.Segment<Step>.() -> FiniteAnimationSpec<Offset> =
            {
                tween(
                    durationMillis = 1500,
                    easing = { AccelerateDecelerateInterpolator().getInterpolation(it) }
                )
            }
        val radiusAnim: @Composable Transition.Segment<Step>.() -> FiniteAnimationSpec<Float> = {
            tween(
                durationMillis = 1500,
                easing = { AccelerateDecelerateInterpolator().getInterpolation(it) }
            )
        }

        // region GreenCircle
        val c1 by transition.animateValue(
            typeConverter = offsetTwoWayConvertor(),
            transitionSpec = centerAnim,
            label = "c1 center"
        ) {
            when (it) {
                Step.Step1 -> Offset(0.9694f * widthPx, 0.3612f * heightPx)
                Step.Step2 -> Offset(0.0972f * widthPx, 0.23f * heightPx)
                Step.Step3 -> Offset(0.1861f * widthPx, 0.9237f * heightPx)
            }
        }

        val r1 by transition.animateFloat(label = "c1 radius", transitionSpec = radiusAnim) {
            when (it) {
                Step.Step1 -> 0.4388f * widthPx
                Step.Step2 -> 0.5111f * widthPx
                Step.Step3 -> 0.525f * widthPx
            }
        }

        val b1 = remember(c1, r1) {
            Brush.radialGradient(
                colors = listOf(
                    Color(0.1404f, 0.585f, 0f, 0.44f),
                    Color(0.2886f, 0.8229f, 0.2028f, 0.00f)
                ),
                center = c1,
                radius = r1,
                tileMode = TileMode.Clamp
            )
        }
        // endregion GreenCircle

        // region PurpleCircle
        val c2 by transition.animateValue(
            typeConverter = offsetTwoWayConvertor(),
            transitionSpec = centerAnim,
            label = "c2 center"
        ) {
            when (it) {
                Step.Step1 -> Offset(0.136f * widthPx, 0.5412f * heightPx)
                Step.Step2 -> Offset(1f * widthPx, 0.5037f * heightPx)
                Step.Step3 -> Offset(0.07f * widthPx, 0.3875f * heightPx)
            }
        }

        val r2 by transition.animateFloat(label = "c2 radius", transitionSpec = radiusAnim) {
            when (it) {
                Step.Step1 -> 0.425f * widthPx
                Step.Step2 -> 0.3944f * widthPx
                Step.Step3 -> 0.4855f * widthPx
            }
        }

        val b2 = remember(c2, r2) {
            Brush.radialGradient(
                colors = listOf(
                    Color(0.3315f, 0.1872f, 0.5733f, 0.44f),
                    Color(0.5928f, 0.4446f, 0.8385f, 0f)
                ),
                center = c2,
                radius = r2,
                tileMode = TileMode.Clamp
            )
        }
        // endregion PurpleCircle

        // region BlueCircle
        val c3 by transition.animateValue(
            typeConverter = offsetTwoWayConvertor(),
            transitionSpec = centerAnim,
            label = "c3 center"
        ) {
            when (it) {
                Step.Step1 -> Offset(0.7638f * widthPx, 0.875f * heightPx)
                Step.Step2 -> Offset(0.1894f * widthPx, 0.9087f * heightPx)
                Step.Step3 -> Offset(1.175f * widthPx, 0.6362f * heightPx)
            }
        }

        val r3 by transition.animateFloat(label = "c3 radius", transitionSpec = radiusAnim) {
            when (it) {
                Step.Step1 -> 0.6188f * widthPx
                Step.Step2 -> 0.6527f * widthPx
                Step.Step3 -> 0.6188f * widthPx
            }
        }

        val b3 = remember(c3, r3) {
            Brush.radialGradient(
                colors = listOf(
                    Color(0.1989f, 0.5187f, 0.9945f, 0.44f),
                    Color(0.4095f, 0.6123f, 0.9203f, 0f)
                ),
                center = c3,
                radius = r3,
                tileMode = TileMode.Clamp
            )
        }
        // endregion BlueCircle

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(brush = b1, radius = r1, center = c1)
            drawCircle(brush = b2, radius = r2, center = c2)
            drawCircle(brush = b3, radius = r3, center = c3)
        }
    }
}

private enum class Step {
    Step1,
    Step2,
    Step3;

    fun nextStep(): Step {
        return when (this) {
            Step1 -> Step2
            Step2 -> Step3
            Step3 -> Step1
        }
    }
}

private fun offsetTwoWayConvertor(): TwoWayConverter<Offset, AnimationVector2D> {
    return object : TwoWayConverter<Offset, AnimationVector2D> {
        override val convertFromVector: (AnimationVector2D) -> Offset
            get() = { Offset(it.v1, it.v2) }
        override val convertToVector: (Offset) -> AnimationVector2D
            get() = { AnimationVector2D(it.x, it.y) }
    }
}