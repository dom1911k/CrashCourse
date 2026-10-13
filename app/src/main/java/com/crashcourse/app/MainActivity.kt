package com.crashcourse.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.crashcourse.app.data.ProgressStore
import com.crashcourse.app.ui.CrashCourseApp
import com.crashcourse.app.ui.CrashCourseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val store = ProgressStore(applicationContext)
        setContent {
            CrashCourseTheme {
                CrashCourseApp(store)
            }
        }
    }
}
