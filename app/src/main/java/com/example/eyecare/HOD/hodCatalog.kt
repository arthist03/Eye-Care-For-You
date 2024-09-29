package com.example.eyecare.HOD


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.eyecare.Extra.AuthViewModel
import com.example.eyecare.Extra.LoadingAnimation
import com.example.eyecare.topBar.topBarId
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

@Composable
fun hodcatalog(navController: NavController) {
    val db = FirebaseFirestore.getInstance()

    var patients by remember { mutableStateOf(listOf<Patient>()) }
    var filteredPatients by remember { mutableStateOf(listOf<Patient>()) } // List of filtered patients
    var searchQuery by remember { mutableStateOf("") } // Holds the search query
    var optoName by remember { mutableStateOf("Loading...") }
    var optoPosition by remember { mutableStateOf("Loading...") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    // Fetch optometrist's details from Firestore
    LaunchedEffect(currentUserId) {
        currentUserId?.let { userId ->
            val userDocRef = db.collection("users").document(userId)
            userDocRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    optoName = document.getString("fullName") ?: "HOD"
                    optoPosition = document.getString("role") ?: "HOD"
                } else {
                    optoName = "HOD"
                    optoPosition = "HOD"
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
                    document.toObject(Patient::class.java)?.copy(id = document.id) // Include document ID
                } ?: emptyList()

                patients = fetchedPatients
                filteredPatients = fetchedPatients // Initially show all patients
                isLoading = false
            }

        onDispose {
            firestoreRegistration.remove()
        }
    }

    // Update the filtered patients whenever search query changes
    LaunchedEffect(searchQuery) {
        filteredPatients = if (searchQuery.isEmpty()) {
            patients
        } else {
            // Filter patients based on progressive letter matching
            patients.filter { patient ->
                patient.name.startsWith(searchQuery, ignoreCase = true)
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
            shape = RoundedCornerShape(25.dp), // Make the corners rounded
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

        // Show loading, error message, or the list of filtered patients
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
                        // Navigate to patient details page with patient ID
                        navController.navigate("withGlassOpto/${patient.id}")
                    }
                }
            }
        }
    }
}




// Patient data model
data class Patient(
    val address: String = "",
    val age: String = "",
    val gender: String = "",
    val id: String = "",
    val imageUri: String? = null,
    val name: String = "",
    val phone: String = "",
    val visitingDate: String = ""
)


@Composable
fun PatientCard(patient: Patient, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },  // Trigger onClick when tapped
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Name: ${patient.name}")
            Text(text = "Age: ${patient.age} years")
            Text(text = "Gender: ${patient.gender}")
            Text(text = "Address: ${patient.id}")
            Text(text = "Phone: ${patient.phone}")
        }
    }
}