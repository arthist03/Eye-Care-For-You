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

@Composable
fun withoutGlassOpto(navController: NavController, patientId: String) {
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
    var snellenRight by remember { mutableStateOf(6f) }
    var isCylindricalLens by remember { mutableStateOf(false) }

    // Fetch patient details from Firestore
    LaunchedEffect(patientId) {
        db.collection("patients").document(patientId).get()
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

    // Fetch optometrist details from Firestore
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
                screenName = "Without Glasses", // Indicate screen type in top bar
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
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Name: ${patient.name}",
                                    fontSize = 20.sp,
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Id: ${patient.id}",
                                    fontSize = 15.sp,
                                )
                            }
                            Text(
                                text = "No Glass's Prescription",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1242E6),
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .background(
                                        color = Color(0xFFE7F0FF),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .align(alignment = Alignment.CenterHorizontally)
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )

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
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row {
                                    Checkbox(
                                        checked = isCylindricalLens,
                                        onCheckedChange = { isCylindricalLens = it }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "Add Cylindrical Lens")
                                }
                            }

                            if (isCylindricalLens) {
                                // Cylindrical Lens
                                Text(text = "Cylindrical Lens")
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    OutlinedTextField(
                                        value = leftCylindricalMag,
                                        onValueChange = { leftCylindricalMag = it },
                                        label = { Text("Left Eye Magnitude") },
                                        modifier = Modifier.weight(1f),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    OutlinedTextField(
                                        value = rightCylindricalMag,
                                        onValueChange = { rightCylindricalMag = it },
                                        label = { Text("Right Eye Magnitude") },
                                        modifier = Modifier.weight(1f),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                    )
                                }
                            }

                            // Snellen Test with Vertical Slider
                            Text(text = "Snellen Test (6/x)")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                VerticalSnellenSlider(
                                    value = snellenLeft,
                                    onValueChange = { snellenLeft = it },
                                    label = "Left Eye",
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                VerticalSnellenSlider(
                                    value = snellenRight,
                                    onValueChange = { snellenRight = it },
                                    label = "Right Eye",
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Save and navigation buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                ElevatedButton(onClick = {
                                    navController.navigate("withGlassOpto/${patient.id}") {
                                        popUpTo("patientDetails/${patient.id}") { inclusive = true }
                                    }
                                }) {
                                    Text(text = "Back")
                                }

                                ElevatedButton(onClick = {
                                    saveOptoData(
                                        patientId = patientId,
                                        name = patient.name,
                                        age = patient.age.toString(),
                                        leftEyeDistance = leftEyeDistance,
                                        rightEyeDistance = rightEyeDistance,
                                        leftEyeNear = leftEyeNear,
                                        rightEyeNear = rightEyeNear,
                                        leftCylindricalMag = leftCylindricalMag,
                                        rightCylindricalMag = rightCylindricalMag,
                                        snellenLeft = snellenLeft,
                                        snellenRight = snellenRight,
                                        db = db,
                                        context = context,
                                        screenType = "withoutGlassOpto"
                                    )
                                    navController.navigate("newGlassOpto/${patient.id}") {
                                        popUpTo("patientDetails/${patient.id}") { inclusive = true }
                                    }
                                }) {
                                    Text(text = "Save Examination Details")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun saveOptoData(
    patientId: String,
    name: String,
    age: String,
    leftEyeDistance: String,
    rightEyeDistance: String,
    leftEyeNear: String,
    rightEyeNear: String,
    leftCylindricalMag: String,
    rightCylindricalMag: String,
    snellenLeft: Float,
    snellenRight: Float,
    db: FirebaseFirestore,
    context: Context,
    screenType: String // To differentiate between screens
) {
    val examinationDetails = hashMapOf(
        "Patient Id" to patientId,
        "Name" to name,
        "Age" to age,
        "Left Eye Distance" to leftEyeDistance,
        "Right Eye Distance" to rightEyeDistance,
        "Left Eye Near" to leftEyeNear,
        "Right Eye Near" to rightEyeNear,
        "Left Cylindrical Magnitude" to leftCylindricalMag,
        "Right Cylindrical Magnitude" to rightCylindricalMag,
        "Snellen Left" to snellenLeft,
        "Snellen Right" to snellenRight
    )

    // Collection names based on screen type
    val collectionName = when (screenType) {
        "withoutGlassOpto" -> "optoWithOutGlasses"
        "withGlassOpto" -> "optoWithGlasses"
        "newGlassOpto" -> "optoNewGlass"
        else -> "examinations"
    }

    // Save data in Firestore
    db.collection(collectionName).document(patientId)
        .set(examinationDetails)
        .addOnSuccessListener {
            Toast.makeText(context, "Data saved successfully to ${name} & ${patientId} ", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener { exception ->
            Toast.makeText(context, "Failed to save data: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
}

@Composable
fun VerticalSnellenSlider(value: Float, onValueChange: (Float) -> Unit, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "$label: 6/${value.toInt()}")
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 6f..60f,
            steps = 10,
            modifier = Modifier
                .height(150.dp)
                .background(Color(0xFFE7F0FF), RoundedCornerShape(16.dp))
        )
    }
}