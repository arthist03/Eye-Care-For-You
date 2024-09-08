package com.example.eyecare.topBar

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.FabPosition
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eyecare.Extra.AuthState
import com.example.eyecare.Extra.AuthViewModel

@Composable
fun topBarId(name: String, position: String, screenName:String,authViewModel: AuthViewModel,navController:NavController) {
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current


    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.UnAuthenticated-> navController.navigate("home")
            is AuthState.Error -> Toast.makeText(context, (authState.value as AuthState.Error).message,
                Toast.LENGTH_SHORT)
            else -> Unit
        }
    }


    Column (modifier = Modifier.statusBarsPadding()){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(listOf(Color(0xFF1242E6), Color(0xFF39A6EE))),
                    shape = RoundedCornerShape(bottomStart = 45.dp, bottomEnd = 45.dp), 0.9f,
                )
                .padding(15.dp)
                .size(130.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Pic",
                modifier = Modifier
                    .size(70.dp)
            )
            Spacer(modifier = Modifier.size(25.dp))

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .size(240.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.W500,
                    color = Color.White
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = position,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W300,
                    color = Color.White
                )
            }

            TextButton(onClick = {authViewModel.signout()}) {
                Text(text = "Sign out", color = Color.White, textAlign = TextAlign.End)
            }
        }
        screenName(screenName = screenName)
    }
}