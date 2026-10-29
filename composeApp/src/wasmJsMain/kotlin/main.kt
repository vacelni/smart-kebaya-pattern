import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.smartkebaya.pattern.ui.MainScreen
import com.smartkebaya.pattern.ui.theme.SmartKebayaTheme
import com.smartkebaya.pattern.viewmodel.KebayaViewModel
import androidx.compose.runtime.remember

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport("compose-canvas") {
        val viewModel = remember { KebayaViewModel() }
        SmartKebayaTheme {
            MainScreen(viewModel = viewModel)
        }
    }
}
