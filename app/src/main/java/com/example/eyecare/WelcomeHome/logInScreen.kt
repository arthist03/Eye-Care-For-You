@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.eyecare.WelcomeHome

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.input.VisualTransformation
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

    LaunchedEffect(Unit) {
        authViewModel.checkAuthState() // This checks if the user is already authenticated
    }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.RedirectToHOD -> {
                Toast.makeText(context, "Redirecting to HOD Screen", Toast.LENGTH_SHORT).show()
                navController.navigate("hodScreen") {
                    popUpTo("login") { inclusive = true } // Remove login from back stack
                }
            }
            is AuthState.RedirectToDoctor -> {
                Toast.makeText(context, "Redirecting to Doctor Screen", Toast.LENGTH_SHORT).show()
                navController.navigate("doctorScreen") {
                    popUpTo("login") { inclusive = true }
                }
            }
            is AuthState.RedirectToOptometrist -> {
                Toast.makeText(context, "Redirecting to Optometrist Screen", Toast.LENGTH_SHORT).show()
                navController.navigate("optometristScreen") {
                    popUpTo("login") { inclusive = true }
                }
            }
            is AuthState.RedirectToReceptionist -> {
                Toast.makeText(context, "Redirecting to Receptionist Screen", Toast.LENGTH_SHORT).show()
                navController.navigate("receptionistScreen") {
                    popUpTo("login") { inclusive = true }
                }
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

        Box(
            modifier = Modifier
                .size(230.dp)  // Box size
                .offset(x = (-50).dp, y = (-70).dp)
                .blur(6.dp)
                .graphicsLayer {
                    clip = true  // Enable clipping to the shape
                    shape = CircleShape  // Define the shape as a circle
                }
                .background(color = Color(0xFF357BDF).copy(alpha = 0.6f), shape = CircleShape) // Add background color with shape and alpha
        )
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .offset(x = -10.dp, y = 20.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Back",
                    modifier = Modifier
                        .clickable { navController.navigate("home") }
                        .size(50.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Log in", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Row (modifier = Modifier.fillMaxWidth()
                .offset(y = -80.dp),
                horizontalArrangement = Arrangement.End
            ){

                Image(
                    painter = painterResource(id = R.drawable.login),
                    contentDescription = "Side logo",
                    modifier = Modifier
                        .size(150.dp)
                )
            }
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
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Icon"
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    focusedLabelColor = Color.White,
                    focusedPlaceholderColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White
                ),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password TextField
            var password by rememberSaveable { mutableStateOf("") }
            var passwordVisible by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password Icon"
                    )
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = "Toggle Password Visibility")
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    focusedLabelColor = Color.White,
                    focusedPlaceholderColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White
                ),
                maxLines = 1
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