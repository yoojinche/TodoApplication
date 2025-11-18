package com.example.todoapplication.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapplication.data.TodoEntity
import com.example.todoapplication.notification.AlarmReceiver
import com.example.todoapplication.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@HiltViewModel
class TodoViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {
    val todoList: StateFlow<List<TodoEntity>> =
        repository.allTodos.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun scheduleTodoNotification(context: Context, todoId: Int, title: String, dueDate: String){
        // 버전 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            // 권한이 없으면 알람 스케줄링을 진행하지 않고 종료
            if (!alarmManager.canScheduleExactAlarms()){
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
                return
            }
        }

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val dueDateMillis = LocalDateTime.parse(dueDate, formatter).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        // AlarmReceiver로 보낼 Intent 생성
        val intent = Intent(context, AlarmReceiver::class.java).apply{
            putExtra("title", title)
            putExtra("todoId", todoId)
        }
        // PendingIntent 생성
        val pendingIntent = PendingIntent.getBroadcast(
            context, todoId, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        // AlarmManger로 실제 알람 등록
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dueDateMillis, pendingIntent)
    }

    private fun cancelTodoNotification(context: Context, todoId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todoId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    fun testAlarmAfterMinutes(context: Context, todoId: Int, title: String, seconds: Long = 5) {
        cancelTodoNotification(context, todoId)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val dueDate = LocalDateTime.now()
            .plusSeconds(seconds)
            .format(formatter)

        scheduleTodoNotification(context, todoId, title, dueDate)
    }

    fun addTodo(context: Context, todo: TodoEntity) {
        viewModelScope.launch {
            val newId = repository.insert(todo).toInt()
            scheduleTodoNotification(context, newId, todo.title, todo.dueDate)
        }
    }

    fun updateTodo(context: Context,todo: TodoEntity) {
        viewModelScope.launch {
            repository.update(todo)
            cancelTodoNotification(context, todo.id)
            scheduleTodoNotification(context, todo.id, todo.title, todo.dueDate)
        }
    }

    fun deleteTodo(context: Context, todo: TodoEntity) {
        viewModelScope.launch {
            repository.delete(todo)
            cancelTodoNotification(context, todo.id)
        }
    }
}
