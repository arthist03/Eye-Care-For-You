package com.example.eyecare.WelcomeHome

import android.widget.Toast
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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

@Composable
fun SignUpScreen(navController: NavController, authViewModel: AuthViewModel) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var selectedRole by rememberSaveable { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedTitle by rememberSaveable { mutableStateOf("Mr.") }
    val roles = listOf("HOD", "Optometrist", "Doctor", "Receptionist")

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

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            drawCircle(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF39A6EE), Color(0xFF1242E6))
                ),
                radius = height / 1.2f,
                center = Offset(x = width / 2, y = height + 50)
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
                Text(text = "Sign Up", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Row (modifier = Modifier
                .fillMaxWidth()
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                    .offset(y = 180.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                item {
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
                            focusedLabelColor = Color.Black,
                            focusedPlaceholderColor = Color.Black,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White
                        ),
                        maxLines = 1
                    )
                }

                item {
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
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White
                        ),
                        maxLines = 1
                    )
                }

                item {
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Confirm Password Icon"
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
                }

                item {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(color = Color.White)
                                .clip(
                                    RoundedCornerShape(15.dp)
                                )) {
                            Text(text = "Title", fontSize = 18.sp)
                            listOf("Mr.", "Mrs.", "Ms.", "Dr.").forEach { title ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedTitle == title,
                                        onClick = { selectedTitle = title }
                                    )
                                    Text(text = title, modifier = Modifier.padding(end = 8.dp))
                                }
                            }
                        }

                }

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Name Icon"
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
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
                }

                item {
                    Box {
                        OutlinedTextField(
                            value = selectedRole,
                            onValueChange = { selectedRole = it },
                            label = { Text("Select Role") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AccountBox,
                                    contentDescription = "Role Icon"
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = "Dropdown Arrow",
                                    modifier = Modifier.clickable { expanded = !expanded }
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
                            )
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(.865f)
                        ) {
                            roles.forEach { role ->
                                DropdownMenuItem(
                                    text = { Text(role) },
                                    onClick = {
                                        selectedRole = role
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
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
                }

                item {
                    ElevatedButton(
                        onClick = {
                            val fullName = "$selectedTitle $name"
                            authViewModel.signup(
                                email,
                                password,
                                confirmPassword,
                                fullName,
                                phone,
                                selectedRole
                            )
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(40.dp))
                            .width(150.dp)
                            .height(40.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 20.dp,
                            pressedElevation = 15.dp
                        )
                    ) {
                        Text(text = "Sign Up", fontSize = 20.sp, color = Color.Black)
                    }
                }
            }
        }
    }