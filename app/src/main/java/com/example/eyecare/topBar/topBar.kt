package com.example.eyecare.topBar

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eyecare.Extra.AuthState
import com.example.eyecare.Extra.AuthViewModel

@Composable
fun topBarId(fullName: String, position: String, screenName: String, authViewModel: AuthViewModel, navController: NavController) {
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.UnAuthenticated -> navController.navigate("home")
            is AuthState.Error -> Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    Column(modifier = Modifier.statusBarsPadding()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF29B2C4), Color(0xFFFFB0E0E6)) // Updated gradient colors
                    ),
                    shape = RoundedCornerShape(bottomStart = 45.dp, bottomEnd = 45.dp),
                )
                .padding(15.dp)
                .height(80.dp), // Adjusted height
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Pic",
                    modifier = Modifier.size(70.dp)
                )
                Spacer(modifier = Modifier.size(25.dp))

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = fullName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W500,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = position,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W300,
                        color = Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.width(20.dp))

            TextButton(onClick = { authViewModel.signout() }) {
                Text(text = "Sign out", color = Color.Black)
            }
        }
        screenName(screenName = screenName)
    }
}