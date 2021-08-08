package com.worker8.androiddevnews.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val typography = Typography() /* to be customized */

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200,
    background = Color(0xFF102A43)
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200,
    background = Color.White,

    /* Other default colors to override
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

val Colors.BottomNavBg: Color
    @Composable
    get() = if (isLight) Color(0xff0C008C) else Color(0xff061a2d)

val Colors.Neutral00: Color
    @Composable
    get() = if (isLight) Color.White else Color(0xFF102A43)
val Colors.Neutral01: Color
    @Composable
    get() = if (isLight) Color(0xFFF0F4F8) else Color(0xFF243B53)
val Colors.Neutral02: Color
    @Composable
    get() = if (isLight) Color(0xFFD9E2EC) else Color(0xFF334E68)
val Colors.Neutral03: Color
    @Composable
    get() = if (isLight) Color(0xFFBCCCDC) else Color(0xFF486581)
val Colors.Neutral04: Color
    @Composable
    get() = if (isLight) Color(0xFF9FB3C8) else Color(0xFF627D98)
val Colors.Neutral05: Color
    @Composable
    get() = if (isLight) Color(0xFF829AB1) else Color(0xFF829AB1)
val Colors.Neutral06: Color
    @Composable
    get() = if (isLight) Color(0xFF627D98) else Color(0xFF9FB3C8)
val Colors.Neutral07: Color
    @Composable
    get() = if (isLight) Color(0xFF486581) else Color(0xFFBCCCDC)
val Colors.Neutral08: Color
    @Composable
    get() = if (isLight) Color(0xFF334E68) else Color(0xFFD9E2EC)
val Colors.Neutral09: Color
    @Composable
    get() = if (isLight) Color(0xFF243B53) else Color(0xFFF0F4F8)
val Colors.Neutral10: Color
    @Composable
    get() = if (isLight) Color(0xFF102A43) else Color.White

val Colors.Primary01: Color
    @Composable
    get() = if (isLight) Color(0xFFE6E6FF) else Color(0xFF0C008C)
val Colors.Primary02: Color
    @Composable
    get() = if (isLight) Color(0xFFC4C6FF) else Color(0xFF1D0EBE)
val Colors.Primary03: Color
    @Composable
    get() = if (isLight) Color(0xFFA2A5FC) else Color(0xFF3525E6)
val Colors.Primary04: Color
    @Composable
    get() = if (isLight) Color(0xFF8888FC) else Color(0xFF4D3DF7)
val Colors.Primary05: Color
    @Composable
    get() = if (isLight) Color(0xFF7069FA) else Color(0xFF5D55FA)
val Colors.Primary06: Color
    @Composable
    get() = if (isLight) Color(0xFF5D55FA) else Color(0xFF7069FA)
val Colors.Primary07: Color
    @Composable
    get() = if (isLight) Color(0xFF4D3DF7) else Color(0xFF8888FC)
val Colors.Primary08: Color
    @Composable
    get() = if (isLight) Color(0xFF3525E6) else Color(0xFFA2A5FC)
val Colors.Primary09: Color
    @Composable
    get() = if (isLight) Color(0xFF1D0EBE) else Color(0xFFC4C6FF)
val Colors.Primary10: Color
    @Composable
    get() = if (isLight) Color(0xFF0C008C) else Color(0xFFE6E6FF)

@Composable
fun AndroidDevNewsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = Shapes,
        content = content
    )
}