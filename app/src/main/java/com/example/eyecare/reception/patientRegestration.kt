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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*

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

    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var receptionistName by remember { mutableStateOf("Loading...") }
    var receptionistPosition by remember { mutableStateOf("Loading...") }

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(currentUserId) {
        currentUserId?.let { userId ->
            val userDocRef = db.collection("users").document(userId)

            userDocRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    receptionistName = document.getString("name") ?: "Receptionist"
                    receptionistPosition = document.getString("position") ?: "Receptionist"
                } else {
                    receptionistName = "Receptionist"
                    receptionistPosition = "Receptionist"
                }
            }.addOnFailureListener {
                receptionistName = "Receptionist"
                receptionistPosition = "Receptionist"
            }
        }
    }

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

    // Fetch patient details if patientId is provided
    LaunchedEffect(patientId) {
        patientId?.let {
            db.collection("patients").document(it).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    name = TextFieldValue(document.getString("name") ?: "")
                    address = TextFieldValue(document.getString("address") ?: "")
                    phone = TextFieldValue(document.getString("phone") ?: "")
                    selectedGender = document.getString("gender") ?: "Male"
                    imageUri = document.getString("imageUri")
                    dateOfBirth = document.getString("dateOfBirth")?.let { dateStr ->
                        LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    }
                    todayDate = document.getString("visitingDate")?.let { dateStr ->
                        LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    } ?: LocalDate.now()
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to fetch details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            topBarId(
                name = receptionistName,
                position = receptionistPosition,
                screenName = "Patient Details",
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

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 16.sp),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

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

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 16.sp),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

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
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GenderRadioButton(selectedGender = selectedGender, onGenderSelected = { selectedGender = it })
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Photo",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.2f))
                            .clickable { /* Add photo upload logic here */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Add Photo",
                            tint = Color.Gray,
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    ElevatedButton(
                        onClick = {
                            val updatedPatientDetails = hashMapOf(
                                "name" to name.text,
                                "age" to age,
                                "address" to address.text,
                                "phone" to phone.text,
                                "gender" to selectedGender,
                                "imageUri" to imageUri,
                                "dateOfBirth" to dateOfBirth?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                "visitingDate" to todayDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            )

                            patientId?.let { id ->
                                db.collection("patients")
                                    .document(id)
                                    .set(updatedPatientDetails)
                                    .addOnSuccessListener {
                                        Log.d("Firestore", "Patient details successfully updated with ID: $id")
                                        Toast.makeText(context, "Details updated", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w("Firestore", "Error updating document", e)
                                        Toast.makeText(context, "Update unsuccessful, try again!", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            navController.navigate("patientDetails/{patientId}")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39A6EE)),
                        modifier = Modifier.fillMaxWidth()  // Make button fill the width
                    ) {
                        Text(
                            text = "Save Details",
                            style = TextStyle(fontSize = 16.sp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GenderRadioButton(selectedGender: String, onGenderSelected: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selectedGender == "Male",
            onClick = { onGenderSelected("Male") }
        )
        Text("Male", modifier = Modifier.padding(start = 8.dp))

        Spacer(modifier = Modifier.width(16.dp))

        RadioButton(
            selected = selectedGender == "Female",
            onClick = { onGenderSelected("Female") }
        )
        Text("Female", modifier = Modifier.padding(start = 8.dp))

        Spacer(modifier = Modifier.width(16.dp))

        RadioButton(
            selected = selectedGender == "Other",
            onClick = { onGenderSelected("Other") }
        )
        Text("Other", modifier = Modifier.padding(start = 8.dp))
    }
}

fun calculateAge(dateOfBirth: LocalDate, todayDate: LocalDate): String {
    val period = Period.between(dateOfBirth, todayDate)
    return period.years.toString()
}
