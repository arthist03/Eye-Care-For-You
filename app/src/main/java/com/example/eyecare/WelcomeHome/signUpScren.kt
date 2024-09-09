package com.example.eyecare.WelcomeHome

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceTheme
import androidx.glance.GlanceTheme.colors
import androidx.navigation.NavController
import com.example.eyecare.Extra.AuthState
import com.example.eyecare.Extra.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController, authViewModel: AuthViewModel) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var selectedRole by rememberSaveable { mutableStateOf("") }
    var selectedTitle by rememberSaveable { mutableStateOf("Mr.") }
    var passwordStrength by remember { mutableStateOf(PasswordStrength.Weak) }
    var passwordsMatch by remember { mutableStateOf(true) }


    //Signup Text
    val infiniteTransition = rememberInfiniteTransition()
    val textColor by infiniteTransition.animateColor(
        initialValue = Color.Black,
        targetValue = Color.White,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val icons = listOf(
        Icons.Filled.KeyboardArrowLeft,
        Icons.Filled.KeyboardArrowRight,
        Icons.Filled.KeyboardArrowUp,
        Icons.Filled.KeyboardArrowDown
    )
    var currentIndex by remember { mutableStateOf(0) }

    // Smooth transition between icons
    val transitionAnimation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Calculate the icon index based on the animation progress
    val animationProgress = transitionAnimation.value
    val nextIndex = (currentIndex + 1) % icons.size
    val interpolatedIndex = (animationProgress * icons.size).toInt()
    val displayedIcon = if (interpolatedIndex % icons.size == 0) icons[currentIndex] else icons[nextIndex]

    // Update the index when the animation completes a cycle
    LaunchedEffect(animationProgress) {
        if (animationProgress >= 1f) {
            currentIndex = (currentIndex + 1) % icons.size
        }
    }




    var roleDropdownExpanded by remember { mutableStateOf(false) }
    var titleDropdownExpanded by remember { mutableStateOf(false) }

    val roles = listOf("HOD", "Optometrist", "Doctor", "Receptionist")
    val title = listOf("Mr.", "Mrs.", "Ms.", "Dr.")
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.RedirectToHOD -> navController.navigate("hodScreen")
            is AuthState.RedirectToDoctor -> navController.navigate("doctorScreen")
            is AuthState.RedirectToOptometrist -> navController.navigate("optometristScreen")
            is AuthState.RedirectToReceptionist -> navController.navigate("receptionistScreen")
            is AuthState.Error -> Toast.makeText(
                context,
                (authState.value as AuthState.Error).message,
                Toast.LENGTH_SHORT
            ).show()
            else -> Unit
        }
    }


    fun checkPasswordStrength(password: String): PasswordStrength {
        val lowercase = Regex("[a-z]").containsMatchIn(password)
        val uppercase = Regex("[A-Z]").containsMatchIn(password)
        val digit = Regex("[0-9]").containsMatchIn(password)
        val special = Regex("[^A-Za-z0-9]").containsMatchIn(password)

        return when {
            password.length < 8 -> PasswordStrength.Weak
            lowercase && uppercase && digit && special -> PasswordStrength.Strong
            lowercase && uppercase && (digit || special) -> PasswordStrength.Medium
            else -> PasswordStrength.Weak
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Canvas for gradient background
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFFB0E0E6), Color.Transparent),
                    radius = 3500f
                ),
                center = Offset(x = size.width*0.75f, y = size.height / 2),
                radius = size.minDimension*2.5f
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .offset(y = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {


            Row (horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically){

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
                    text = "Sign Up",
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 56.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
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
                        focusedLabelColor = Color.Black,
                        focusedPlaceholderColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White
                    ),
                    maxLines = 1
                )


                var passwordVisible by remember { mutableStateOf(false) }
                passwordStrength = checkPasswordStrength(password)
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
                        focusedLabelColor = Color.Black,
                        focusedPlaceholderColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = when (passwordStrength) {
                            PasswordStrength.Strong -> Color.Green
                            PasswordStrength.Medium -> Color(0xFFFFA500) // Orange
                            PasswordStrength.Weak -> Color.Red
                        },
                        unfocusedBorderColor = Color.White
                    ),
                    maxLines = 1
                )


                passwordsMatch = password == confirmPassword
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(15.dp)),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Confirm Password Icon"
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = !passwordsMatch,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        focusedPlaceholderColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = if (passwordsMatch) Color.Green else Color.Red,
                        unfocusedBorderColor = if (passwordsMatch) Color.Green else Color.Red
                    ),
                    maxLines = 1
                )
                if (!passwordsMatch) {
                    Text(
                        text = "Passwords do not match",
                        color = Color.Red,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }


                ExposedDropdownMenuBox(
                    expanded = titleDropdownExpanded,
                    onExpandedChange = { titleDropdownExpanded = !titleDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedTitle,
                        onValueChange = { selectedTitle = it },
                        readOnly = true,
                        label = { Text("Title") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = titleDropdownExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(15.dp)),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            focusedPlaceholderColor = Color.Black,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = titleDropdownExpanded,
                        onDismissRequest = { titleDropdownExpanded = false }
                    ) {
                        title.forEach { title ->
                            DropdownMenuItem(
                                text = { Text(title) },
                                onClick = {
                                    selectedTitle = title
                                    titleDropdownExpanded = false
                                }
                            )
                        }
                    }
                }


                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(15.dp)),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Name Icon"
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        focusedPlaceholderColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White
                    ),
                    maxLines = 1
                )


                ExposedDropdownMenuBox(
                    expanded = roleDropdownExpanded,
                    onExpandedChange = { roleDropdownExpanded = !roleDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedRole,
                        onValueChange = { selectedRole = it },
                        readOnly = true,
                        label = { Text("Role") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleDropdownExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(15.dp)),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            focusedPlaceholderColor = Color.Black,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = roleDropdownExpanded,
                        onDismissRequest = { roleDropdownExpanded = false }
                    ) {
                        roles.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role) },
                                onClick = {
                                    selectedRole = role
                                    roleDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(15.dp)),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone Icon"
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        focusedPlaceholderColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White
                    ),
                    maxLines = 1
                )


            ElevatedButton(
                onClick = {
                    val fullName = "$selectedTitle $name"
                    if (passwordsMatch && passwordStrength == PasswordStrength.Strong) {
                        authViewModel.signup(
                            email,
                            password,
                            confirmPassword,
                            fullName,
                            phone,
                            selectedRole
                        )
                    }
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(40.dp))
                    .width(150.dp)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
                elevation = ButtonDefaults.elevatedButtonElevation(10.dp)
            ) {
                Text(text = "Register", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

enum class PasswordStrength {
    Weak, Medium, Strong
}