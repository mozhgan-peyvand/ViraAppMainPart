package ir.part.app.intelligentassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ir.part.app.intelligentassistant.ui.theme.IntelligentAssistantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntelligentAssistantTheme {

            }
        }
    }
}