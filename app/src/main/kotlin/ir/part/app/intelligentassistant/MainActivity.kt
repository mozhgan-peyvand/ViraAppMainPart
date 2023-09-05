package ir.part.app.intelligentassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection.Rtl
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.part.app.intelligentassistant.utils.ui.navigation.AppNavigation
import ir.part.app.intelligentassistant.utils.ui.theme.IntelligentAssistantTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            IntelligentAssistantTheme {
                CompositionLocalProvider(
                    LocalLayoutDirection provides Rtl,
                ) {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = MaterialTheme.colors.background
                            )
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            AppNavigation(navController)
                        }
                    }
                }
            }

        }
    }
}
