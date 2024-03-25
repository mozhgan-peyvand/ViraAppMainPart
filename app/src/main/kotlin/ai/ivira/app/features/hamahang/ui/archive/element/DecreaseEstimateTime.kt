package ai.ivira.app.features.hamahang.ui.archive.element

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay

// Duplicate DecreaseEstimateTime 3
@Composable
fun DecreaseEstimateTime(
    estimationTime: Int,
    token: String,
    callBack: (Int) -> Unit
) {
    val getEstimationTime = remember(token) {
        mutableIntStateOf(estimationTime)
    }
    LaunchedEffect(token) {
        while (getEstimationTime.intValue > 0) {
            if (getEstimationTime.intValue < 14) {
                delay(1000)
                getEstimationTime.intValue -= 1
                callBack(getEstimationTime.intValue)
                continue
            }

            delay(10000)
            getEstimationTime.intValue -= 10
            callBack(getEstimationTime.intValue)
        }
    }
}