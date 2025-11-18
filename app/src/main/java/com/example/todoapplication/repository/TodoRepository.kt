package com.example.todoapplication.repository

import com.example.todoapplication.data.TodoDao
import com.example.todoapplication.data.TodoEntity
import javax.inject.Inject

class TodoRepository @Inject constructor(private val dao: TodoDao) {
    val allTodos = dao.getAll()

    suspend fun insert(todo: TodoEntity):Long{
        return dao.insert(todo)
    }
    suspend fun update(todo: TodoEntity) = dao.update(todo)
    suspend fun delete(todo: TodoEntity) = dao.delete(todo)
}
