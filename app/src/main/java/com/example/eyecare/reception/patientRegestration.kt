package com.example.eyecare.reception

import android.content.Context
import android.util.Log
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.identity.util.UUID
import com.example.eyecare.Extra.AuthViewModel
import com.example.eyecare.topBar.topBarId
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.rpc.context.AttributeContext.Auth

@Composable
fun PatientDetailsScreen(navController: NavController) {
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var age by remember { mutableStateOf(TextFieldValue("")) }
    var address by remember { mutableStateOf(TextFieldValue("")) }
    var phone by remember { mutableStateOf(TextFieldValue("")) }
    var selectedGender by remember { mutableStateOf("Male") }
    var imageUri by remember { mutableStateOf<String?>(null) }

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

    Scaffold(
        topBar = {
            topBarId(
                name = receptionistName,  // Display the fetched name
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
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Patient Details",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.fillMaxWidth(),  // Make title responsive
                        textAlign = TextAlign.Center         // Center the title text
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 16.sp),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        label = { Text("Age") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(fontSize = 16.sp),
                        shape = RoundedCornerShape(10.dp)
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

                    Spacer(modifier = Modifier.height(20.dp))

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
                        horizontalArrangement = Arrangement.SpaceEvenly,
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

// Inside the ElevatedButton onClick block
                    ElevatedButton(
                        onClick = {
                            val patientId = generateUniqueId()

                            // Patient details map
                            val patientDetails = hashMapOf(
                                "id" to patientId,
                                "name" to name.text,
                                "age" to age.text,
                                "address" to address.text,
                                "phone" to phone.text,
                                "gender" to selectedGender,
                                "imageUri" to imageUri // Add photo upload logic later if necessary
                            )

                            // Firestore database instance
                            val db = FirebaseFirestore.getInstance()

                            // Realtime Database instance
                            val realtimeDb = FirebaseDatabase.getInstance().reference

                            // Store in Firestore
                            db.collection("patients")
                                .document(patientId)
                                .set(patientDetails)
                                .addOnSuccessListener {
                                    Log.d("Firestore", "Patient details successfully stored with ID: $patientId")
                                    Toast.makeText(context, "Registered with ID: $patientId", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Log.w("Firestore", "Error adding document", e)
                                    Toast.makeText(context, "Register unsuccessful, try again!", Toast.LENGTH_SHORT).show()
                                }

                            // Store in Realtime Database
                            realtimeDb.child("patients").child(patientId)
                                .setValue(patientDetails)
                                .addOnSuccessListener {
                                    Log.d("RealtimeDB", "Patient details successfully stored in Realtime Database with ID: $patientId")
                                    Toast.makeText(context, "Successfully saved to Realtime DB!", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Log.w("RealtimeDB", "Error adding data to Realtime Database", e)
                                    Toast.makeText(context, "Failed to save to Realtime DB, try again!", Toast.LENGTH_SHORT).show()
                                }
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

fun generateUniqueId(): String {
    val allowedChars = "0123456789"
    return (1..7)
        .map { allowedChars.random() }
        .joinToString("")
}