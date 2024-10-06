package com.example.eyecare.Opto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eyecare.Extra.AuthViewModel
import com.example.eyecare.Extra.LoadingAnimation
import com.example.eyecare.topBar.topBarId
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PreviewScreen(navController: NavController, patientId: String, screenName: String) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var patientDetails by remember { mutableStateOf<Patient?>(null) }
    var examinationDetails by remember { mutableStateOf<Map<String, Any>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    // Fetch patient and examination details from Firestore
    LaunchedEffect(patientId) {
        db.collection("patients").document(patientId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    patientDetails = document.toObject(Patient::class.java)
                } else {
                    errorMessage = "Patient not found"
                }
                // Fetching examination details
                db.collection("optoNewGlasses").document(patientId).get()
                    .addOnSuccessListener { doc ->
                        examinationDetails = doc.data
                        isLoading = false
                    }
                    .addOnFailureListener { exception ->
                        errorMessage = "Failed to fetch examination details: ${exception.message}"
                        isLoading = false
                    }
            }
            .addOnFailureListener { exception ->
                errorMessage = "Failed to fetch patient details: ${exception.message}"
                isLoading = false
            }
    }

    Scaffold(
        topBar = {
            topBarId(
                fullName = "Optometrist",
                position = "Optometrist",
                screenName = screenName, // Display screen name in the top bar
                authViewModel = AuthViewModel(),
                navController = navController
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (isLoading) {
                LoadingAnimation(
                    circleSize = 25.dp,
                    spaceBetween = 10.dp,
                    travelDistance = 20.dp
                )
            } else if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                )
            } else {
                patientDetails?.let { patient ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Display patient details
                            Text(text = "Patient Name: ${patient.name}")
                            Text(text = "ID: ${patient.id}")
                            Text(text = "Age: ${patient.age} Years")
                            Text(text = "Screen: $screenName") // Display the screen name

                            // Display new glass prescription details
                            examinationDetails?.let { details ->
                                Text(text = "Left Glass Prescription: ${details["Left Glass Prescription"]}")
                                Text(text = "Right Glass Prescription: ${details["Right Glass Prescription"]}")
                            }
                        }
                    }
                }
            }
        }
    }
}
