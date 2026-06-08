package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BoutiquePrimaryDark,
    secondary = BoutiqueSecondaryDark,
    tertiary = BoutiqueTertiaryDark,
    background = BoutiqueBackgroundDark,
    surface = BoutiqueSurfaceDark,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = BoutiqueTextDark,
    onSurface = BoutiqueTextDark
)

private val LightColorScheme = lightColorScheme(
    primary = BoutiquePrimary,
    secondary = BoutiqueSecondary,
    tertiary = BoutiqueTertiary,
    background = BoutiqueBackground,
    surface = BoutiqueSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = BoutiqueText,
    onSurface = BoutiqueText
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disable dynamic colors to keep our premium boutique color branding consistent and stunning!
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
