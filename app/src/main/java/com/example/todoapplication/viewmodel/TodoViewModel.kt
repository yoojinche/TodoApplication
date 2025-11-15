package com.example.todoapplication.viewmodel

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapplication.data.TodoEntity
import com.example.todoapplication.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TodoViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {
    val todoList: StateFlow<List<TodoEntity>> =
        repository.allTodos.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun scheduleTodoNotification(context: Context, todoId: Int, title: String, dueDate: String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()){
                val intent = Intent(Settings.ACTION_REQUEST_EXACT_ALARM)
                context.startActivity(intent)
                return
            }
        }
    }

    fun addTodo(todo: TodoEntity) {
        viewModelScope.launch {
            repository.insert(todo)
        }
    }

    fun updateTodo(todo: TodoEntity) {
        viewModelScope.launch {
            repository.update(todo)
        }
    }

    fun deleteTodo(todo: TodoEntity) {
        viewModelScope.launch {
            repository.delete(todo)
        }
    }
}
