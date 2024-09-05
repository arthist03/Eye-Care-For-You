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
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something Went Wrong!")
                }
            }
    }

    fun signup(email: String, password: String, confirmPassword: String, name: String, phone: String, selectedRole: String) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty() || phone.isEmpty() || selectedRole.isEmpty()) {
            _authState.value = AuthState.Error("Enter details in all fields")
            return
        }

        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    try {
                        saveUserDetails(userId, name, email, password, phone, selectedRole)
                    } catch (e: Exception) {
                        _authState.value = AuthState.Error("Encryption failed: ${e.message}")
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something Went Wrong!")
                }
            }
    }

    private fun saveUserDetails(userId: String, name: String, email: String, password: String, phone: String, role: String) {
        val user = hashMapOf(
            "name" to name,
            "email" to email,
            "password" to password,  // Storing encrypted password
            "phone" to phone,
            "role" to role
        )

        firestore.collection("users").document(userId).set(user)
            .addOnSuccessListener {
                Log.d("AuthViewModel", "User data saved successfully") // Debugging log
                // Redirect to the specific role-based screen
                when (role) {
                    "HOD" -> _authState.value = AuthState.RedirectToHOD
                    "DOCTOR" -> _authState.value = AuthState.RedirectToDoctor
                    "OPTOMETRIST" -> _authState.value = AuthState.RedirectToOptometrist
                    "RECEPTIONIST" -> _authState.value = AuthState.RedirectToReceptionist
                    else -> _authState.value = AuthState.UnAuthenticated
                }
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Error("Failed to save user data! Error: ${e.message}")
            }
    }


    // Key generation for AES encryption
    private fun generateKey(secret: String): SecretKeySpec {
        val sha = MessageDigest.getInstance("SHA-256")
        var key = secret.toByteArray(Charsets.UTF_8)
        key = sha.digest(key)
        return SecretKeySpec(key.copyOf(16), "AES")
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
    data class Error(val message: String) : AuthState()
}
