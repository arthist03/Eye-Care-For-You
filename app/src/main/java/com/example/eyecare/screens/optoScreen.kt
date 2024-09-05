package com.example.eyecare.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment


@Composable
fun optoScreen() {

    Column (modifier = Modifier
        .fillMaxSize()
        .background(color = Color.Gray),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center)
    {
        Text(text = "Welcome To OPTOMETRIST Screen")
    }
}

@Preview
@Composable
private fun optoPrev() {
    optoScreen()
}