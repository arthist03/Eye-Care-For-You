package com.example.eyecare.Extra

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eyecare.Doctor.docGlassScreens.newGlassDoc
import com.example.eyecare.Doctor.docGlassScreens.withGlassDoc
import com.example.eyecare.Doctor.docGlassScreens.withoutGlassDoc
import com.example.eyecare.Doctor.doctorcatalog
import com.example.eyecare.HOD.hodcatalog
import com.example.eyecare.Opto.PatientCatalogPage
import com.example.eyecare.Opto.PreviewScreen
import com.example.eyecare.Opto.glassScreens.newGlassOpto
import com.example.eyecare.Opto.glassScreens.withGlassOpto
import com.example.eyecare.Opto.glassScreens.withoutGlassOpto
import com.example.eyecare.Trail.pageDoctors
import com.example.eyecare.Trail.pageHOD
import com.example.eyecare.Opto.pageOpto
import com.example.eyecare.reception.PageReception
import com.example.eyecare.WelcomeHome.LoginScreen
import com.example.eyecare.WelcomeHome.homeScreen
import com.example.eyecare.WelcomeHome.SignUpScreen
import com.example.eyecare.adminPage.adminPage
import com.example.eyecare.adminPage.editUser
import com.example.eyecare.reception.PatientDetailsScreen

import com.example.eyecare.topBar.screenName

@Composable
fun AppNavHost(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { homeScreen(navController) }
        composable("login") { LoginScreen(navController,authViewModel) }
        composable("doctorScreen") { pageDoctors(navController, authViewModel) }
        composable("hodScreen") { pageHOD(navController,authViewModel) }
        composable("adminPage") { adminPage(navController) }
        composable("editUserPage") { editUser(navController, authViewModel) }
        composable("optometristScreen") { pageOpto(navController, authViewModel) }
        composable("receptionistScreen") { PageReception(navController, authViewModel) }
        composable("OptoPatients"){ PatientCatalogPage(navController)}
        composable("hodPatients"){ hodcatalog(navController) }
        composable("doctorpatients"){ doctorcatalog(navController) }


        composable("signup/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            SignUpScreen(navController = navController,  authViewModel, userId = userId)
        }

        composable("patientDetails/{patientId}") { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId")
            PatientDetailsScreen(navController = navController, patientId = patientId)
        }

        composable("withGlassOpto/{patientId}") { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            withGlassOpto(navController = navController, patientId = patientId)
        }
            composable("withoutGlassOpto/{patientId}") { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            withoutGlassOpto(navController = navController, patientId = patientId)
        }
        composable("newGlassOpto/{patientId}") { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            newGlassOpto(navController = navController, patientId = patientId)
        }

        composable("withGlassDoc/{patientId}") { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            withGlassDoc(navController = navController, patientId = patientId)
        }
            composable("withoutGlassDoc/{patientId}") { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            withoutGlassDoc(navController = navController, patientId = patientId)
        }
        composable("newGlassDoc/{patientId}") { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            newGlassDoc(navController = navController, patientId = patientId)
        }

        composable("PreviewScreen/{patientId}") { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            PreviewScreen(navController = navController, patientId = patientId, screenName = "")
        }


    }
}