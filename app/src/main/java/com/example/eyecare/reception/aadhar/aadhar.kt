package com.example.eyecare.reception.aadhar

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.ByteArrayOutputStream

// Data class for the Aadhaar response
data class AadhaarResponse(
    val name: String,
    val gender: String,
    val dob: String,
    val mobile_number: String,
    val aadhaar_number: String,
    val address: String
)

// Retrofit API interface
interface AadhaarApi {
    @Multipart
    @POST("extractAadhaarData") // Replace with your actual endpoint
    suspend fun extractData(@Part file: MultipartBody.Part): AadhaarResponse
}

// Main Activity to host the Camera and Aadhaar extraction composable
class CameraCaptureActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraCaptureAndExtract()
        }
    }
}

@Composable
fun CameraCaptureAndExtract() {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var extractedData by remember { mutableStateOf<AadhaarResponse?>(null) }
    var isCameraReady by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Request camera permissions and setup camera
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as ComponentActivity,
                arrayOf(Manifest.permission.CAMERA),
                1001
            )
        } else {
            isCameraReady = true
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (isCameraReady) {
            // Show camera preview
            CameraPreview { capturedBitmap ->
                bitmap = capturedBitmap
                uploadImage(capturedBitmap) { response ->
                    extractedData = response
                }
            }

            // Display the captured image
            bitmap?.let { imgBitmap ->
                Image(
                    bitmap = imgBitmap.asImageBitmap(),
                    contentDescription = "Captured Image",
                    modifier = Modifier.size(200.dp)
                )
            }

            // Display the extracted Aadhaar details
            extractedData?.let { data ->
                Column {
                    BasicText("Name: ${data.name}")
                    BasicText("Gender: ${data.gender}")
                    BasicText("DOB: ${data.dob}")
                    BasicText("Mobile Number: ${data.mobile_number}")
                    BasicText("Aadhaar Number: ${data.aadhaar_number}")
                    BasicText("Address: ${data.address}")
                }
            }
        } else {
            BasicText("Camera permission denied")
        }
    }
}

@Composable
fun CameraPreview(onImageCaptured: (Bitmap) -> Unit) {
    val context = LocalContext.current // Obtain context here, in a @Composable function
    val previewView = remember { PreviewView(context) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    AndroidView(factory = { previewView }) { previewView ->
        val preview = Preview.Builder().build()
        imageCapture = ImageCapture.Builder().build()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            preview.setSurfaceProvider(previewView.surfaceProvider)
            cameraProvider.bindToLifecycle(
                context as LifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )

        }, ContextCompat.getMainExecutor(context))
    }

    // Capture button inside composable function
    Button(onClick = {
        imageCapture?.let {
            captureImage(it, onImageCaptured, context) // Pass context explicitly
        }
    }) {
        Text("Capture Image")
    }
}

// Function to capture the image (No @Composable needed)
fun captureImage(imageCapture: ImageCapture, onImageCaptured: (Bitmap) -> Unit, context: android.content.Context) {
    val outputOptions = ImageCapture.OutputFileOptions.Builder(ByteArrayOutputStream()).build()

    imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                // Convert to Bitmap for displaying and processing
                // Placeholder, replace with actual image saving logic
                val bitmap = Bitmap.createBitmap(800, 600, Bitmap.Config.ARGB_8888)
                onImageCaptured(bitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
            }
        })
}



fun uploadImage(bitmap: Bitmap, onResponse: (AadhaarResponse) -> Unit) {
    // Convert Bitmap to a byte array
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    val byteArray = stream.toByteArray()

    // Convert byte array to MultipartBody.Part
    val requestFile = MultipartBody.Part.createFormData(
        "file", "aadhaar_image.jpg", byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
    )

    // Create Retrofit instance and call API
    val retrofit = Retrofit.Builder()
        .baseUrl("https://uidai.gov.in//") // Use a mock API for testing
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    val aadhaarApi = retrofit.create(AadhaarApi::class.java)

    CoroutineScope(Dispatchers.IO).launch {
        try {
            // Make the network call
            val response = aadhaarApi.extractData(requestFile)

            // Switch to Main thread to update the UI
            withContext(Dispatchers.Main) {
                onResponse(response) // Send the extracted data to the UI
            }
        } catch (e: Exception) {
            e.printStackTrace() // Handle the error here (logging or showing an error message)
        }
    }
}
