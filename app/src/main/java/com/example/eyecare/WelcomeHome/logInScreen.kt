@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.eyecare.WelcomeHome

import android.widget.Toast
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eyecare.Extra.AuthState
import com.example.eyecare.Extra.AuthViewModel
import kotlin.random.Random

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    // Color Animation
    val infiniteTransition = rememberInfiniteTransition()

    // Font Family Animation
    val fontFamilies = listOf(
        FontFamily.SansSerif,
        FontFamily.Monospace,
        FontFamily.Serif,
        FontFamily.Cursive,
        FontFamily.Default
    )
    val fontFamilyNames = listOf("SansSerif", "Monospace", "Serif","Cursive", "Default")
    val transition = rememberInfiniteTransition()
    val fontFamilyIndex by transition.animateValue(
        initialValue = 0,
        targetValue = fontFamilies.size - 1,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val currentFontFamily = fontFamilies[fontFamilyIndex]

    // Icon Animation
    val icons = listOf(
        Icons.Filled.KeyboardArrowLeft,
        Icons.Filled.KeyboardArrowRight,
        Icons.Filled.KeyboardArrowUp,
        Icons.Filled.KeyboardArrowDown
    )
    var currentIndex by remember { mutableStateOf(0) }
    val transitionAnimation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val animationProgress = transitionAnimation.value
    val nextIndex = (currentIndex + 1) % icons.size
    val interpolatedIndex = (animationProgress * icons.size).toInt()
    val displayedIcon =
        if (interpolatedIndex % icons.size == 0) icons[currentIndex] else icons[nextIndex]
    LaunchedEffect(animationProgress) {
        if (animationProgress >= 1f) {
            currentIndex = (currentIndex + 1) % icons.size
        }
    }

    LaunchedEffect(Unit) {
        authViewModel.checkAuthState()
    }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.RedirectToHOD -> {
                Toast.makeText(context, "Redirecting to HOD Screen", Toast.LENGTH_SHORT).show()
                navController.navigate("hodScreen") {
                    popUpTo("login") { inclusive = true }
                }
            }

            is AuthState.RedirectToDoctor -> {
                Toast.makeText(context, "Redirecting to Doctor Screen", Toast.LENGTH_SHORT).show()
                navController.navigate("doctorScreen") {
                    popUpTo("login") { inclusive = true }
                }
            }

            is AuthState.RedirectToOptometrist -> {
                Toast.makeText(context, "Redirecting to Optometrist Screen", Toast.LENGTH_SHORT)
                    .show()
                navController.navigate("OptoPatients") {
                    popUpTo("login") { inclusive = true }
                }
            }

            is AuthState.RedirectToReceptionist -> {
                Toast.makeText(context, "Redirecting to Receptionist Screen", Toast.LENGTH_SHORT)
                    .show()
                navController.navigate("receptionistScreen") {
                    popUpTo("login") { inclusive = true }
                }
            }

            is AuthState.RedirectToAdmin -> {
                Toast.makeText(context, "Redirecting to Admin Screen", Toast.LENGTH_SHORT)
                    .show()
                navController.navigate("adminPage") {
                    popUpTo("login") { inclusive = true }
                }
            }

            is AuthState.Error -> {
                Toast.makeText(
                    context,
                    (authState.value as AuthState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                Toast.makeText(context, "Awaiting login", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFFB0E0E6), Color.Transparent),
                    radius = 3500f
                ),
                center = Offset(x = size.width * 0.75f, y = size.height / 2),
                radius = size.minDimension * 2.5f
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp)
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = displayedIcon,
                        contentDescription = "Arrow",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(50.dp)
                            .alpha(transitionAnimation.value)
                            .clickable { navController.navigate("home") }
                    )

                    Text(
                        text = "Log In",
                        color = Color.Black,
                        fontFamily = currentFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 56.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(150.dp))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Email TextField
                    var email by rememberSaveable {
                        mutableStateOf("")
                    }
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(15.dp)),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(15.dp)),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password Icon"
                            )
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val image =
                                if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = image,
                                    contentDescription = "Toggle Password Visibility"
                                )
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
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
                }
            }
        }
    }
}
