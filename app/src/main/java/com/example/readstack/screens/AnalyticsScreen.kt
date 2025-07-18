package com.example.readstack.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import dev.chrisbanes.haze.HazeState
import java.lang.reflect.Modifier

@Composable
fun AnalyticsScreen(
    navController: NavHostController,
    hazeState: HazeState,
) {
    Text(
        text = "Analytics Screen"
    )
}