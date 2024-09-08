package com.example.eyecare.Opto


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eyecare.Extra.AuthState
import com.example.eyecare.Extra.AuthViewModel

@Composable
fun pageOpto(navController: NavController, authViewModel: AuthViewModel) {
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.UnAuthenticated-> navController.navigate("home")
            is AuthState.Error -> Toast.makeText(context, (authState.value as AuthState.Error).message,Toast.LENGTH_SHORT)
            else -> Unit
        }
    }

    Column (modifier = Modifier
        .fillMaxSize()
        .background(color = Color.Gray),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center)
    {
        Text(text = "Welcome To Opto Screen")
        Spacer(modifier = Modifier.height(20.dp))

        ElevatedButton(onClick = { navController.navigate("OptoPatients") }) {
            Text(text = "Check Patients")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Sign out", fontSize = 15.sp, color = Color.Black ,modifier = Modifier.clickable { authViewModel.signout()})
    }
}