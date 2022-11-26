package com.worker8.androiddevnews.common.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color


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

interface ThemeColor {
    val Neutral00: Color
    val Neutral01: Color
    val Neutral02: Color
    val Neutral03: Color
    val Neutral04: Color
    val Neutral05: Color
    val Neutral06: Color
    val Neutral07: Color
    val Neutral08: Color
    val Neutral09: Color
    val Neutral10: Color
}

class BlueGrey {
    object Dark : ThemeColor {
        override val Neutral00: Color = Color(0xFF102A43)
        override val Neutral01: Color = Color(0xFF243B53)
        override val Neutral02: Color = Color(0xFF334E68)
        override val Neutral03: Color = Color(0xFF486581)
        override val Neutral04: Color = Color(0xFF627D98)
        override val Neutral05: Color = Color(0xFF829AB1)
        override val Neutral06: Color = Color(0xFF9FB3C8)
        override val Neutral07: Color = Color(0xFFBCCCDC)
        override val Neutral08: Color = Color(0xFFD9E2EC)
        override val Neutral09: Color = Color(0xFFF0F4F8)
        override val Neutral10: Color = Color.White
    }

    object Light : ThemeColor {
        override val Neutral00: Color = Color.White
        override val Neutral01: Color = Color(0xFFF0F4F8)
        override val Neutral02: Color = Color(0xFFD9E2EC)
        override val Neutral03: Color = Color(0xFFBCCCDC)
        override val Neutral04: Color = Color(0xFF9FB3C8)
        override val Neutral05: Color = Color(0xFF829AB1)
        override val Neutral06: Color = Color(0xFF627D98)
        override val Neutral07: Color = Color(0xFF486581)
        override val Neutral08: Color = Color(0xFF334E68)
        override val Neutral09: Color = Color(0xFF243B53)
        override val Neutral10: Color = Color(0xFF102A43)
    }
}

class CoolGrey {
    object Dark : ThemeColor {
        override val Neutral00: Color = Color(0xFF1F2933)
        override val Neutral01: Color = Color(0xFF323F4B)
        override val Neutral02: Color = Color(0xFF3E4C59)
        override val Neutral03: Color = Color(0xFF52606D)
        override val Neutral04: Color = Color(0xFF616E7C)
        override val Neutral05: Color = Color(0xFF7B8794)
        override val Neutral06: Color = Color(0xFF9AA5B1)
        override val Neutral07: Color = Color(0xFFCBD2D9)
        override val Neutral08: Color = Color(0xFFE4E7EB)
        override val Neutral09: Color = Color(0xFFF5F7FA)
        override val Neutral10: Color = Color.White
    }

    object Light : ThemeColor {
        override val Neutral00: Color = Color.White
        override val Neutral01: Color = Color(0xFFF5F7FA)
        override val Neutral02: Color = Color(0xFFE4E7EB)
        override val Neutral03: Color = Color(0xFFCBD2D9)
        override val Neutral04: Color = Color(0xFF9AA5B1)
        override val Neutral05: Color = Color(0xFF7B8794)
        override val Neutral06: Color = Color(0xFF616E7C)
        override val Neutral07: Color = Color(0xFF52606D)
        override val Neutral08: Color = Color(0xFF3E4C59)
        override val Neutral09: Color = Color(0xFF323F4B)
        override val Neutral10: Color = Color(0xFF1F2933)
    }
}

class NeutralColor(val dark: ThemeColor = CoolGrey.Dark, val light: ThemeColor = CoolGrey.Light)

val CurrentNeutral = NeutralColor()

val Colors.Neutral00: Color
    @Composable
    get() = if (isLight) CurrentNeutral.light.Neutral00 else CurrentNeutral.dark.Neutral00
val Colors.Neutral01: Color
    @Composable
    get() = if (isLight) CurrentNeutral.light.Neutral01 else CurrentNeutral.dark.Neutral01
val Colors.Neutral02: Color
    @Composable
    get() = if (isLight) CurrentNeutral.light.Neutral02 else CurrentNeutral.dark.Neutral02
val Colors.Neutral03: Color
    @Composable
    get() = if (isLight) CurrentNeutral.light.Neutral03 else CurrentNeutral.dark.Neutral03
val Colors.Neutral04: Color
    @Composable
    get() = if (isLight) CurrentNeutral.light.Neutral04 else CurrentNeutral.dark.Neutral04
val Colors.Neutral05: Color
    @Composable
    get() = if (isLight) CurrentNeutral.light.Neutral05 else CurrentNeutral.dark.Neutral05
val Colors.Neutral06: Color
    @Composable
    get() = if (isLight) CurrentNeutral.light.Neutral06 else CurrentNeutral.dark.Neutral06
val Colors.Neutral07: Color
    @Composable
    get() = if (isLight) CurrentNeutral.light.Neutral07 else CurrentNeutral.dark.Neutral07
val Colors.Neutral08: Color
    @Composable
    get() = if (isLight) CurrentNeutral.light.Neutral08 else CurrentNeutral.dark.Neutral08
val Colors.Neutral09: Color
    @Composable
    get() = if (isLight) CurrentNeutral.light.Neutral09 else CurrentNeutral.dark.Neutral09
val Colors.Neutral10: Color
    @Composable
    get() = if (isLight) CurrentNeutral.light.Neutral10 else CurrentNeutral.dark.Neutral10

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
    val defaultTypography = Typography()

    MaterialTheme(
        colors = colors,
        typography = Typography(
            caption = defaultTypography.caption.copy(color = colors.Neutral08)
        ),
        shapes = Shapes,
        content = {
            val temp = LocalRippleTheme.provides(RippleCustomTheme)
            CompositionLocalProvider(temp) {
                content()
            }
//            ProvideTextStyle(
//                value = TextStyle(color = Color.White),
//                content = content
//            )
        }
    )
}


private object RippleCustomTheme : RippleTheme {

    //Your custom implementation...
    @Composable
    override fun defaultColor() =
        MaterialTheme.colors.Primary03

    @Composable
    override fun rippleAlpha(): RippleAlpha =
        RippleTheme.defaultRippleAlpha(
            MaterialTheme.colors.Primary09,
            lightTheme = isSystemInDarkTheme()
        )
}