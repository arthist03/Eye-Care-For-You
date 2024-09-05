@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.eyecare.WelcomeHome

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eyecare.Extra.AuthState
import com.example.eyecare.Extra.AuthViewModel
import com.example.eyecare.R
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.RedirectToHOD -> {
                Toast.makeText(context, "Redirecting to HOD Screen", Toast.LENGTH_SHORT).show()
                navController.navigate("hodScreen")
            }
            is AuthState.RedirectToDoctor -> {
                Toast.makeText(context, "Redirecting to Doctor Screen", Toast.LENGTH_SHORT).show()
                navController.navigate("doctorScreen")
            }
            is AuthState.RedirectToOptometrist -> {
                Toast.makeText(context, "Redirecting to Optometrist Screen", Toast.LENGTH_SHORT).show()
                navController.navigate("optometristScreen")
            }
            is AuthState.RedirectToReceptionist -> {
                Toast.makeText(context, "Redirecting to Receptionist Screen", Toast.LENGTH_SHORT).show()
                navController.navigate("receptionistScreen")
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> {
                // No action, could still be loading or uninitialized
                Toast.makeText(context, "Awaiting login", Toast.LENGTH_SHORT).show()
            }
        }
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 15.dp)
    ) {
        // Background with circles and gradient
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            drawCircle(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF39A6EE), Color(0xFF39A6EE))
                ),
                radius = height / 1.2f,
                center = Offset(x = width / 2, y = height + 50)
            )

            drawCircle(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1242E6), Color(0xFF39A6EE))
                ),
                radius = height / 1.5f,
                center = Offset(x = width / 2, y = height - 50)
            )

            drawCircle(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF000825), Color(0xFF001F8B))
                ),
                radius = height / 1.8f,
                center = Offset(x = width / 2, y = height - 10)
            )
        }

        // Circle behind the back arrow and "Log In" text
        Canvas(modifier = Modifier
            .size(200.dp)
            .offset(x = (-50).dp, y = (-80).dp)
            .blur(50.dp)
            .clip(CircleShape)
        ) {
            drawCircle(
                color = Color(0xFF2878EB),
                radius = size.minDimension / 2,
                center = Offset(size.width / 2, size.height / 2),
                alpha = 0.8f // Adjust alpha for blur effect
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                modifier = Modifier
                    .clickable { navController.navigate("home") }
                    .size(50.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Log In", fontSize = 24.sp)
        }

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Image(
                painter = painterResource(id = R.drawable.login),
                contentDescription ="Side logo",
                Modifier.size(150.dp)
            )
        }

        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp)
                .offset(y = 450.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            // Email TextField

            var email by rememberSaveable {
                mutableStateOf("")
            }
            OutlinedTextField(
                value = email,
                onValueChange = {email = it},
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    containerColor = Color.White,
                    focusedTextColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password TextField
            var password by rememberSaveable { mutableStateOf("") }
            OutlinedTextField(
                value = password,
                onValueChange = {password = it},
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    containerColor = Color.White,
                    focusedTextColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black,
                )
            )

            Spacer(modifier = Modifier.height(72.dp))

            ElevatedButton(
                onClick = { authViewModel.login(email, password) },
                colors = ButtonDefaults.elevatedButtonColors(Color(0xFF1F86FF)),
                modifier = Modifier
                    .height(50.dp)
                    .width(170.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 20.dp,
                    pressedElevation = 15.dp
                )
            ) {
                Text(
                    text = "Log in",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text(text = "Don't have an account? ", color = Color.White)
                Text(text = "SignUp", color = Color.White, modifier = Modifier.clickable { navController.navigate("signup") })

            }
        }
    }
}