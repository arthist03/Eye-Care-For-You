package com.example.eyecare.WelcomeHome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eyecare.Extra.AuthViewModel
import com.example.eyecare.R

@Composable
fun homeScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 25.dp,
                vertical = 10.dp,
            ),
        contentAlignment = Alignment.BottomCenter // Align content to the bottom center
    ) {
        // Draw the circles
        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val width = size.width
            val height = size.height

            drawCircle(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFB0E0E6), Color(0xFF2CE8FF))
                ),
                radius = height / 1.8f,
                center = Offset(x = width / 2, y = height + 50)
            )

            drawCircle(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFB0E0E6), Color(0xFF84F1FF))
                ),
                radius = height / 2.7f,
                center = Offset(x = width / 2, y = height - 50)
            )

            drawCircle(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFB0E0E6), Color(0xFFB0E0E6))
                ),
                radius = height / 3.7f,
                center = Offset(x = width / 2, y = height - 10)
            )
        }

        // Content above the circles
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.login),
                contentDescription = "Login Image for Home Screen",
                modifier = Modifier.padding(5.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "Eye - Care For You", fontSize = 34.sp, color = Color(0xFF005780))

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "Precision Meets Compassion")
        }

        // Buttons at the bottom, aligned on top of the circles
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp), // Adjust padding to position on circles
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ElevatedButton(onClick = { navController.navigate("login") },
                colors = ButtonDefaults.elevatedButtonColors(Color(0xFF3C8A94)),
                modifier = Modifier.height(50.dp).width(300.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 20.dp,
                    pressedElevation = 15.dp)
            ) {
                Text(text = "Log in", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }
    }
}

