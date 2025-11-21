package com.example.todoapplication.screens.note

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todoapplication.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(viewModel: NoteViewModel) {
    val notes by viewModel.notes.collectAsState()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
// 실시간 리스너 시작(또는 페이징 모드로 바꾸려면 loadFirstPage() 호출)
        viewModel.startListening()
    }
    Scaffold(
        topBar = { TopAppBar(title = { Text("Firestore Notes") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.add(title, content); title=""; content="" }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) { OutlinedTextField(
            value = title, onValueChange = { title = it },
            label = { Text("Title") }, modifier = Modifier.fillMaxWidth()
        )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = content, onValueChange = { content = it },
                label = { Text("Content") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(notes, key = { it.id }) { n ->
                    ListItem(
                        headlineContent = { Text(n.title) },
                        supportingContent = { Text(n.content) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.update(n.id, title = n.title + " ✓", content = null)
                            }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}