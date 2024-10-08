package com.example.eyecare.reception

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.eyecare.Extra.AuthViewModel
import com.example.eyecare.topBar.topBarId
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.IOException

@Composable
fun PageReception(navController: NavController, authViewModel: AuthViewModel) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    val db = FirebaseFirestore.getInstance()

    var receptionistName by remember { mutableStateOf("Loading...") }
    var receptionistPosition by remember { mutableStateOf("Loading...") }

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

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

    fun performSearch(query: String) {
        if (query.isNotBlank()) {
            db.collection("patients")
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + "\uf8ff")
                .get()
                .addOnSuccessListener { documents ->
                    val nameResults = documents.map { it.data }

                    db.collection("patients")
                        .whereGreaterThanOrEqualTo("phone", query)
                        .whereLessThanOrEqualTo("phone", query + "\uf8ff")
                        .get()
                        .addOnSuccessListener { phoneDocuments ->
                            val phoneResults = phoneDocuments.map { it.data }

                            db.collection("patients")
                                .whereGreaterThanOrEqualTo("id", query)
                                .whereLessThanOrEqualTo("id", query + "\uf8ff")
                                .get()
                                .addOnSuccessListener { idDocuments ->
                                    val idResults = idDocuments.map { it.data }
                                    searchResults = (nameResults + phoneResults + idResults).distinctBy { it["id"] }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Search by ID failed", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Search by phone failed", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Search by name failed", Toast.LENGTH_SHORT).show()
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFEBF5FB), Color(0xFFB3BBC4)))),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(receptionistName, receptionistPosition, navController, authViewModel)

        Column(
            modifier = Modifier.fillMaxSize().padding(top = 70.dp)
        ) {
            PatientEntryCard(searchQuery, { searchQuery = it; performSearch(it) }, searchResults, navController)
        }
    }
}

@Composable
fun TopBar(receptionistName: String, receptionistPosition: String, navController: NavController, authViewModel: AuthViewModel) {
    topBarId(
        fullName = receptionistName,
        position = receptionistPosition,
        screenName = "Patient Registration",
        authViewModel = authViewModel,
        navController = navController
    )
}

@Composable
fun PatientEntryCard(searchQuery: String, onSearchQueryChange: (String) -> Unit, searchResults: List<Map<String, Any>>, navController: NavController) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.elevatedCardElevation(12.dp),
        shape = RoundedCornerShape(15.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ElevatedButton(
                onClick = { navController.navigate("patientDetails/{patientId}") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF39C12))
            ) {
                Text(text = "Enter Patient Details Manually", color = Color.White)
            }

            Spacer(modifier = Modifier.height(20.dp))

            val cameraPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions(),
                onResult = { permissions ->
                    val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
                    val storageGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: false
                    if (cameraGranted && storageGranted) {
                        openCameraForResult(context) { uri ->
                            processImage(context, uri)
                        }
                    } else {
                        Toast.makeText(context, "Camera and Storage permissions are required.", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            ElevatedButton(
                onClick = {
                    // Check current permission status before launching the request
                    when {
                        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                            openCameraForResult(context) { uri ->
                                processImage(context, uri)
                            }
                        }
                        else -> cameraPermissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF39C12))
            ) {
                Text(text = "Capture Image", color = Color.White)
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Search by Name, Phone, or ID") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 16.sp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn {
                items(searchResults) { result ->
                    val id = result["id"] as? String ?: ""
                    val patientName = result["name"] as? String ?: ""

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                if (id.isNotEmpty()) {
                                    navController.navigate("patientDetails/$id")
                                } else {
                                    Toast.makeText(context, "Invalid Patient ID", Toast.LENGTH_SHORT).show()
                                }
                            },
                        elevation = CardDefaults.elevatedCardElevation(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(text = "Name: $patientName", fontSize = 16.sp)
                            Text(text = "Phone: ${result["phone"]}", fontSize = 16.sp)
                            Text(text = "ID: $id", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

// Camera-related code

private const val CAMERA_REQUEST_CODE_1 = 1001

fun openCameraForResult(context: Context, onResult: (Uri) -> Unit) {
    val imageFile = File.createTempFile("image", ".jpg", context.externalCacheDir)
    val imageUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)

    // Launch camera intent
    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

    // Check if context is an Activity to start camera intent
    if (context is android.app.Activity) {
        context.startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE_1)
        onResult(imageUri) // This may need to be adjusted based on actual image capture success
    }
}

private fun processImage(context: Context, imageUri: Uri) {
    // Implement your image processing logic here, for example, using ML Kit for text recognition
    val inputImage = InputImage.fromFilePath(context, imageUri)
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    recognizer.process(inputImage)
        .addOnSuccessListener { visionText ->
            // Handle recognized text
            Log.d("TextRecognition", "Recognized text: ${visionText.text}")
        }
        .addOnFailureListener { e ->
            Log.e("TextRecognition", "Text recognition failed: ${e.message}")
        }
}
