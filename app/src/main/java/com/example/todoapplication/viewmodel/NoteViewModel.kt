package com.example.todoapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapplication.data.Note
import com.example.todoapplication.repository.NoteRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class NoteViewModel (
    private val repo: NoteRepository = NoteRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()
    private var lastDoc: DocumentSnapshot? = null
    private val uid: String?
        get() = auth.currentUser?.uid
    /** 실시간 리스너 시작 */
    fun startListening() {
        val u = uid ?: return
        repo.listenNotes(u).onEach { list ->
            _notes.value = list
        }.launchIn(viewModelScope)
    }
    /** 페이지 로드(옵션) */
    fun loadFirstPage() = viewModelScope.launch {
        val u = uid ?: return@launch
        val (list, last) = repo.loadFirstPage(u)
        _notes.value = list
        lastDoc = last
    }
    fun loadNextPage() = viewModelScope.launch {
        val u = uid ?: return@launch
        val l = lastDoc ?: return@launch
        val (list, next) = repo.loadNextPage(u, l)
        _notes.value = _notes.value + list
        lastDoc = next
    }
    fun add(title: String, content: String) = viewModelScope.launch {
        val u = uid ?: return@launch
        repo.add(u, title, content)
    }
    fun update(id: String, title: String?, content: String?) = viewModelScope.launch {
        val u = uid ?: return@launch
        repo.update(u, id, title, content)
    }
    fun delete(id: String) = viewModelScope.launch {
        val u = uid ?: return@launch
        repo.delete(u, id)
    }
}