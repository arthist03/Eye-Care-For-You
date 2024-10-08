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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.eyecare.Extra.AuthViewModel
import com.example.eyecare.Extra.LoadingAnimation
import com.example.eyecare.Opto.Patient
import com.example.eyecare.topBar.topBarId
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

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
    var snellenLeftN by remember { mutableStateOf(6f) }
    var snellenRight by remember { mutableStateOf(6f) }
    var snellenRightN by remember { mutableStateOf(6f) }
    var isCylindricalLens by remember { mutableStateOf(false) }

    LaunchedEffect(patientId) {
        // Fetch patient details
        db.collection("patients").document(patientId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    patientDetails = document.toObject(Patient::class.java)

                    // Fetch available visit dates dynamically
                    db.collection("patients")
                        .document(patientId)
                        .collection("visits")
                        .get()
                        .addOnSuccessListener { visitDocuments ->
                            if (!visitDocuments.isEmpty) {
                                val firstVisitDocument = visitDocuments.documents.first()

                                // Fetching the formatted visiting date
                                val formattedVisitingDate = firstVisitDocument.id
                                val visitingDate = formattedVisitingDate // Assuming both formats are now aligned

                                // Log/Toast for debugging
                                Toast.makeText(context, "Fetching Visit Date: $formattedVisitingDate", Toast.LENGTH_LONG).show()

                                // Fetch optometric data for the visit date
                                fetchVisitingDate(patientId, visitingDate, db, { visitingDateResult ->
                                    patientDetails = patientDetails?.copy(visitingDate = visitingDateResult)

                                    // Fetch the optometric examination data
                                    db.collection("patients")
                                        .document(patientId)
                                        .collection("visits")
                                        .document(visitingDate)
                                        .collection("withoutGlassOpto")
                                        .document(visitingDate)
                                        .get()
                                        .addOnSuccessListener { examDoc ->
                                            if (examDoc.exists()) {
                                                // Pre-fill the fields with the existing data
                                                leftEyeDistance = examDoc.getString("Left Eye Distance") ?: ""
                                                rightEyeDistance = examDoc.getString("Right Eye Distance") ?: ""
                                                leftEyeNear = examDoc.getString("Left Eye Near") ?: ""
                                                rightEyeNear = examDoc.getString("Right Eye Near") ?: ""
                                                leftCylindricalMag = examDoc.getString("Left Cylindrical Magnitude") ?: ""
                                                rightCylindricalMag = examDoc.getString("Right Cylindrical Magnitude") ?: ""
                                                snellenLeft = examDoc.getDouble("Snellen Left")?.toFloat() ?: 6f
                                                snellenLeftN = examDoc.getDouble("Snellen Left Near")?.toFloat() ?: 6f
                                                snellenRight = examDoc.getDouble("Snellen Right")?.toFloat() ?: 6f
                                                snellenRightN = examDoc.getDouble("Snellen Right Near")?.toFloat() ?: 6f
                                                isCylindricalLens = examDoc.getBoolean("isCylindricalLens") ?: false
                                            } else {
                                                // Handle if no data found
                                                Toast.makeText(context, "No exam data found for $visitingDate", Toast.LENGTH_SHORT).show()
                                            }
                                            isLoading = false
                                        }
                                        .addOnFailureListener { exception ->
                                            errorMessage = "Failed to fetch exam data: ${exception.message}"
                                            isLoading = false
                                        }
                                }, { error ->
                                    errorMessage = error
                                    isLoading = false
                                })
                            } else {
                                errorMessage = "No visits found for this patient"
                                isLoading = false
                            }
                        }
                        .addOnFailureListener { exception ->
                            errorMessage = "Failed to fetch visit dates: ${exception.message}"
                            isLoading = false
                        }
                } else {
                    errorMessage = "Patient not found"
                    isLoading = false
                }
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
                fullName = optoName,
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

                            // Save and navigation buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                ElevatedButton(onClick = {
                                    navController.navigate("withGlassOpto/${patient.id}")
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
                                        snellenLeftN = snellenLeftN,
                                        snellenRight = snellenRight,
                                        snellenRightN = snellenRightN,
                                        db = db,
                                        context = context,
                                        screenType = "withoutGlassOpto"
                                    )
                                    navController.navigate("newGlassOpto/${patient.id}")
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



