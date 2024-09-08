package com.example.eyecare.reception

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eyecare.Extra.AuthState
import com.example.eyecare.Extra.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PageReception(navController: NavController, authViewModel: AuthViewModel) {
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.UnAuthenticated -> navController.navigate("home")
            is AuthState.Error -> Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    fun performSearch(query: String) {
        if (query.isNotBlank()) {
            db.collection("patients")
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + "\uf8ff")
                .get()
                .addOnSuccessListener { documents ->
                    searchResults = documents.map { it.data }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Search failed", Toast.LENGTH_SHORT).show()
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Gray),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Welcome To Reception Screen")
        Spacer(modifier = Modifier.height(20.dp))

        ElevatedButton(onClick = { navController.navigate("patientDetails") }) {
            Text(text = "Enter Patient Details Manually")
        }

        Spacer(modifier = Modifier.height(20.dp))

        ElevatedButton(onClick = {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            (context as? Activity)?.startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }) {
            Text(text = "Capture Image")
        }

        Spacer(modifier = Modifier.height(20.dp))

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

        LazyColumn {
            items(searchResults) { result ->
                val patientId = result["id"] as? String ?: ""
                val patientName = result["name"] as? String ?: ""
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color.White)
                        .clickable {
                            navController.navigate("patientDetails/${patientId}")
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Name: $patientName", fontSize = 16.sp)
                    Text(text = "Phone: ${result["phone"]}", fontSize = 16.sp)
                    Text(text = "ID: $patientId", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Sign out",
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier.clickable { authViewModel.signout() }
        )
    }
}

const val REQUEST_IMAGE_CAPTURE = 1
