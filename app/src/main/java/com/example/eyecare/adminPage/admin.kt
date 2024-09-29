package com.example.eyecare.adminPage

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eyecare.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun adminPage(navController: NavController) {
    Scaffold(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Box(modifier = Modifier.padding(10.dp)){
                Image(
                    painter = painterResource(id = R.drawable.addfriend),
                    contentDescription = "Add User",
                    modifier = Modifier.padding(5.dp)
                        .clickable { navController.navigate("signup/{userId}") }
                )
            }

            Box(modifier = Modifier.padding(10.dp)){
                Image(
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = "Edit User",
                    modifier = Modifier.padding(5.dp).clickable { navController.navigate("editUserPage") }
                )
            }
        }
    }
}