fun saveOptoData(
    db: FirebaseFirestore,
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
    snellenLeftN: Float,
    snellenRight: Float,
    snellenRightN: Float,
    context: Context,
    screenType: String // To differentiate between screens
) {
    // Fetch the visiting date dynamically
    val visitingDate = SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(Date()) // Current date in dd_MM_yyyy format

    // Create a map for examination details
    val examinationDetails = hashMapOf<String, Any>(
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
        "Snellen Left Near" to snellenLeftN,
        "Snellen Right" to snellenRight,
        "Snellen Right Near" to snellenRightN,
        "Screen Type" to screenType,
        "Date" to visitingDate // Use visitingDate
    )

    // Save the data in the following structure:
    // patients -> patientId -> visits -> visitingDate -> screenType -> visitingDate -> data
    db.collection("patients").document(patientId)
        .collection("visits")
        .document(visitingDate) // Document represents the visiting date
        .collection(screenType) // Sub-collection for the screen type (withGlassOpto, withoutGlassOpto, etc.)
        .document(visitingDate) // Document for the visiting date within the screen type collection
        .set(examinationDetails, SetOptions.merge()) // Merge the data into the document
        .addOnSuccessListener {
            // Only assign doctor if screenType is "newGlassOpto"
            if (screenType == "newGlassOpto") {
                assignDoctorIfNeeded(db, patientId, visitingDate, context)
            }
            Toast.makeText(context, "Data saved successfully", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener { exception ->
            Toast.makeText(context, "Failed to save data: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
}



private fun assignDoctorIfNeeded(
    db: FirebaseFirestore,
    patientId: String,
    visitDate: String,
    context: Context
) {
    // Check if the patient already has an assigned doctor
    db.collection("patients").document(patientId)
        .get()
        .addOnSuccessListener { patientDoc ->
            if (patientDoc.exists()) {
                // Get patient details from the document
                val name = patientDoc.getString("name") ?: "Unknown"
                val address = patientDoc.getString("address")
                val phone = patientDoc.getString("phone")
                val gender = patientDoc.getString("gender") ?: "Unknown"
                val age = patientDoc.getString("age") ?: "Unknown"
                val dateOfBirth = patientDoc.getString("dateOfBirth")
                val imageUri = patientDoc.getString("imageUri")

                // Proceed to find a doctor if the patient doesn't have one
                db.collection("patients").document(patientId)
                    .collection("visits").document(visitDate)
                    .get()
                    .addOnSuccessListener { visitDoc ->
                        val assignedDoctorId = visitDoc.getString("AssignedDoctorId")
                        if (assignedDoctorId != null) {
                            Toast.makeText(context, "Patient is already assigned to a doctor.", Toast.LENGTH_SHORT).show()
                        } else {
                            // No doctor assigned, find a new doctor
                            findAvailableDoctor(
                                db, patientId, visitDate, context,
                                name, address, phone, gender, age, dateOfBirth, imageUri
                            )
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, "Failed to check doctor assignment: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(context, "Patient does not exist.", Toast.LENGTH_SHORT).show()
            }
        }
        .addOnFailureListener { exception ->
            Toast.makeText(context, "Failed to fetch patient details: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
}

private fun findAvailableDoctor(
    db: FirebaseFirestore,
    patientId: String,
    visitDate: String,
    context: Context,
    name: String,
    address: String?,
    phone: String?,
    gender: String,
    age: String,
    dateOfBirth: String?,
    imageUri: String?
) {
    // Fetch doctors with role DOCTOR
    db.collection("users")
        .whereEqualTo("role", "DOCTOR")
        .get()
        .addOnSuccessListener { doctorDocs ->
            if (doctorDocs.isEmpty) {
                Toast.makeText(context, "No doctors available.", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            // Logic to choose a doctor (this can be improved based on your criteria)
            val doctorDoc = doctorDocs.documents.first() // For simplicity, picking the first available doctor
            val doctorId = doctorDoc.id
            val doctorName = doctorDoc.getString("fullName") ?: "Unknown Doctor"

            // Assign the doctor to the patient for this visit
            val visitRef = db.collection("patients").document(patientId)
                .collection("visits").document(visitDate)

            visitRef.update("AssignedDoctorId", doctorId, "doctorName", doctorName)
                .addOnSuccessListener {
                    // Prepare patient details map
                    val patientDetails = hashMapOf<String, Any>(
                        "name" to name,
                        "gender" to gender,
                        "age" to age,
                        "id" to patientId
                    )

                    // Only add non-nullable fields to the map
                    address?.let { patientDetails["address"] = it }
                    phone?.let { patientDetails["phone"] = it }
                    dateOfBirth?.let { patientDetails["dateOfBirth"] = it }
                    imageUri?.let { patientDetails["imageUri"] = it }

                    // Save in doctor's collection
                    val doctorPatientsRef = db.collection("users").document(doctorId)
                        .collection("AssignedPatients").document(visitDate)

                    doctorPatientsRef.set(
                        hashMapOf(
                            "visitingDate" to visitDate,
                            "patientId" to patientId,
                            "patientDetails" to patientDetails // Store all patient details here
                        ),
                        SetOptions.merge()
                    ).addOnSuccessListener {
                        Toast.makeText(context, "Doctor assigned successfully: $doctorName", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener { exception ->
                        Toast.makeText(context, "Failed to assign doctor to patient: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Failed to assign doctor: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
        .addOnFailureListener { exception ->
            Toast.makeText(context, "Failed to fetch doctors: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
}



@Composable
fun VerticalSnellenSlider(value: Float, onValueChange: (Float) -> Unit, label: String) {
    // Define the allowed Snellen values
    val snellenValues = listOf(60f, 36f, 24f, 18f, 12f, 9f, 6f)

    // Obtain the HapticFeedback instance
    val haptic = LocalHapticFeedback.current

    // Find the index of the current value in the list
    val currentIndex = snellenValues.indexOf(value.coerceIn(6f, 60f))

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the current value in the Snellen format
        Text(text = "$label: 6/${snellenValues[currentIndex].toInt()}")

        // Use Box to allow vertical rotation of the Slider
        Box(
            modifier = Modifier
                .height(60.dp) // Height of the vertical slider
                .width(150.dp) // Width of the vertical slider (to make it visible)
                .background(Color(0xFFE7F0FF), RoundedCornerShape(16.dp))
        ) {
            Slider(
                value = currentIndex.toFloat(),
                onValueChange = { newIndex ->
                    onValueChange(snellenValues[newIndex.toInt()]) // Use the exact value from the list

                    // Trigger haptic feedback when the value changes
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                valueRange = 0f..(snellenValues.size - 1).toFloat(), // Restrict slider to the indices
                steps = snellenValues.size - 2, // Define the number of steps based on the list
                modifier = Modifier
                    .fillMaxHeight() // Fill the available height in the rotated Box
                    .align(Alignment.Center) // Center the Slider in the Box
                    .height(10.dp) // Height of the vertical slider
            )
        }
    }
}



@Composable
fun VerticalNearSlider(valueN: Float, onValueChange: (Float) -> Unit, label: String) {
    // Define the allowed Near vision values
    val nearValues = listOf(6f, 8f, 10f, 24f, 32f, 48f, 49f) // Use 49f for ">N48"

    // Obtain the HapticFeedback instance
    val haptic = LocalHapticFeedback.current

    // Find the index of the current value in the list
    val currentIndex = nearValues.indexOf(valueN.coerceIn(6f, 49f))

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the current value in the Near format
        Text(text = if (nearValues[currentIndex] == 49f) "$label: >N48" else "$label: N/${nearValues[currentIndex].toInt()}")

        // Use Box to allow vertical rotation of the Slider
        Box(
            modifier = Modifier
                .height(60.dp) // Height of the vertical slider
                .width(150.dp) // Width of the vertical slider (to make it visible)
                .background(Color(0xFFE7F0FF), RoundedCornerShape(16.dp))
        ) {
            Slider(
                value = currentIndex.toFloat(),
                onValueChange = { newIndex ->
                    onValueChange(nearValues[newIndex.toInt()]) // Use the exact value from the list

                    // Trigger haptic feedback when the value changes
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                valueRange = 0f..(nearValues.size - 1).toFloat(), // Restrict slider to the indices
                steps = nearValues.size - 2, // Define the number of steps based on the list
                modifier = Modifier
                    .fillMaxHeight() // Fill the available height in the rotated Box
                    .align(Alignment.Center) // Center the Slider in the Box
                    .height(10.dp) // Height of the vertical slider
            )
        }
    }
}
