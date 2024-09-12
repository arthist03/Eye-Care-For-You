package com.example.eyecare.reception

import android.app.DatePickerDialog
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eyecare.Extra.AuthViewModel
import com.example.eyecare.topBar.topBarId
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailsScreen(navController: NavController, patientId: String?) {
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var address by remember { mutableStateOf(TextFieldValue("")) }
    var phone by remember { mutableStateOf(TextFieldValue("")) }
    var selectedGender by remember { mutableStateOf("Male") }
    var imageUri by remember { mutableStateOf<String?>(null) }
    var dateOfBirth by remember { mutableStateOf<LocalDate?>(null) }
    var todayDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showVisitingDatePicker by remember { mutableStateOf(false) }

    var receptionistName by remember { mutableStateOf("Loading...") }
    var receptionistPosition by remember { mutableStateOf("Loading...") }

    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(patientId) {
        val patientDocRef = db.collection("patients").document(patientId ?: "")

        patientDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                name = TextFieldValue(document.getString("name") ?: "")
                address = TextFieldValue(document.getString("address") ?: "")
                phone = TextFieldValue(document.getString("phone") ?: "")
                selectedGender = document.getString("gender") ?: "Male"
            } else {
                Toast.makeText(context, "Patient not found", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to fetch patient details", Toast.LENGTH_LONG).show()
        }
    }



    // Fetch receptionist details
    LaunchedEffect(currentUserId) {
        currentUserId?.let { userId ->
            val userDocRef = db.collection("users").document(userId)
            userDocRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    receptionistName = document.getString("name") ?: "Receptionist"
                    receptionistPosition = document.getString("position") ?: "Receptionist"
                }
            }.addOnFailureListener {
                receptionistName = "Receptionist"
                receptionistPosition = "Receptionist"
            }
        }
    }

    // Show date picker dialogs
    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                dateOfBirth = LocalDate.of(year, month + 1, dayOfMonth)
                showDatePicker = false
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    if (showVisitingDatePicker) {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                todayDate = LocalDate.of(year, month + 1, dayOfMonth)
                showVisitingDatePicker = false
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = {
            topBarId(
                name = receptionistName,
                position = receptionistPosition,
                screenName = "Patient Registration",
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Visiting date selection
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(onClick = { showVisitingDatePicker = true }) {
                            Text(text = "Select Visiting Date")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Selected Date: ${todayDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                            style = TextStyle(fontSize = 16.sp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Name input field
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 16.sp),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Date of birth selection
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(onClick = { showDatePicker = true }) {
                            Text(text = "Select Date of Birth")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Selected Date: ${dateOfBirth?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "Not selected"}",
                            style = TextStyle(fontSize = 16.sp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Calculate and display age
                    val age = dateOfBirth?.let { calculateAge(it, todayDate) } ?: "N/A"

                    OutlinedTextField(
                        value = age,
                        onValueChange = {},
                        label = { Text("Age") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(fontSize = 16.sp),
                        shape = RoundedCornerShape(10.dp),
                        enabled = false  // Make age field read-only
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Address input field
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 16.sp),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Phone number input field
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        textStyle = TextStyle(fontSize = 16.sp),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Gender selection
                    Text(
                        text = "Sex",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedGender == "Male",
                            onClick = { selectedGender = "Male" }
                        )
                        Text(text = "Male")

                        Spacer(modifier = Modifier.width(16.dp))

                        RadioButton(
                            selected = selectedGender == "Female",
                            onClick = { selectedGender = "Female" }
                        )
                        Text(text = "Female")

                        Spacer(modifier = Modifier.width(16.dp))

                        RadioButton(
                            selected = selectedGender == "Other",
                            onClick = { selectedGender = "Other" }
                        )
                        Text(text = "Other")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Display profile image if available
                    if (imageUri != null) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = Color.DarkGray,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    } else {
                        Text(
                            text = "No profile image selected",
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Save patient data button
                    ElevatedButton(
                        onClick = {
                            // Save patient data when button is clicked
                            savePatientData(
                                name = name.text,
                                address = address.text,
                                phone = phone.text,
                                gender = selectedGender,
                                imageUri = imageUri,
                                dateOfBirth = dateOfBirth,
                                visitingDate = todayDate,
                                db = db,
                                navController = navController,
                                patientId= patientId
                            )
                            //navController.navigate(("receptionistScreen"))
                            navController.popBackStack()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Patient Details")
                    }
                }
            }
        }
    }
}

// Function to calculate age from birth date
fun calculateAge(dateOfBirth: LocalDate, visitingDate: LocalDate): String {
    val period = Period.between(dateOfBirth, visitingDate)
    return period.years.toString()
}

// Suspend function to generate a unique patient ID
suspend fun generateUniquePatientId(db: FirebaseFirestore): String {
    var uniqueId: String
    do {
        uniqueId = (1..8)
            .map { "0123456789".random() }
            .joinToString("")
        val documentSnapshot = db.collection("patients").document(uniqueId).get().await()
    } while (documentSnapshot.exists()) // Repeat if the ID already exists
    return uniqueId
}

// Function to save patient data to Firestore with generated patient ID
fun savePatientData(
    name: String,
    address: String,
    phone: String,
    gender: String,
    imageUri: String?,
    dateOfBirth: LocalDate?,
    visitingDate: LocalDate,
    db: FirebaseFirestore,
    navController: NavController,
    patientId: String?
) {
    // Use coroutine to handle Firestore operations
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // Generate a unique patient ID
            val patientId = generateUniquePatientId(db)

            // Create a map of patient data
            val patientData = hashMapOf(
                "name" to name,
                "address" to address,
                "phone" to phone,
                "gender" to gender,
                "dateOfBirth" to dateOfBirth?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                "visitingDate" to visitingDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                "imageUri" to imageUri,
                "patientId" to patientId // Add the patient ID to the data
            )

            // Store the patient data using the generated patient ID as the document ID
            db.collection("patients").document(patientId).set(patientData).await()

            // Show a toast message on success
            withContext(Dispatchers.Main) {
                Toast.makeText(navController.context, "Patient saved successfully!", Toast.LENGTH_SHORT).show()
            }

            // Navigate back or show success feedback

        } catch (e: Exception) {
            Log.e("PatientDetailsScreen", "Error saving patient data", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(navController.context, "Failed to save patient data.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


/*
suspend fun generateUniquePatientId(db: FirebaseFirestore): String {
    val existingIds = mutableListOf<String>()
    val patientsSnapshot = db.collection("patients").get().await()

    for (document in patientsSnapshot.documents) {
        existingIds.add(document.id)
    }

    var newPatientId: String
    do {
        newPatientId = generateRandomAlphanumericId(8) // Change the length if needed
    } while (existingIds.contains(newPatientId))

    return newPatientId
}

fun generateRandomAlphanumericId(length: Int): String {
    val chars = "0123456789"
    return (1..7)
        .map { chars.random() }
        .joinToString("")
}
*/