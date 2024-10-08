package com.example.eyecare.Doctor

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eyecare.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun chooseMode(navController: NavController) {


    Scaffold(modifier = Modifier.padding(10.dp).fillMaxSize()) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFFB0E0E6), Color.Transparent),
                    radius = 3500f
                ),
                center = Offset(x = size.width * 0.75f, y = size.height / 2),
                radius = size.minDimension * 2.5f
            )
        }
        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()) {
            Column (verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { navController.navigate("doctorpatients") }.border(2.dp, Color.Black, RoundedCornerShape(8.dp)).padding(10.dp)) {
                Image(
                    painter = painterResource(R.drawable.online),
                    contentDescription = "Online Image",
                    modifier = Modifier.size(150.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text("Online", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(15.dp))
            Column (verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { navController.navigate("offlineEntries") }.border(2.dp, Color.Black, RoundedCornerShape(8.dp)).padding(10.dp)) {
                Image(
                    painter = painterResource(R.drawable.offline),
                    contentDescription = "Online Image",
                    modifier = Modifier.size(150.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text("Offline", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}