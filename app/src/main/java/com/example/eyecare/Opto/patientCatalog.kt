package com.example.eyecare.Opto

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.eyecare.Extra.AuthViewModel
import com.example.eyecare.Extra.LoadingAnimation
import com.example.eyecare.topBar.topBarId
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.eyecare.R

@Composable
fun PatientCatalogPage(navController: NavController) {
    val db = FirebaseFirestore.getInstance()

    var patients by remember { mutableStateOf(listOf<Patient>()) }
    var filteredPatients by remember { mutableStateOf(listOf<Patient>()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(Date())) }
    var optoName by remember { mutableStateOf("Loading...") }
    var optoPosition by remember { mutableStateOf("Loading...") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val context = LocalContext.current

    // Fetch optometrist's details from Firestore
    LaunchedEffect(currentUserId) {
        currentUserId?.let { userId ->
            val userDocRef = db.collection("users").document(userId)
            userDocRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    optoName = document.getString("fullName") ?: "Optometrist"
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

    // Fetch assigned patients from Firestore
// Fetch assigned patients from Firestore
    DisposableEffect(selectedDate) {
        val firestoreRegistration = db.collection("users")
            .document(currentUserId ?: "")
            .collection("AssignedPatients")
            .document(selectedDate)  // Use selectedDate formatted as dd_MM_yyyy
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    errorMessage = "Error fetching patients: ${e.message}"
                    isLoading = false
                    return@addSnapshotListener
                }

                val fetchedPatients = snapshot?.data?.get("ssignedPatients") as? List<Map<String, Any>> ?: emptyList()

                patients = fetchedPatients.map { patientData ->
                    Patient(
                        id = patientData["id"] as? String ?: "",
                        name = patientData["name"] as? String ?: "",
                        age = patientData["age"] as? String ?: "",
                        gender = patientData["gender"] as? String ?: "",
                        address = patientData["address"] as? String ?: "",
                        phone = patientData["phone"] as? String ?: "",
                        imageUri = patientData["imageUri"] as? String,
                        visitingDate = selectedDate.replace("_", "/")  // Store as dd/MM/yyyy for display
                    )
                }

                filteredPatients = patients
                isLoading = false
            }

        onDispose {
            firestoreRegistration.remove()
        }
    }

    LaunchedEffect(searchQuery, selectedDate) {
        filteredPatients = patients.filter { patient ->
            (searchQuery.isEmpty() || patient.name.startsWith(searchQuery, ignoreCase = true)) &&
                    patient.visitingDate == selectedDate.replace("_", "/")  // Only display patients for today
        }
    }


    // Function to show DatePicker
    @SuppressLint("DefaultLocale")
    fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val formattedDate = String.format("%02d_%02d_%04d", dayOfMonth, month + 1, year)
                selectedDate = formattedDate // Update the selected date
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    // UI Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        // TopBar with Optometrist Name and Position
        topBarId(
            fullName = optoName,
            position = optoPosition,
            screenName = "Patient List",
            authViewModel = AuthViewModel(),
            navController = navController
        )

        // Search Bar with Filter Icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = "Search Icon")
                },
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.weight(1f),
                label = { Text("Search patients") },
                placeholder = { Text("Enter name") },
                singleLine = true,
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    focusedPlaceholderColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White
                ),
                trailingIcon = {
                    IconButton(onClick = { showDatePicker() }) {
                        Icon(Icons.Filled.FilterList, contentDescription = "Filter by Date")
                    }
                }
            )
        }

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
    // Construct the image URL based on the patient ID
    val imageUrl = "https://firebasestorage.googleapis.com/v0/b/eyecare-for-you.appspot.com/o/patient_images%2F${patient.id}.jpg?alt=media"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = // Replace with your placeholder resource
                rememberAsyncImagePainter(ImageRequest.Builder // Replace with your error image resource
                    (LocalContext.current).data(data = imageUrl).apply(block = fun ImageRequest.Builder.() {
                    placeholder(R.drawable.edit) // Replace with your placeholder resource
                    error(R.drawable.addfriend) // Replace with your error image resource
                }).build()
                ),
                contentDescription = "Patient Image",
                modifier = Modifier
                    .size(100.dp).clip(RoundedCornerShape(50.dp))
                    .padding(),
                contentScale = ContentScale.Crop
            )


            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Name: ${patient.name}")
                Text(text = "Age: ${patient.age} years")
                Text(text = "Gender: ${patient.gender}")
                Text(text = "Id: ${patient.id}")
                Text(text = "Phone: ${patient.phone}")
            }
        }
    }
}
