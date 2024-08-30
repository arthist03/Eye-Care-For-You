package com.example.eyecare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.eyecare.WelcomeHome.LoginScreen
import com.example.eyecare.WelcomeHome.homeScreen
import com.example.eyecare.ui.theme.EyeCareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EyeCareTheme {
                LoginScreen()
            }
        }
    }
}