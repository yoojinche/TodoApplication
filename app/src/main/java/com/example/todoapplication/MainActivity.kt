package com.example.todoapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.todoapplication.navigation.NavGraph
import com.example.todoapplication.notification.NotificationHelper
import com.example.todoapplication.ui.theme.TodoApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        NotificationHelper.createChannel(this)
        askNotificationPermission()

        // 알림에서 온 경우에만 들어있는 값
        val todoIdFromNotification = intent?.getIntExtra("todoId", -1) ?: -1

        setContent {
            TodoApplicationTheme {
                val nav = rememberNavController()
                val context = LocalContext.current

                LaunchedEffect(todoIdFromNotification) {
                    if (todoIdFromNotification != -1){
                        nav.navigate("addEdit?id=${todoIdFromNotification}")
                    }
                }

                NavGraph(context, nav)
            }
        }
    }

    private fun askNotificationPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ){
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
    }
}

