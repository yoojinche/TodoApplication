package com.example.todoapplication.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.todoapplication.screens.AddEditTodoScreen
import com.example.todoapplication.screens.TodoListScreen
import com.example.todoapplication.viewmodel.TodoViewModel


@Composable
fun NavGraph(context: Context, navController: NavHostController) {
    val viewModel: TodoViewModel = hiltViewModel()

    NavHost (navController = navController, startDestination = "list") {
        composable("list") {
            TodoListScreen(context, viewModel, navController)
        }
        composable("addEdit?id={id}") { backStackEntry ->
            AddEditTodoScreen(context, viewModel, navController, backStackEntry)
        }
        composable("addEdit") { backStackEntry ->
            AddEditTodoScreen(context, viewModel, navController, backStackEntry)
        }
    }
}