import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.*

@Composable
fun AxisSelectorApp() {
    var angle by remember { mutableStateOf(0f) } // Angle value between 0 and 180 degrees
    val radius = 100.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Knob(
            modifier = Modifier.size(radius * 2),
            currentAngle = angle,
            onAngleChange = { newAngle ->
                angle = newAngle.coerceIn(0f, 180f) // Restrict angle between 0 and 180
            }
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Selected Angle: ${angle.toInt()}°")
    }
}

@Composable
fun Knob(
    modifier: Modifier = Modifier,
    currentAngle: Float,
    onAngleChange: (Float) -> Unit
) {
    var center by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = modifier
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                // Calculate the position relative to the center
                val touchX = change.position.x - center.x
                val touchY = change.position.y - center.y

                // Calculate angle in degrees
                val angleInRadians = atan2(touchY, touchX)
                val angleInDegrees = (angleInRadians * 180f / PI).toFloat()

                // Normalize the angle between 0 and 180 degrees
                val normalizedAngle = if (angleInDegrees < 0) {
                    360f + angleInDegrees
                } else {
                    angleInDegrees
                }

                // Limit the knob rotation to 0-180 degrees
                if (normalizedAngle in 180f..360f) {
                    onAngleChange(360f - normalizedAngle)
                } else if (normalizedAngle in 0f..180f) {
                    onAngleChange(normalizedAngle)
                }
            }
        }
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize(),
            onDraw = {
                center = this.center

                // Draw the background arc (180 degrees)
                drawArc(
                    color = Color.Gray,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    style = Stroke(20f)
                )

                // Calculate the angle in radians for the pointer position
                val radians = (currentAngle - 90) * (PI / 180).toFloat()

                // Draw the pointer on the arc
                val pointerLength = size.minDimension / 2
                val x = cos(radians) * pointerLength + size.width / 2
                val y = sin(radians) * pointerLength + size.height / 2
                drawCircle(
                    color = Color.Blue,
                    radius = 10f,
                    center = Offset(x, y)
                )
            }
        )
    }
}
