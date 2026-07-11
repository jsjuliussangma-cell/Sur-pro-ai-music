package com.surpro.aimusic.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.surpro.aimusic.ui.navigation.AppNavigation
import com.surpro.aimusic.ui.theme.SurProTheme

@Composable
fun SurProApp() {
    val navController = rememberNavController()
    
    SurProTheme {
        AppNavigation(navController = navController)
    }
}
