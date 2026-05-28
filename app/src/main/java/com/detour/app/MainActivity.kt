package com.detour.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.detour.app.ui.navigation.DetourNavGraph
import com.detour.app.ui.theme.DetourTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DetourTheme {
                DetourNavGraph()
            }
        }
    }
}
