package com.example.eyecare.reception

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eyecare.Extra.AuthViewModel
import com.example.eyecare.topBar.topBarId

@Composable
fun PageReception(navController: NavController, authViewModel: AuthViewModel) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    // Perform search and handle auth state
    fun performSearch(query: String) {
        if (query.isNotBlank()) {
            // Database search logic
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFEBF5FB), Color(0xFFB3BBC4)))),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar placement
        topBarId(
            name = "Receptionist",
            position = "Reception",
            screenName = "Reception Screen",
            authViewModel,
            navController
        )

        Column(
            modifier = Modifier.fillMaxSize()
                .padding(top=70.dp)
        ) {


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.elevatedCardElevation(12.dp), // Increased elevation
                shape = RoundedCornerShape(15.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(24.dp), // Increased padding
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    // Enter patient details button
                    ElevatedButton(
                        onClick = { navController.navigate("patientDetails/{patientId}") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF39C12)) // Updated parameter
                    ) {
                        Text(text = "Enter Patient Details Manually", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Capture image button
                    ElevatedButton(
                        onClick = { /* handle capture image */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF39C12)) // Updated parameter
                    ) {
                        Text(text = "Capture Image", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Search bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            performSearch(it)
                        },
                        label = { Text("Search by Name, Phone, or ID") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 16.sp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Search results list
                    LazyColumn {
                        items(searchResults) { result ->
                            val patientId = result["id"] as? String ?: ""
                            val patientName = result["name"] as? String ?: ""

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clickable {
                                        navController.navigate("patientDetails/${patientId}")
                                    },
                                elevation = CardDefaults.elevatedCardElevation(8.dp) // Updated elevation
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                ) {
                                    Text(text = "Name: $patientName", fontSize = 16.sp)
                                    Text(text = "Phone: ${result["phone"]}", fontSize = 16.sp)
                                    Text(text = "ID: $patientId", fontSize = 16.sp)
                                }
                            }


                        }
                    }




                }
            }
        }
    }
}
