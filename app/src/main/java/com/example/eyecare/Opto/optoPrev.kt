package com.example.eyecare.Opto

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExaminationDetailsScreen(navController: NavController, patientId: String) {
    val db = FirebaseFirestore.getInstance()

    // State variables for patient details
    var patientName by remember { mutableStateOf("") }
    var patientAge by remember { mutableStateOf("") }
    var visitingDate by remember { mutableStateOf("") }

    // State variables for examination data from each screen type
    var withoutGlassData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var withGlassData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var newGlassData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    // Get current date in the required format
    val currentVisitingDate = SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(Date())

    // List of screen types
    val screenTypes = listOf("withoutGlassOpto", "withGlassOpto", "newGlassOpto")

    // Fetch patient details and examination data for each screen type
    LaunchedEffect(patientId) {
        try {
            // Fetch patient details
            val patientSnapshot = db.collection("patients").document(patientId).get().await()
            if (patientSnapshot.exists()) {
                patientName = patientSnapshot.getString("name") ?: "Unknown"
                patientAge = patientSnapshot.getString("age") ?: "Unknown"
                visitingDate = patientSnapshot.getString("visitingDate") ?: currentVisitingDate
            } else {
                errorMessage = "No patient details found"
            }

            // Fetch examination data for each screen type
            screenTypes.forEach { screenType ->
                val screenSnapshot = db.collection("patients")
                    .document(patientId)
                    .collection("visits")
                    .document(currentVisitingDate)
                    .collection(screenType)
                    .document(currentVisitingDate)
                    .get().await()

                if (screenSnapshot.exists()) {
                    when (screenType) {
                        "withoutGlassOpto" -> withoutGlassData = screenSnapshot.data
                        "withGlassOpto" -> withGlassData = screenSnapshot.data
                        "newGlassOpto" -> newGlassData = screenSnapshot.data
                    }
                } else {
                    Log.e("Examination Fetch", "No data found for screen type: $screenType")
                }
            }

        } catch (e: Exception) {
            errorMessage = "Failed to fetch data: ${e.message}"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Examination Summary") })
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                ElevatedButton(onClick = {
                    navController.popBackStack()
                }) {
                    Text(text = "Back")
                }
            } else {
                Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {

                    // Display Patient Details
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Patient Name: $patientName", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(text = "ID: $patientId", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Absolute.SpaceBetween) {
                            Text(text = "Visiting Date: $visitingDate", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Age: $patientAge", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Function to render tables for each screen type
                    @Composable
                    fun renderExaminationTable(screenType: String, data: Map<String, Any>?) {
                        if (data == null) {
                            Text(text = "No data available for $screenType", fontWeight = FontWeight.Bold)
                            return
                        }

                        // Table Header
                        Text(text = "$screenType", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Gray)
                                .border(1.dp, Color.Black),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Parameter",
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(8.dp),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Right Eye",
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(8.dp),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Left Eye",
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(8.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Display Data in Table Format
                        val rightEyeData = mutableMapOf<String, String>()
                        val leftEyeData = mutableMapOf<String, String>()

                        // Segment data based on key patterns
                        data.forEach { (key, value) ->
                            when {
                                key.contains("right", ignoreCase = true) -> rightEyeData[key.replace("right", "").trim()] = value.toString()
                                key.contains("left", ignoreCase = true) -> leftEyeData[key.replace("left", "").trim()] = value.toString()
                            }
                        }

                        // Get unique parameter names (without "right" or "left")
                        val parameters = rightEyeData.keys.union(leftEyeData.keys)

                        // Display each row with unified parameters
                        parameters.forEach { parameter ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color.Black),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = parameter,  // Unified parameter name
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(8.dp)
                                )
                                Text(
                                    text = rightEyeData[parameter] ?: "N/A",  // Right eye value
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(8.dp)
                                )
                                Text(
                                    text = leftEyeData[parameter] ?: "N/A",  // Left eye value
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(8.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Render tables for each screen type
                    renderExaminationTable("Without Glass Opto", withoutGlassData)
                    renderExaminationTable("With Glass Opto", withGlassData)
                    renderExaminationTable("New Glass Opto", newGlassData)

                    Spacer(modifier = Modifier.height(32.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ElevatedButton(onClick = {
                            navController.popBackStack()
                        }) {
                            Text(text = "Back")
                        }
                        ElevatedButton(onClick = {
                            // Export functionality to be implemented
                        }) {
                            Text(text = "Export")
                        }
                        ElevatedButton(onClick = {
                            navController.navigate("OptoPatients") {
                                popUpTo("OptoPatients") { inclusive = true }
                            }
                        }) {
                            Text(text = "Go to Patients")
                        }
                    }
                }
            }
        }
    }
}