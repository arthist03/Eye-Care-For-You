package com.example.eyecare.Opto.glassScreens

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SeekerWithTextField() {
    var textFieldValue by remember { mutableStateOf("90") }
    var showSeeker by remember { mutableStateOf(false) }
    var seekerValue by remember { mutableStateOf(90) }

    // Update textFieldValue when seekerValue changes
    LaunchedEffect(seekerValue) {
        textFieldValue = seekerValue.toString()
    }

    Box(
        modifier = Modifier

            .fillMaxWidth()

            .padding(16.dp),

        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = { newValue ->
                        val intValue = newValue.toIntOrNull()
                        if (intValue != null && intValue in 0..180) {
                            seekerValue = intValue
                        }
                        textFieldValue = newValue // Allow empty value as well
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("Enter Angle") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { showSeeker = !showSeeker }) {
                    Text("Open Seeker")
                }
            }
            if (showSeeker) {
                Spacer(modifier = Modifier.height(16.dp))
                CustomSemiCircularSeeker(
                    modifier = Modifier.size(250.dp),
                    initialValue = seekerValue,
                    primaryColor = Color.Black,
                    secondaryColor = Color.LightGray,
                    circleRadius = 100f,
                    onPositionChange = { newValue ->
                        seekerValue = newValue
                    }
                )
            }
        }
    }
}

@Composable
fun CustomSemiCircularSeeker(
    modifier: Modifier = Modifier,
    initialValue: Int,
    primaryColor: Color,
    secondaryColor: Color,
    minValue: Int = 0,
    maxValue: Int = 180,
    circleRadius: Float,
    onPositionChange: (Int) -> Unit
) {
    var circleCenter by remember { mutableStateOf(Offset.Zero) }
    var positionValue by remember { mutableStateOf(initialValue) }

    // Update positionValue when initialValue changes
    LaunchedEffect(initialValue) {
        positionValue = initialValue
    }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val touchAngle = calculateTouchAngle(change.position, circleCenter, circleRadius)
                    val newPosition = ((touchAngle / 180f) * (maxValue - minValue) + minValue).toInt()
                    positionValue = newPosition.coerceIn(minValue, maxValue)
                    onPositionChange(positionValue)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val circleThickness = width / 30f
            circleCenter = Offset(x = width / 2f, y = height / 2f)

            // Semi-circle background
            drawArc(
                color = secondaryColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                style = Stroke(width = circleThickness, cap = StrokeCap.Round),
                size = Size(width = circleRadius * 5f, height = circleRadius * 5f),
                topLeft = Offset((circleRadius) , (circleRadius))
            )

            // Foreground arc
            drawArc(
                color = Color(0xFF3DB69E),
                startAngle = 180f,
                sweepAngle = (positionValue - minValue) * 180f / (maxValue - minValue),
                useCenter = false,
                style = Stroke(width = circleThickness * 1.5f, cap = StrokeCap.Round),
                size = Size(width = circleRadius * 5f, height = circleRadius * 5f),
                topLeft = Offset((circleRadius) , (circleRadius))
            )

            // Draw the current value in the center
            drawContext.canvas.nativeCanvas.apply {
                drawIntoCanvas {
                    drawText(
                        "$positionValue°",
                        circleCenter.x,
                        circleCenter.y + circleRadius + -25.dp.toPx(),
                        Paint().apply {
                            textSize = 25.sp.toPx()
                            textAlign = Paint.Align.CENTER
                            color = primaryColor.toArgb()
                            isFakeBoldText = true
                        }
                    )
                }
            }
        }
    }
}

fun calculateTouchAngle(touchPoint: Offset, center: Offset, radius: Float): Float {
    val dx = touchPoint.x - center.x
    val dy = center.y - touchPoint.y  // Keep this to reverse y-axis

    var angle = Math.toDegrees(Math.atan2(dy.toDouble(), dx.toDouble())).toFloat()

    // Normalize the angle to ensure smooth movement between 0 and 180 degrees
    angle = if (angle < 0) angle + 360 else angle

    // Invert angle for correct clockwise rotation (since default angles work counterclockwise)
    angle = 180f - angle

    // Ensure the angle stays in the [0, 180] range
    return angle.coerceIn(0f, 180f)
}

@Preview(showBackground = true)
@Composable
fun SeekerWithTextFieldPreview() {
    SeekerWithTextField()
}