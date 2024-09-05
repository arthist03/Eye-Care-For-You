package com.example.eyecare.Extra

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eyecare.Trail.pageDoctors
import com.example.eyecare.Trail.pageHOD
import com.example.eyecare.Trail_Pages.pageOpto
import com.example.eyecare.Trail_Pages.pageReception
import com.example.eyecare.WelcomeHome.LoginScreen
import com.example.eyecare.WelcomeHome.homeScreen
import com.example.eyecare.WelcomeHome.SignUpScreen

@Composable
fun AppNavHost(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { homeScreen(navController, authViewModel) }
        composable("login") { LoginScreen(navController,authViewModel) }
        composable("signup") { SignUpScreen(navController, authViewModel) }
        composable("doctorScreen") { pageDoctors(navController, authViewModel) }
        composable("hodScreen") { pageHOD(navController,authViewModel) }
        composable("optometristScreen") { pageOpto(navController, authViewModel) }
        composable("receptionistScreen") { pageReception(navController, authViewModel) }
    }
}