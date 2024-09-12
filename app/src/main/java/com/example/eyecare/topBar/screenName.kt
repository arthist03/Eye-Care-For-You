package com.example.eyecare.topBar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun screenName(screenName: String) {
    Row (modifier = Modifier
        .fillMaxWidth()
        .offset(y = -20.dp)
        .clip(shape = RoundedCornerShape(20.dp)),
        horizontalArrangement = Arrangement.Center){

        Box(modifier = Modifier
            .size(350.dp, 50.dp)
            .padding(4.dp)
            .border(2.dp, Color.Black, shape = RoundedCornerShape(20.dp))
            .clip(shape = RoundedCornerShape(20.dp))


        ){
            Column(
            modifier = Modifier.fillMaxSize()
                .background(color = Color(0xFF37B0D5)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = screenName,
                fontSize = 25.sp,
                fontWeight = FontWeight.W400,
                color = Color.Black
            )
        }
        }
    }
}