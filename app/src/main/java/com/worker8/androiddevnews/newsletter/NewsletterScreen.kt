package com.worker8.androiddevnews.newsletter

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import com.worker8.androiddevnews.ui.theme.Neutral02

private enum class WeeklyTab(val text: String) {
    AndroidWeekly("Android Weekly"),
    KotlinWeekly("Kotlin Weekly")
}

@Composable
fun NewsletterScreen() {
    var selectedTab by remember { mutableStateOf(WeeklyTab.AndroidWeekly) }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()

    ) {
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.onBackground
        ) {
            WeeklyTab.values().forEachIndexed { index, tab ->
                Tab(selected = selectedTab.ordinal == index,
                    onClick = {
                        selectedTab = WeeklyTab.values().find { it.ordinal == index }
                            ?: WeeklyTab.AndroidWeekly
                    },
                    text = { Text(tab.text) })
            }
        }
        when (selectedTab) {
            WeeklyTab.AndroidWeekly -> {
                AndroidWeeklyScreen()
            }
            WeeklyTab.KotlinWeekly -> {
                Text("Kotlin Weekly  ..... in construction")
            }
        }
    }

}