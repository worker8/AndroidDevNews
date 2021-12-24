package com.worker8.androiddevnews.newsletter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

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
                KotlinWeeklyScreen()
            }
        }
    }

}