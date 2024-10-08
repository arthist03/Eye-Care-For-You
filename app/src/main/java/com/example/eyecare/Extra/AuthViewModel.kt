package com.example.eyecare.Extra

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import android.util.Base64

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthState()
    }

    fun checkAuthState() {
        if (auth.currentUser == null) {
            _authState.value = AuthState.UnAuthenticated
        } else {
            _authState.value = AuthState.Authenticated
        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or Password can't be empty")
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Get the logged-in user's ID
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    // Fetch user details from Firestore based on userId
                    fetchUserRole(userId)
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Login failed!")
                }
            }
    }

    private fun fetchUserRole(userId: String) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role")
                    when (role) {
                        "HOD" -> _authState.value = AuthState.RedirectToHOD
                        "DOCTOR" -> _authState.value = AuthState.RedirectToDoctor
                        "OPTOMETRIST" -> _authState.value = AuthState.RedirectToOptometrist
                        "RECEPTIONIST" -> _authState.value = AuthState.RedirectToReceptionist
                        "ADMIN" -> _authState.value = AuthState.RedirectToAdmin
                        else -> _authState.value = AuthState.Error("Invalid role assigned")
                    }
                } else {
                    _authState.value = AuthState.Error("User data not found!")
                }
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Error("Failed to fetch user role: ${e.message}")
            }
    }

    fun signup(email: String, password: String, confirmPassword: String, name: String, phone: String, selectedRole: String, fullName: String) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty() || phone.isEmpty() || selectedRole.isEmpty() || fullName.isEmpty()) {
            _authState.value = AuthState.Error("Enter details in all fields")
            return
        }

        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    saveUserDetails(userId, name, email, password, phone, selectedRole, fullName)
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Signup failed!")
                }
            }
    }

    private fun saveUserDetails(userId: String, name: String, email: String, password: String, phone: String, role: String, fullName:String) {
        val user = hashMapOf(
            "id" to userId,
            "name" to name,
            "email" to email,
            "password" to password,  // Consider encrypting passwords before storing.
            "phone" to phone,
            "role" to role.uppercase(),  // Convert role to uppercase.
            "fullName" to fullName
            )

        firestore.collection("users").document(userId).set(user)
            .addOnSuccessListener {
                Log.d("AuthViewModel", "User data saved successfully")
                // Redirect after signup based on role
                when (role.uppercase()) {
                    "HOD" -> _authState.value = AuthState.RedirectToHOD
                    "DOCTOR" -> _authState.value = AuthState.RedirectToDoctor
                    "OPTOMETRIST" -> _authState.value = AuthState.RedirectToOptometrist
                    "RECEPTIONIST" -> _authState.value = AuthState.RedirectToReceptionist
                    "ADMIN" -> _authState.value = AuthState.RedirectToAdmin
                    else -> _authState.value = AuthState.Error("Unknown role")
                }
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Error("Failed to save user data! Error: ${e.message}")
            }
    }


    fun signout() {
        auth.signOut()
        _authState.value = AuthState.UnAuthenticated
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object UnAuthenticated : AuthState()
    object Loading : AuthState()
    object RedirectToHOD : AuthState()
    object RedirectToDoctor : AuthState()
    object RedirectToOptometrist : AuthState()
    object RedirectToReceptionist : AuthState()
    object RedirectToAdmin : AuthState()
    data class Error(val message: String) : AuthState()
}

