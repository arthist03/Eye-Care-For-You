@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.eyecare.WelcomeHome

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eyecare.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
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
                        .clickable { /* Handle Back */ }
                        .size(50.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Log In", fontSize = 24.sp)
            }
        Row {
            Image(painter = painterResource(id = R.drawable.login), contentDescription ="Side logo")
        }

            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(30.dp)
                    .offset(y = 450.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Bottom,
            ){
                // Email TextField
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        containerColor = Color.White,
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password TextField
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        containerColor = Color.White,
                        )
                )

                Spacer(modifier = Modifier.height(72.dp))

                ElevatedButton(onClick = { /*TODO*/ },
                    colors = ButtonDefaults.elevatedButtonColors(Color(0xFF1F86FF)),
                    modifier = Modifier
                        .height(50.dp)
                        .width(170.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 20.dp,
                        pressedElevation = 15.dp)
                ) {
                    Text(text = "Log in", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }
        }
    }

@Preview
@Composable
private fun LoginPrev() {
    LoginScreen()
}
