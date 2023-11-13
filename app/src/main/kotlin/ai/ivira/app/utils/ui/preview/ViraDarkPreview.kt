package ai.ivira.app.utils.ui.preview

import androidx.compose.ui.tooling.preview.Preview
import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.FUNCTION

@Target(FUNCTION)
@Retention(SOURCE)
@Preview(showBackground = true, backgroundColor = 0xFF070707)
annotation class ViraDarkPreview