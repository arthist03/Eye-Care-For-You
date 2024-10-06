package com.example.eyecare.Opto.previewScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.eyecare.Extra.AuthViewModel
import com.example.eyecare.Opto.Patient
import com.example.eyecare.Opto.glassScreens.saveOptoData
import com.example.eyecare.topBar.topBarId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

@Composable
fun OptoPreviewScreen(navController: NavController, patientId: String) {
    val db = FirebaseFirestore.getInstance()

    // Fetch patient details
    var patientDetails by remember { mutableStateOf<Patient?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    var optoName by remember { mutableStateOf("Loading...") }
    var optoPosition by remember { mutableStateOf("Loading...") }

    // Fetching patient data
    LaunchedEffect(patientId) {
        db.collection("patients").document(patientId)
            .collection("visits").document(patientId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    patientDetails = document.toObject(Patient::class.java)
                } else {
                    errorMessage = "Patient not found"
                }
                isLoading = false
            }
            .addOnFailureListener { exception ->
                errorMessage = "Failed to fetch patient details: ${exception.message}"
                isLoading = false
            }
    }

    // Fetch optometrist details
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

    Scaffold(
        topBar = {
            topBarId(
                name = optoName,
                position = optoPosition,
                screenName = "Preview of Examination", // Indicate screen type in top bar
                authViewModel = AuthViewModel(),
                navController = navController
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                patientDetails?.let { patient ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Patient details at the top
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = Color(0xFFE0F7FA)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(text = "Patient Name: ${patient.name}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Patient Age: ${patient.age}", fontSize = 18.sp)
                                Text(text = "Patient ID: ${patient.id}", fontSize = 18.sp)
                            }
                        }

                        // Without Glasses Section
                        SectionCard(
                            title = "Without Glasses",
                            patientId = patientId,
                            screenType = "withoutGlassOpto"
                        )

                        // With Glasses Section
                        SectionCard(
                            title = "With Glasses",
                            patientId = patientId,
                            screenType = "withGlassOpto"
                        )

                        // New Prescription Section
                        SectionCard(
                            title = "New Prescription",
                            patientId = patientId,
                            screenType = "newGlassOpto"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionCard(title: String, patientId: String, screenType: String) {
    val db = FirebaseFirestore.getInstance()
    var data by remember { mutableStateOf<Map<String, Any>?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch data for the section
    LaunchedEffect(screenType) {
        db.collection(screenType).document(patientId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    data = document.data
                }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFFFAFAFA)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                data?.let {
                    it.forEach { (key, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "$key:", fontWeight = FontWeight.SemiBold)
                            Text(text = value.toString())
                        }
                    }
                } ?: run {
                    Text(text = "No data available.")
                }
            }
        }
    }
}

@Composable
fun LoadingAnimation(circleSize: Dp, spaceBetween: Dp, travelDistance: Dp) {
    // Placeholder for loading animation
    // You can use this function as a placeholder or customize it as needed
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun OptoPreviewScreenPreview() {
    // Create a NavController instance (if required for topBar, else use null)
    val navController = rememberNavController()

    // Dummy patient data for preview purposes
    val previewPatient = Patient(
        name = "John Doe",
        age = "29"
    )

    // Call the actual screen you want to preview
    OptoPreviewScreen(navController = navController, patientId = previewPatient.toString())
}
