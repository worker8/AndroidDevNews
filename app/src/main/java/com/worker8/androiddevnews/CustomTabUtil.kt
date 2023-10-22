package com.worker8.androiddevnews

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

fun launchCustomTab(context: Context, url: String, backgroundColor: Color) {
    CustomTabsIntent.Builder()
//                        .setInitialActivityHeightPx(400, ACTIVITY_HEIGHT_FIXED)
//                        .setInitialActivityHeightPx(500)
//                        .setCloseButtonPosition(CustomTabsIntent.CLOSE_BUTTON_POSITION_END)
//                        .setToolbarCornerRadiusDp(10)
        // set the default color scheme
        .setDefaultColorSchemeParams(
            CustomTabColorSchemeParams.Builder()
                .setToolbarColor(backgroundColor.toArgb())
                .build()
        )
        // set the alternative dark color scheme
        .setColorSchemeParams(
            CustomTabsIntent.COLOR_SCHEME_DARK,
            CustomTabColorSchemeParams.Builder()
                .setToolbarColor(backgroundColor.toArgb())
                .build()
        )
        .build()
        .launchUrl(context, Uri.parse(url))
}