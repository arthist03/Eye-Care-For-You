package com.example.eyecare.adminPage

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eyecare.Extra.AuthViewModel
import com.example.eyecare.Extra.LoadingAnimation
import com.example.eyecare.topBar.topBarId
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun editUser(navController: NavController, authViewModel: AuthViewModel) {
    val db = FirebaseFirestore.getInstance()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    var adminName by remember { mutableStateOf("Loading...") }
    var adminPosition by remember { mutableStateOf("Loading...") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    // Update the type of users and filteredUsers to List<AppUser>
    var users by remember { mutableStateOf(listOf<AppUser>()) }
    var filteredUsers by remember { mutableStateOf(listOf<AppUser>()) }
    var searchQuery by remember { mutableStateOf("") }

    if (currentUserId == null) {
        errorMessage = "No user is currently signed in."
        return
    }

    LaunchedEffect(currentUserId) {
        val userDocRef = db.collection("users").document(currentUserId)
        userDocRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                adminName = document.getString("fullName") ?: "Admin"
                adminPosition = document.getString("role") ?: "Admin"
            } else {
                adminName = "Admin"
                adminPosition = "Admin"
            }
            isLoading = false
        }.addOnFailureListener { exception ->
            errorMessage = "Failed to fetch user details: ${exception.message}"
            isLoading = false
        }
    }

    DisposableEffect(Unit) {
        val firestoreRegistration = db.collection("users")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    errorMessage = "Error fetching patients: ${e.message}"
                    isLoading = false
                    return@addSnapshotListener
                }

                val fetchedUsers = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(AppUser::class.java)?.copy(id = document.id)
                } ?: emptyList()

                users = fetchedUsers
                filteredUsers = fetchedUsers
                isLoading = false
            }

        onDispose {
            firestoreRegistration.remove()
        }
    }

    LaunchedEffect(searchQuery) {
        filteredUsers = if (searchQuery.isEmpty()) {
            users
        } else {
            users.filter { user ->
                user.name.startsWith(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
        ) {
            topBarId(
                fullName = adminName,
                position = adminPosition,
                screenName = "Admin Page",
                authViewModel = authViewModel, // Use passed viewModel
                navController = navController
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                label = { Text("Search Users") },
                placeholder = { Text("Enter name") },
                singleLine = true,
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    focusedPlaceholderColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White
                )
            )

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
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxSize()
                ) {
                    items(filteredUsers) { user ->
                        UserCard(user = user) {
                            navController.navigate("signup/${user.id}")
                        }
                    }
                }
            }
        }
    }
}

data class AppUser( // Renamed from User to AppUser
    val id: String = "",
    val imageUri: String? = null,
    val name: String = ""
)

@Composable
fun UserCard(user: AppUser, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Name: ${user.name}")
            Text(text = "Id: ${user.id}")
        }
    }
}
