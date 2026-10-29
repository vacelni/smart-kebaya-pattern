package com.smartkebaya.pattern

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smartkebaya.pattern.ui.MainScreen
import com.smartkebaya.pattern.ui.theme.SmartKebayaTheme
import com.smartkebaya.pattern.viewmodel.KebayaViewModel
import com.smartkebaya.pattern.util.PlatformUtil

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        PlatformUtil.setContext(this)
        setContent {
            val viewModel: KebayaViewModel = viewModel()
            val state by viewModel.uiState.collectAsState()
            SmartKebayaTheme(darkTheme = state.isDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}