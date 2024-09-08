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

    var patientDetails by remember { mutableStateOf<Patient?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    var optoName by remember { mutableStateOf("Loading...") }
    var optoPosition by remember { mutableStateOf("Loading...") }

    var leftEyeDistance by remember { mutableStateOf("") }
    var rightEyeDistance by remember { mutableStateOf("") }
    var leftEyeNear by remember { mutableStateOf("") }
    var rightEyeNear by remember { mutableStateOf("") }
    var leftCylindricalMag by remember { mutableStateOf("") }
    var leftCylindricalAxis by remember { mutableStateOf(0f) }
    var rightCylindricalMag by remember { mutableStateOf("") }
    var rightCylindricalAxis by remember { mutableStateOf(0f) }
    var isCylindricalLens by remember { mutableStateOf(false) }
    var snellenLeft by remember { mutableStateOf(6f) }
    var snellenRight by remember { mutableStateOf(6f) }

    // Fetch patient details from Firestore by ID
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

    Scaffold(
        topBar = {
            topBarId(
                name = optoName,
                position = optoPosition,
                screenName = "Without Glasses",
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
                            Row (horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()){
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
                            Row (horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()){
                                Text(
                                    text = "Gender: ${patient.gender}",
                                    fontSize = 20.sp,
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    text = "Age: ${patient.age} Years",
                                    fontSize = 15.sp,
                                )
                            }

                            // New Prescription
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

                            // Distance Vision
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

                            // Near Vision
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

                            // Cylindrical Lens Option
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
                                    SeekerWithTextField()
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    OutlinedTextField(
                                        value = rightCylindricalMag,
                                        onValueChange = { rightCylindricalMag = it },
                                        label = { Text("Right Eye Magnitude") },
                                        modifier = Modifier.weight(1f),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    SeekerWithTextField()
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

                            Row (modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly){
                                ElevatedButton(onClick = {navController.navigate("withGlassOpto/${patient.id}") {
                                    popUpTo("patientDetails/${patient.id}") { inclusive = true }
                                } }) {
                                    Text(text = "Back")
                                }

                                ElevatedButton(onClick = {
                                    saveWithOutGlassOpto(
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
                                        snellenRight = snellenRight,
                                        db = db,
                                        context= context
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

fun saveWithOutGlassOpto(
    patientId: String,
    name:String,
    age:String,
    leftEyeDistance: String,
    rightEyeDistance: String,
    leftEyeNear: String,
    rightEyeNear: String,
    leftCylindricalMag: String,
    rightCylindricalMag: String,
    snellenLeft: Float,
    snellenRight: Float,
    db: FirebaseFirestore,
    context: Context
) {
    val examinationDetails = hashMapOf(
        "Name" to name,
        "Age" to age,
        "leftEyeDistance" to leftEyeDistance,
        "rightEyeDistance" to rightEyeDistance,
        "leftEyeNear" to leftEyeNear,
        "rightEyeNear" to rightEyeNear,
        "leftCylindricalMag" to leftCylindricalMag,
        "rightCylindricalMag" to rightCylindricalMag,
        "snellenLeft" to snellenLeft,
        "snellenRight" to snellenRight,
    )

    db.collection("optoWithOutGlasses").document(patientId)
        .set(examinationDetails)
        .addOnSuccessListener {
            Toast.makeText(context, "Data saved Successfully", Toast.LENGTH_SHORT).show() // Use .show() to display the toast
        }
        .addOnFailureListener { exception ->
            Toast.makeText(context, "Failed to save data: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
}