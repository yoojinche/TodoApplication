package com.example.todoapplication.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoginMenuScreen(
    onEmailClick: () -> Unit,
    onGoogleClick: () -> Unit
) {
    Scaffold { padding ->
        Column(
                modifier = Modifier .fillMaxSize() .padding(padding)
                    .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
        ) {
            Text("로그인 방법을 선택하세요",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(24.dp))
            Button(onClick = onEmailClick,
                    modifier = Modifier.fillMaxWidth()
            ) {
                Text("이메일/비밀번호 로그인")
        }
            Spacer(Modifier.height(12.dp))
            Button(onClick = onGoogleClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Google 로그인")
            }
        }
    }
}