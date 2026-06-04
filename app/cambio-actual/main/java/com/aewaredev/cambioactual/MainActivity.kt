package com.aewaredev.cambioactual

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.aewaredev.cambioactual.ui.navigation.NavApp
import com.aewaredev.cambioactual.ui.theme.CambioActualTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CambioActualTheme {
                NavApp(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
