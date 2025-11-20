package com.example.todoapplication.screens.auth

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.todoapplication.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

@SuppressLint("VisibleForTests")
@Composable
fun GoogleSignScreen(onSignedIn: () -> Unit) {
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val scope = rememberCoroutineScope()
// 1) Credential Manager 요청 옵션 구성
    val googleIdOption = remember {
        GetSignInWithGoogleOption.Builder(
            /* serverClientId = */ context.getString(R.string.my_web_client_id)
        ).build()
    }
    val credentialManager = remember { CredentialManager.create(context) }
    val request = remember {
        GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }
    var msg by remember { mutableStateOf<String?>(null) }

    Column (Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Button (onClick = {
            scope.launch {
                try {
// 2) 사용자에게 Google 선택 UI 표시 (CM bottom sheet / 버튼)
                    val result = credentialManager.getCredential(context, request)
                    val cred = result.credential
// 3) Google ID 토큰 꺼내기
                    val googleIdTokenCred = GoogleIdTokenCredential.createFrom(cred.data)
                    val idToken = googleIdTokenCred.idToken
// 4) FirebaseAuth로 교환
                    val firebaseCred = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(firebaseCred)
                        .addOnSuccessListener { onSignedIn() }
                        .addOnFailureListener { e -> msg = "Firebase 로그인 실패: ${e.message}" }
                } catch (e: GetCredentialException) {
                    msg = "취소/오류: ${e::class.simpleName}"
                } catch (e: Exception) {
                    msg = "예상치 못한 오류: ${e.message}"
                }
            }
        }) { Text("Sign in with Google") }
        msg?.let{ Text(it, color = MaterialTheme.colorScheme.error) }
    }
}