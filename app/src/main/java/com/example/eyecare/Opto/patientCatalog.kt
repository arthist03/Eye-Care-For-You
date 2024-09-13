package com.example.eyecare.Opto

import android.util.Log
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eyecare.Extra.AuthViewModel
import com.example.eyecare.Extra.LoadingAnimation
import com.example.eyecare.topBar.topBarId
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

@Composable
fun PatientCatalogPage(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    var patients by remember { mutableStateOf(listOf<Patient>()) }
    var filteredPatients by remember { mutableStateOf(listOf<Patient>()) }
    var searchQuery by remember { mutableStateOf("") }
    var optoName by remember { mutableStateOf("Loading...") }
    var optoPosition by remember { mutableStateOf("Loading...") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val currentUserId = auth.currentUser?.uid

    // Fetch optometrist's details from Firestore
    LaunchedEffect(currentUserId) {
        currentUserId?.let { userId ->
            val userDocRef = db.collection("users").document(userId)
            userDocRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    optoName = document.getString("name") ?: "Optometrist"
                    optoPosition = document.getString("role") ?: "Optometrist"
                } else {
                    optoName = "Optometrist"
                    optoPosition = "Optometrist"
                }
                isLoading = false
            }.addOnFailureListener { exception ->
                errorMessage = "Failed to fetch user details: ${exception.message}"
                isLoading = false
            }
        } ?: run {
            errorMessage = "User ID is null"
            isLoading = false
        }
    }

    // Fetch patients from Firestore
    DisposableEffect(Unit) {
        val firestoreRegistration: ListenerRegistration = db.collection("patients")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    errorMessage = "Error fetching patients: ${e.message}"
                    isLoading = false
                    return@addSnapshotListener
                }

                val fetchedPatients = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Patient::class.java)?.copy(id = document.id)
                } ?: emptyList()

                patients = fetchedPatients
                filteredPatients = fetchedPatients
                isLoading = false
            }

        onDispose {
            firestoreRegistration.remove()
        }
    }

    // Update filtered patients based on search query
    LaunchedEffect(searchQuery) {
        filteredPatients = if (searchQuery.isEmpty()) {
            patients
        } else {
            patients.filter { patient ->
                patient.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // UI Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        // TopBar with Optometrist Name and Position
        topBarId(
            name = optoName,
            position = optoPosition,
            screenName = "Patient List",
            authViewModel = AuthViewModel(),
            navController = navController
        )

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            label = { Text("Search patients") },
            placeholder = { Text("Enter name") },
            singleLine = true,
            shape = RoundedCornerShape(25.dp),
        )

        // Show loading, error message, or the list of filtered patients
        if (isLoading) {
            // Assuming you have a LoadingAnimation composable
            LoadingAnimation(
                circleSize = 25.dp,
                spaceBetween = 10.dp,
                travelDistance = 20.dp
            )
        } else if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxSize()
            ) {
                items(filteredPatients) { patient ->
                    PatientCard(patient = patient) {
                        navController.navigate("withGlassOpto/${patient.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun PatientCard(patient: Patient, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Name: ${patient.name}")
            Text(text = "Age: ${patient.age} years")
            Text(text = "Gender: ${patient.gender}")
            Text(text = "Address: ${patient.address}")
            Text(text = "Phone: ${patient.phone}")

            // Display status of the patient
            if (patient.currentlyUnderObservation) {
                Text(
                    text = "Under Observation",
                    color = Color.Red,
                    fontSize = 14.sp
                )
            } else {
                Text(
                    text = "Available",
                    color = Color.Green,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// Functions to update patient status
fun startObservation(patientId: String) {
    val db = FirebaseFirestore.getInstance()
    db.collection("patients").document(patientId).update(
        "status", "Under Observation",
        "currentlyUnderObservation", true
    ).addOnSuccessListener {
        // Status updated successfully
    }.addOnFailureListener { e ->
        // Handle the error
    }
}

fun endObservation(patientId: String) {
    val db = FirebaseFirestore.getInstance()
    db.collection("patients").document(patientId).update(
        "status", "Available",
        "currentlyUnderObservation", false
    ).addOnSuccessListener {
        // Status updated successfully
    }.addOnFailureListener { e ->
        // Handle the error
    }
}


data class Patient(
    val id: String = "",
    val name: String = "",
    val age: String = "",
    val gender: String = "",
    val address: String = "",
    val phone: String = "",
    val status: String = "Available", // Default status
    val currentlyUnderObservation: Boolean = false // Indicates if the patient is being handled
)
