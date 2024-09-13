package com.example.eyecare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.eyecare.Extra.AppNavHost
import com.example.eyecare.Extra.AuthViewModel
import com.example.eyecare.Extra.LoadingAnimation
import com.example.eyecare.WelcomeHome.LoginScreen
import com.example.eyecare.WelcomeHome.homeScreen
import com.example.eyecare.topBar.topBarId
import com.example.eyecare.ui.theme.EyeCareTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    private val auth:FirebaseAuth by lazy { Firebase.auth }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authViewModel: AuthViewModel by viewModels()
        setContent {
            EyeCareTheme {
                AppNavHost(authViewModel = authViewModel)
            }
        }
    }
}