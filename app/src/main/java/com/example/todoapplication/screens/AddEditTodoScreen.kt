package com.example.todoapplication.screens

import android.content.Context
import android.icu.util.Calendar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.todoapplication.data.TodoEntity
import com.example.todoapplication.viewmodel.TodoViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTodoScreen(
    context: Context,
    viewModel: TodoViewModel,
    navController: NavController,
    backStackEntry: NavBackStackEntry
) {
    val todoId = backStackEntry.arguments?.getString("id")?.toIntOrNull()
    val todoList by viewModel.todoList.collectAsState()
    val existingTodo = todoList.find { it.id == todoId }

    var text by remember { mutableStateOf(existingTodo?.title ?: "") }
    var tag by remember { mutableStateOf(existingTodo?.tag ?: "") }
    var memo by remember { mutableStateOf(existingTodo?.memo ?: "") }
    var dueDate by remember { mutableStateOf(existingTodo?.dueDate ?: "") }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                dueDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
            },
            year,
            month,
            day
        )
    }

    Scaffold(
        topBar = { TopBar() }
    ) { padding ->
        Column (modifier = Modifier
            .padding(padding)
            .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = text,
                onValueChange = { text = it },
                label = { Text("할 일") })
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = tag,
                onValueChange = { tag = it },
                label = { Text("태그") })
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = memo,
                onValueChange = { memo = it },
                label = { Text("메모") })
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable{
                    datePickerDialog.show()
                },
                value = dueDate,
                onValueChange = { dueDate = it  },
                label = { Text("날짜 선택") })

            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val newOrUpdatedTodo = TodoEntity(
                    id = todoId ?: 0,
                    title = text,
                    tag = tag,
                    memo = memo,
                    dueDate = dueDate
                )
                Button (onClick = {
                    if (todoId != null) {
                        viewModel.updateTodo(context, newOrUpdatedTodo)
                    } else {
                        viewModel.addTodo(context, newOrUpdatedTodo)
                    }
                    navController.popBackStack()
                }) {
                    if (todoId != null) {
                        Text("수정")
                    } else {
                        Text("추가")
                    }
                }
                if (todoId != null) {
                    Button(onClick = {
                        viewModel.deleteTodo(context, newOrUpdatedTodo)
                        navController.popBackStack()
                    }) {
                        Text("삭제")
                    }
                }
                Button(onClick = {
                    if (todoId != null) {
                        viewModel.testAlarmAfterMinutes(context, todoId, "테스트 알람", 5)
                    }
                    navController.popBackStack()
                }) {
                    Text("테스트")
                }
            }
        }
    }
}
