package app.zabludo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFFB23A48),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD9),
    onPrimaryContainer = Color(0xFF410005),
    secondary = Color(0xFF775652),
    onSecondary = Color.White,
    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF201A1A),
    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF201A1A),
    surfaceVariant = Color(0xFFF4DDDB),
    onSurfaceVariant = Color(0xFF534342)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB4AB),
    onPrimary = Color(0xFF691A1F),
    primaryContainer = Color(0xFF93000A),
    onPrimaryContainer = Color(0xFFFFDAD9),
    secondary = Color(0xFFE6C1BE),
    onSecondary = Color(0xFF442C2C),
    background = Color(0xFF201A1A),
    onBackground = Color(0xFFEDE0DF),
    surface = Color(0xFF201A1A),
    onSurface = Color(0xFFEDE0DF),
    surfaceVariant = Color(0xFF534342),
    onSurfaceVariant = Color(0xFFD7C1BF)
)

@Composable
fun ZaBlyudoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(colorScheme = colors, content = content)
}
