package com.example.eyecare.reception

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
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
import coil.compose.rememberAsyncImagePainter
import com.example.eyecare.Extra.AuthViewModel
import com.example.eyecare.topBar.topBarId
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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

    val imageCaptureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            // You can save the image or convert it to a URI here
            // For this example, we just set it as an imageUri (string path) for display
            imageUri = saveBitmapToFile(it, context).toString() // You'll need to implement this
        }
    }

    LaunchedEffect(patientId) {
        val patientDocRef = db.collection("patients").document(patientId ?: "")

        patientDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                name = TextFieldValue(document.getString("name") ?: "")
                address = TextFieldValue(document.getString("address") ?: "")
                phone = TextFieldValue(document.getString("phone") ?: "")
                selectedGender = document.getString("gender") ?: "Male"

                // Fetch and set the date of birth if it exists
                val dobString = document.getString("dateOfBirth")
                if (!dobString.isNullOrEmpty()) {
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    val dob = LocalDate.parse(dobString, formatter)
                    dateOfBirth = dob // assuming selectedDateOfBirth is a LocalDate
                }
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
                    receptionistName = document.getString("fullName") ?: "Receptionist"
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
                fullName = receptionistName,
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
                        horizontalArrangement = Arrangement.spacedBy(1.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedGender == "Male",
                            onClick = { selectedGender = "Male" }
                        )
                        Text(text = "Male")


                        RadioButton(
                            selected = selectedGender == "Female",
                            onClick = { selectedGender = "Female" }
                        )
                        Text(text = "Female")


                        RadioButton(
                            selected = selectedGender == "Other",
                            onClick = { selectedGender = "Other" }
                        )
                        Text(text = "Other")
                    }




                    Spacer(modifier = Modifier.height(12.dp))

                    // Display profile image if available
                    if (imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = "Captured Image",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        ElevatedButton(
                            onClick = { imageCaptureLauncher.launch() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Capture Image")
                        }
                    } else {
                        ElevatedButton(
                            onClick = { imageCaptureLauncher.launch() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Capture Image")
                        }
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
                                age = age,
                                id= patientId
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


// Function to calculate age from birth date
fun calculateAge(dateOfBirth: LocalDate, visitingDate: LocalDate): String {
    val period = Period.between(dateOfBirth, visitingDate)
    return period.years.toString()
}

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
    age: String,
    id: String?
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // Check if a patient with the same name and ID already exists
            val existingPatientQuery = db.collection("patients")
                .whereEqualTo("name", name)
                .whereEqualTo("id", id)
                .get()
                .await()

            // If the patient exists, use their existing ID
            val patientId = if (existingPatientQuery.documents.isNotEmpty()) {
                existingPatientQuery.documents.first().id
            } else {
                // Generate a unique patient ID
                generateUniquePatientId(db)
            }

            // Create a map of patient details
            val patientDetails = hashMapOf<String, Any>(
                "name" to name,
                "address" to address,
                "phone" to phone,
                "gender" to gender,
                "age" to age,
                "id" to patientId
            )

            // Only add non-nullable fields to the map
            dateOfBirth?.let {
                patientDetails["dateOfBirth"] = it.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            }

            // Upload image to Firebase Storage and get the download URL
            if (imageUri != null) {
                val imageUrl = uploadImageToFirebaseStorage(imageUri, patientId)
                if (imageUrl != null) {
                    patientDetails["imageUri"] = imageUrl
                }
            }

            // Save patient details in the main document
            db.collection("patients").document(patientId)
                .set(patientDetails, SetOptions.merge()).await()

            // Assign the optometrist to the patient
            val (optometristId, optometristName) = assignOptometristToPatient(db, patientId, visitingDate)

            // Prepare visit data
            val visitData = hashMapOf<String, Any>(
                "assignedOptometristId" to optometristId,
                "assignedOptometristName" to optometristName,
                "visitingDate" to visitingDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) // Format as dd/MM/yyyy
            )

            // Save visit data under visits collection with the full date as the document ID in dd_MM_yyyy format
            db.collection("patients").document(patientId)
                .collection("visits")
                .document(visitingDate.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"))) // Format as dd_MM_yyyy for document ID
                .set(visitData, SetOptions.merge()).await()

            // Assign the patient to the optometrist with a date-based document
            assignPatientToOptometristWithDate(
                db = db,
                patientName = name,  // Use patient's name instead of ID
                optometristId = optometristId,
                optometristName = optometristName,  // Use optometrist's name
                patientData = patientDetails,  // Pass patient details
                visitingDate = visitingDate
            )

            // Show a success message
            withContext(Dispatchers.Main) {
                Toast.makeText(navController.context, "$name visit saved and assigned to $optometristName!", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.e("PatientDetailsScreen", "Error saving patient data", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(navController.context, "Failed to save patient visit data.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

private suspend fun uploadImageToFirebaseStorage(imageUri: String, patientId: String): String? {
    return try {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("patient_images/$patientId.jpg")

        // Create a file from the URI
        val file = Uri.parse(imageUri)
        val uploadTask = storageRef.putFile(file).await()

        // Get the download URL
        storageRef.downloadUrl.await().toString()
    } catch (e: Exception) {
        Log.e("ImageUpload", "Error uploading image", e)
        null
    }
}

private fun saveBitmapToFile(bitmap: Bitmap, context: Context): File? {
    return try {
        // Create a file in the app's internal storage
        val file = File(context.filesDir, "image_${System.currentTimeMillis()}.jpg")
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.flush()
        fos.close()
        file // Return the created file
    } catch (e: IOException) {
        Log.e("BitmapSave", "Error saving bitmap to file", e)
        null
    }
}



suspend fun assignPatientToOptometristWithDate(
    db: FirebaseFirestore,
    patientName: String,  // Use patient name instead of ID
    optometristId: String,
    optometristName: String,  // Pass optometrist's name
    patientData: Map<String, Any>,
    visitingDate: LocalDate
) {
    // Format the visiting date to use as a document ID
    val dateStr = visitingDate.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"))

    // Construct the patient-specific data
    val patientDataWithOptometrist = patientData + mapOf(
        "assignedOptometristName" to optometristName
    )

    // Save the patient data directly as a document within the date document
    db.collection("users")
        .document(optometristId)
        .collection("AssignedPatients")
        .document(dateStr)  // Date document in dd/MM/yyyy format
        .set(mapOf(patientName to patientDataWithOptometrist), SetOptions.merge())  // Use patient's name as the key
        .await()
}

suspend fun assignOptometristToPatient(db: FirebaseFirestore, patientId: String, visitingDate: LocalDate): Pair<String, String> {
    val usersCollection = db.collection("users")

    // Get all users with the role "OPTOMETRIST"
    val optometrists = usersCollection
        .whereEqualTo("role", "OPTOMETRIST")
        .get()
        .await()
        .documents

    var selectedOptometristId: String? = null
    var selectedOptometristName: String? = null
    var leastAssignedPatients = Int.MAX_VALUE

    val visitingDateStr = visitingDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

    for (optometrist in optometrists) {
        val assignedPatientsCollection = usersCollection.document(optometrist.id)
            .collection("AssignedPatients")
            .document(visitingDateStr)
            .get()
            .await()

        val assignedPatientsCount = assignedPatientsCollection.data?.size ?: 0

        // Find the optometrist with the least number of assigned patients for the given date
        if (assignedPatientsCount < leastAssignedPatients) {
            selectedOptometristId = optometrist.id
            selectedOptometristName = optometrist.getString("fullName") ?: "Unknown"
            leastAssignedPatients = assignedPatientsCount
        }
    }

    // Update the selected optometrist's assigned patients list for the visiting date
    selectedOptometristId?.let { optometristId ->
        db.collection("users").document(optometristId)
            .collection("AssignedPatients")
            .document(visitingDateStr)
            .set(mapOf(patientId to true), SetOptions.merge())
            .await()
    }

    return Pair(selectedOptometristId ?: throw Exception("No available optometrist found"), selectedOptometristName ?: "Unknown")
}