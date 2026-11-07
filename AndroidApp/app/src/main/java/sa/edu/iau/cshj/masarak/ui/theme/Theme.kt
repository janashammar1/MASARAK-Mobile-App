package sa.edu.iau.cshj.masarak.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = DeepNavy,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = Teal,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    tertiary = PearlAqua,
    onTertiary = DeepNavy,
    background = androidx.compose.ui.graphics.Color.White,
    onBackground = DeepNavy,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = DeepNavy
)

private val DarkColorScheme = darkColorScheme(
    primary = PearlAqua,
    onPrimary = DeepNavy,
    secondary = Teal,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    tertiary = Periwinkle,
    background = DeepNavy,
    onBackground = androidx.compose.ui.graphics.Color.White,
    surface = DeepNavy,
    onSurface = androidx.compose.ui.graphics.Color.White
)

@Composable
fun MASARAKTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}