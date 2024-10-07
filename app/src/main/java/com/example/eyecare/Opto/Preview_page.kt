package com.example.eyecare.Opto.glassScreens

import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eyecare.Opto.Patient
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptoCheckupScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    // State to hold patient details
    var patients by remember { mutableStateOf<List<Patient>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    // Fetch data from Firestore
    LaunchedEffect(Unit) {
        try {
            val snapshot = db.collection("patients").get().await()
            patients = snapshot.toObjects(Patient::class.java)
            isLoading = false
        } catch (e: Exception) {
            errorMessage = "Error fetching data: ${e.message}"
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Optometry Check-up") },
                actions = {
                    IconButton(onClick = { generatePDF(context, patients) }) {
                        Text("Generate PDF")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(patients) { patient ->
                        Card(modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Name: ${patient.name}")
                                Text(text = "ID: ${patient.id}")
                                Text(text = "Age: ${patient.age} Years")
                                // Add other fields as necessary
                            }
                        }
                    }
                }
            }
        }
    }
}

fun generatePDF(context: Context, patients: List<Patient>) {
    val pdfFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "PatientSummary.pdf")

    try {
        // Create a new PDF document
        val document = PDDocument()

        // Add a new page to the document
        val page = PDPage()
        document.addPage(page)

        // Prepare content stream
        val contentStream = PDPageContentStream(document, page)

        // Set font
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12f)

        // Add title
        contentStream.beginText()
        contentStream.newLineAtOffset(50f, 750f)
        contentStream.showText("Optometry Check-up Summary")
        contentStream.endText()

        // Write each patient's details
        var yPosition = 720f
        for (patient in patients) {
            contentStream.beginText()
            contentStream.newLineAtOffset(50f, yPosition)
            contentStream.showText("Name: ${patient.name}")
            contentStream.newLineAtOffset(0f, -15f)
            contentStream.showText("ID: ${patient.id}")
            contentStream.newLineAtOffset(0f, -15f)
            contentStream.showText("Age: ${patient.age} Years")
            contentStream.newLineAtOffset(0f, -15f)
            contentStream.newLineAtOffset(0f, -15f)
            contentStream.newLineAtOffset(0f, -30f)
            contentStream.showText("-------------------------------------------")
            contentStream.endText()

            yPosition -= 120f // Move to the next position
            if (yPosition < 100f) {
                // Add new page if needed
                contentStream.close()
                val newPage = PDPage()
                document.addPage(newPage)
                yPosition = 720f
                contentStream.close()
                PDPageContentStream(document, newPage).use { newContentStream ->
                    newContentStream.setFont(PDType1Font.HELVETICA_BOLD, 12f)
                }
            }
        }

        contentStream.close()

        // Save PDF document
        document.save(pdfFile)
        document.close()

        // Show toast
        Toast.makeText(context, "PDF Generated: ${pdfFile.path}", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error generating PDF: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
