// Updated MainActivity integration with singleton ThemeManager

package com.example.fplyzer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.fplyzer.ui.navigation.FplNavigation
import com.example.fplyzer.ui.theme.FPLyzerTheme
import com.example.fplyzer.ui.theme.ThemeManagerHolder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FPLyzerApp()
        }
    }
}

@Composable
fun FPLyzerApp() {
    val context = LocalContext.current
    val themeManager = ThemeManagerHolder.getInstance(context)

    FPLyzerTheme(themeManager = themeManager) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            FplNavigation()
        }
    }
}
