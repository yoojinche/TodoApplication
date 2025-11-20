package com.example.todoapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todoapplication.screens.auth.EmailAuthScreen
import com.example.todoapplication.screens.auth.GoogleSignScreen
import com.example.todoapplication.screens.auth.HomeScreen
import com.example.todoapplication.screens.auth.LoginMenuScreen
import com.example.todoapplication.screens.auth.Routes

@Composable
fun AppNav(onLoginDone: () -> Unit) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.MENU) {
        composable(Routes.MENU) {
            LoginMenuScreen(
                onEmailClick = { nav.navigate(Routes.EMAIL) },
                onGoogleClick = { nav.navigate(Routes.GOOGLE) }
            )
        }
        composable(Routes.EMAIL) {
            EmailAuthScreen(
                onSignedIn = {
                    onLoginDone()
                    nav.navigate(Routes.HOME) { popUpTo(Routes.MENU) { inclusive = true } }
                }
            )
        }
        composable(Routes.GOOGLE) {
            GoogleSignScreen(
                onSignedIn = {
                    onLoginDone()
                    nav.navigate(Routes.HOME) { popUpTo(Routes.MENU) { inclusive = true } }
                }
            )
        }
        composable(Routes.HOME) { HomeScreen(onNoteClick = { nav.navigate(Routes.NOTE) }) }
        composable(Routes.NOTE) {
//            val vm = remember { NoteViewModel() }
//            NoteScreen(viewModel = vm)
        }
    }
}