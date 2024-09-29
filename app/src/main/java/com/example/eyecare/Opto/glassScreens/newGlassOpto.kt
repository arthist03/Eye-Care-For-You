package com.example.eyecare.Opto.glassScreens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.eyecare.Extra.AuthViewModel
import com.example.eyecare.Extra.LoadingAnimation
import com.example.eyecare.Opto.Patient
import com.example.eyecare.topBar.topBarId
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun newGlassOpto(navController: NavController, patientId: String) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    // State to store patient and optometrist details
    var patientDetails by remember { mutableStateOf<Patient?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    var optoName by remember { mutableStateOf("Loading...") }
    var optoPosition by remember { mutableStateOf("Loading...") }

    // State for optometric examination inputs
    var leftEyeDistance by remember { mutableStateOf("") }
    var rightEyeDistance by remember { mutableStateOf("") }
    var leftEyeNear by remember { mutableStateOf("") }
    var rightEyeNear by remember { mutableStateOf("") }
    var leftCylindricalMag by remember { mutableStateOf("") }
    var rightCylindricalMag by remember { mutableStateOf("") }
    var snellenLeft by remember { mutableStateOf(6f) }
    var snellenLeftN by remember { mutableStateOf(6f) }
    var snellenRight by remember { mutableStateOf(6f) }
    var snellenRightN by remember { mutableStateOf(6f) }
    var isCylindricalLens by remember {mutableStateOf(false)}


    LaunchedEffect(patientId) {
        val todayDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        db.collection("patients").document(patientId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val patient = document.toObject(Patient::class.java)
                    val visitingDate = document.getString("visitingDate") // Assuming visitingDate is stored as a string in "dd/MM/yyyy" format

                    if (visitingDate == todayDate) {
                        patientDetails = patient
                    } else {
                        errorMessage = "No patient visit scheduled for today."
                    }
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

    // Fetch optometrist details from Firestore
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

    Scaffold(
        topBar = {
            topBarId(
                name = optoName,
                position = optoPosition,
                screenName = "New Prescription", // Indicate screen type in top bar
                authViewModel = AuthViewModel(),
                navController = navController
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 8.dp)
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
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row (modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween){
                                Text(text = "Name: ${patient.name}")
                                Text(text = "ID: ${patient.id}")
                            }
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Age: ${patient.age} Years")
                                Text(text = "Date: ${patient.visitingDate}")
                            }

                            HorizontalDivider(thickness = 2.dp)

                            // Fields for examination details
                            Text(text = "Distance Vision")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                OutlinedTextField(
                                    value = leftEyeDistance,
                                    onValueChange = { leftEyeDistance = it },
                                    label = { Text("Left Eye (Spherical)") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                OutlinedTextField(
                                    value = rightEyeDistance,
                                    onValueChange = { rightEyeDistance = it },
                                    label = { Text("Right Eye (Spherical)") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                            }

                            Text(text = "Near Vision")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                OutlinedTextField(
                                    value = leftEyeNear,
                                    onValueChange = { leftEyeNear = it },
                                    label = { Text("Left Eye (Spherical)") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                OutlinedTextField(
                                    value = rightEyeNear,
                                    onValueChange = { rightEyeNear = it },
                                    label = { Text("Right Eye (Spherical)") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Checkbox for Cylindrical Lens
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isCylindricalLens,
                                        onCheckedChange = { isCylindricalLens = it }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "Add Cylindrical Lens")
                                }

                                // Conditionally show Cylindrical Lens fields
                                if (isCylindricalLens) {
                                    Text(
                                        text = "Cylindrical Lens",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = leftCylindricalMag,
                                            onValueChange = { leftCylindricalMag = it },
                                            label = { Text("Left Eye Magnitude") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                        )
                                        SeekerWithTextField()
                                        OutlinedTextField(
                                            value = rightCylindricalMag,
                                            onValueChange = { rightCylindricalMag = it },
                                            label = { Text("Right Eye Magnitude") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                        )
                                        SeekerWithTextField()
                                    }
                                }

                                // Snellen Test with Vertical Slider
                                Text(text = "Snellen Test (6/x)")
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        VerticalSnellenSlider(
                                            value = snellenLeft,
                                            onValueChange = { snellenLeft = it },
                                            label = "Left Eye"
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        VerticalSnellenSlider(
                                            value = snellenRight,
                                            onValueChange = { snellenRight = it },
                                            label = "Right Eye"
                                        )

                                    }
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        VerticalNearSlider(
                                            valueN = snellenLeftN,
                                            onValueChange = { snellenLeftN = it },
                                            label = "Left Eye"
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        VerticalNearSlider(
                                            valueN = snellenRightN,
                                            onValueChange = { snellenRightN = it },
                                            label = "Right Eye"
                                        )
                                    }

                                }

                                Spacer(modifier = Modifier.height(20.dp))


                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                                ) {
                                    ElevatedButton(onClick = {
                                        navController.navigate("withoutGlassOpto/${patient.id}") {
                                        }
                                    }) {
                                        Text(text = "Back")
                                    }

                                    ElevatedButton(onClick = {
                                        saveOptoData(
                                            patientId = patientId,
                                            name = patient.name,
                                            age = patient.age,
                                            leftEyeDistance = leftEyeDistance,
                                            rightEyeDistance = rightEyeDistance,
                                            leftEyeNear = leftEyeNear,
                                            rightEyeNear = rightEyeNear,
                                            leftCylindricalMag = leftCylindricalMag,
                                            rightCylindricalMag = rightCylindricalMag,
                                            snellenLeft = snellenLeft,
                                            snellenLeftN = snellenLeftN,
                                            snellenRight = snellenRight,
                                            snellenRightN = snellenRightN,
                                            db = db,
                                            context = context,
                                            screenType = "newGlassOpto"
                                        )
                                        navController.navigate("OptoPatients")

                                    }) {
                                        Text(text = "Save Examination")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}