package com.example.eyecare.Doctor.offlineScreens

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eyecare.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


@Composable
fun offlinScreen(navController: NavController) {

    var name by rememberSaveable { mutableStateOf("") }
    var docName by rememberSaveable { mutableStateOf("Dr. " + "") }
    var phone by rememberSaveable { mutableStateOf("") }
    var aadhar by rememberSaveable { mutableStateOf("") }
    var dateOfBirth by rememberSaveable { mutableStateOf<LocalDate?>(null) }
    var dateOfVisit by rememberSaveable { mutableStateOf(LocalDate.now()) }

    val context = LocalContext.current

    var isChecked by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

// Without Glass Data
    //For data Distance vision
    var DVRS by rememberSaveable { mutableStateOf("") }
    var DVRC by rememberSaveable { mutableStateOf("") }
    var DVLS by rememberSaveable { mutableStateOf("") }
    var DVLC by rememberSaveable { mutableStateOf("") }
    var DVRCA by rememberSaveable { mutableStateOf("") }
    var DVLCA by rememberSaveable { mutableStateOf("") }

    // Data for Near vision
    var NVRS by rememberSaveable { mutableStateOf("") }
    var NVRC by rememberSaveable { mutableStateOf("") }
    var NVLS by rememberSaveable { mutableStateOf("") }
    var NVLC by rememberSaveable { mutableStateOf("") }
    var NVRCA by rememberSaveable { mutableStateOf("") }
    var NVLCA by rememberSaveable { mutableStateOf("") }


// With Glass Data
    //For data Distance vision
    var DVRSW by rememberSaveable { mutableStateOf("") }
    var DVRCW by rememberSaveable { mutableStateOf("") }
    var DVLSW by rememberSaveable { mutableStateOf("") }
    var DVLCW by rememberSaveable { mutableStateOf("") }
    var DVRCAW by rememberSaveable { mutableStateOf("") }
    var DVLCAW by rememberSaveable { mutableStateOf("") }

    // Data for Near vision
    var NVRSW by rememberSaveable { mutableStateOf("") }
    var NVRCW by rememberSaveable { mutableStateOf("") }
    var NVLSW by rememberSaveable { mutableStateOf("") }
    var NVLCW by rememberSaveable { mutableStateOf("") }
    var NVRCAW by rememberSaveable { mutableStateOf("") }
    var NVLCAW by rememberSaveable { mutableStateOf("") }

// New Glass Data
    //For data Distance vision
    var DVRSN by rememberSaveable { mutableStateOf("") }
    var DVRCN by rememberSaveable { mutableStateOf("") }
    var DVLSN by rememberSaveable { mutableStateOf("") }
    var DVLCN by rememberSaveable { mutableStateOf("") }
    var DVRCAN by rememberSaveable { mutableStateOf("") }
    var DVLCAN by rememberSaveable { mutableStateOf("") }

    // Data for Near vision
    var NVRSN by rememberSaveable { mutableStateOf("") }
    var NVRCN by rememberSaveable { mutableStateOf("") }
    var NVLSN by rememberSaveable { mutableStateOf("") }
    var NVLCN by rememberSaveable { mutableStateOf("") }
    var NVRCAN by rememberSaveable { mutableStateOf("") }
    var NVLCAN by rememberSaveable { mutableStateOf("") }


    var IPD by rememberSaveable { mutableStateOf("") }

    // Function to show DatePicker for Birth Date
    val showBirthDatePicker = {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                dateOfBirth = LocalDate.of(year, month + 1, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Function to show DatePicker for Visit Date
    val showVisitDatePicker = {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                dateOfVisit = LocalDate.of(year, month + 1, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars).padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                contentDescription = "Arrow for back",
                tint = Color.Black,
                modifier = Modifier
                    .size(30.dp)
                    .clickable { navController.navigate("mode") }
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(painter = painterResource(R.drawable.kmc), contentDescription = "KMC logo")
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(5.dp)
        ) {
            item() {

                OutlinedTextField(
                    value = docName,
                    onValueChange = { docName = it },
                    label = { Text("Doctor Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { showVisitDatePicker() }) {
                        Text(text = "Select Visit Date")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Selected Date: ${dateOfVisit.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                        style = TextStyle(fontSize = 16.sp)
                    )
                }
            }
            item {

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Patient Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )
            }
            item {

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Patient Phone Number") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )
            }

            item {

                OutlinedTextField(
                    value = aadhar,
                    onValueChange = { aadhar = it },
                    label = { Text("Patient Aadhar Number") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )
            }

            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { showBirthDatePicker() }) {
                        Text(text = "Select Birth Date")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Selected Date: ${dateOfBirth?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "Not Selected"}",
                        style = TextStyle(fontSize = 16.sp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth().height(2.dp),
                    color = Color.Black
                )
            }



            item {
                Spacer(modifier = Modifier.height(15.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().border(width = 2.dp, color = Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Without Glasses Observation",
                        fontSize = 25.sp
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))

                Card(elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)) {
                    Column(modifier = Modifier.padding(start = 10.dp)) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Absolute.SpaceEvenly
                        ) {
                            Text(text = "Parameters", style = TextStyle(fontSize = 20.sp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Text(text = "Right Eye", style = TextStyle(fontSize = 20.sp))
                                Text(text = "Left Eye", style = TextStyle(fontSize = 20.sp))
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Distance Vision", style = TextStyle(fontSize = 20.sp))
                                Text(text = "Sphere", style = TextStyle(fontSize = 20.sp))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {

                                OutlinedTextField(
                                    value = DVRS,
                                    onValueChange = { DVRS = it },
                                    label = { Text("") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(100.dp)
                                )
                                OutlinedTextField(
                                    value = DVLS,
                                    onValueChange = { DVLS = it },
                                    label = { Text("") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(100.dp)
                                )
                            }

                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Distance Vision",
                                    style = TextStyle(fontSize = 20.sp)
                                )
                                Text(text = "Cylindrical", style = TextStyle(fontSize = 20.sp))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                OutlinedTextField(
                                    value = DVRC,
                                    onValueChange = { DVRC = it },
                                    label = { Text("") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(100.dp)
                                )
                                OutlinedTextField(
                                    value = DVLC,
                                    onValueChange = { DVLC = it },
                                    label = { Text("") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(100.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Distance Vision",
                                    style = TextStyle(fontSize = 20.sp)
                                )
                                Text(text = "Cylindrical Axis", style = TextStyle(fontSize = 20.sp))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                OutlinedTextField(
                                    value = DVRCA,
                                    onValueChange = { DVRCA = it },
                                    label = { Text("") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(100.dp)
                                )
                                OutlinedTextField(
                                    value = DVLCA,
                                    onValueChange = { DVLCA = it },
                                    label = { Text("") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(100.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row {

                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth().height(1.dp),
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    //Near for Without Glasses
                    Column(modifier = Modifier.padding(start = 10.dp)) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Near Vision", style = TextStyle(fontSize = 20.sp))
                                Text(text = "Sphere", style = TextStyle(fontSize = 20.sp))
                            }
                            Spacer(modifier = Modifier.width(35.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {

                                OutlinedTextField(
                                    value = NVRS,
                                    onValueChange = { NVRS = it },
                                    label = { Text("") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(100.dp)
                                )
                                OutlinedTextField(
                                    value = NVLS,
                                    onValueChange = { NVLS = it },
                                    label = { Text("") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(100.dp)
                                )
                            }

                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Near Vision",
                                    style = TextStyle(fontSize = 20.sp)
                                )
                                Text(text = "Cylindrical", style = TextStyle(fontSize = 20.sp))
                            }
                            Spacer(modifier = Modifier.width(35.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                OutlinedTextField(
                                    value = NVRC,
                                    onValueChange = { NVRC = it },
                                    label = { Text("") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(100.dp)
                                )
                                OutlinedTextField(
                                    value = NVLC,
                                    onValueChange = { NVLC = it },
                                    label = { Text("") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(100.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Near Vision",
                                    style = TextStyle(fontSize = 20.sp)
                                )
                                Text(text = "Cylindrical Axis", style = TextStyle(fontSize = 20.sp))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                OutlinedTextField(
                                    value = NVRCA,
                                    onValueChange = { NVRCA = it },
                                    label = { Text("") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(100.dp)
                                )
                                OutlinedTextField(
                                    value = NVLCA,
                                    onValueChange = { NVLCA = it },
                                    label = { Text("") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(100.dp)
                                )
                            }
                        }
                    }
                }
            }



            item {
                Spacer(modifier = Modifier.height(20.dp))
            }


            item {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(width = 2.dp, color = Color.Gray), shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.padding(10.dp).fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { isChecked = it }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "With Glasses Observation", style = TextStyle(fontSize = 20.sp))
                    }

                }

            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
            // Conditionally show the form fields when the checkbox is checked
            if (isChecked) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .border(width = 2.dp, color = Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "With Glasses Observation",
                            fontSize = 30.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))

                    Card(elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)) {
                        Column(modifier = Modifier.padding(start = 10.dp)) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Absolute.SpaceEvenly
                            ) {
                                Text(text = "Parameters", style = TextStyle(fontSize = 20.sp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Text(text = "Right Eye", style = TextStyle(fontSize = 20.sp))
                                    Text(text = "Left Eye", style = TextStyle(fontSize = 20.sp))
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Distance Vision",
                                        style = TextStyle(fontSize = 20.sp)
                                    )
                                    Text(text = "Sphere", style = TextStyle(fontSize = 20.sp))
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {

                                    OutlinedTextField(
                                        value = DVRSW,
                                        onValueChange = { DVRSW = it },
                                        label = { Text("") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.width(100.dp)
                                    )
                                    OutlinedTextField(
                                        value = DVLSW,
                                        onValueChange = { DVLSW = it },
                                        label = { Text("") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.width(100.dp)
                                    )
                                }

                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Distance Vision",
                                        style = TextStyle(fontSize = 20.sp)
                                    )
                                    Text(text = "Cylindrical", style = TextStyle(fontSize = 20.sp))
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    OutlinedTextField(
                                        value = DVRCW,
                                        onValueChange = { DVRCW = it },
                                        label = { Text("") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.width(100.dp)
                                    )
                                    OutlinedTextField(
                                        value = DVLCW,
                                        onValueChange = { DVLCW = it },
                                        label = { Text("") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.width(100.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Distance Vision",
                                        style = TextStyle(fontSize = 20.sp)
                                    )
                                    Text(
                                        text = "Cylindrical Axis",
                                        style = TextStyle(fontSize = 20.sp)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    OutlinedTextField(
                                        value = DVRCAW,
                                        onValueChange = { DVRCAW = it },
                                        label = { Text("") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.width(100.dp)
                                    )
                                    OutlinedTextField(
                                        value = DVLCAW,
                                        onValueChange = { DVLCAW = it },
                                        label = { Text("") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.width(100.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row {

                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth().height(1.dp),
                                color = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        //Near for Without Glasses
                        Column(modifier = Modifier.padding(start = 10.dp)) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "Near Vision", style = TextStyle(fontSize = 20.sp))
                                    Text(text = "Sphere", style = TextStyle(fontSize = 20.sp))
                                }
                                Spacer(modifier = Modifier.width(35.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {

                                    OutlinedTextField(
                                        value = NVRSW,
                                        onValueChange = { NVRSW = it },
                                        label = { Text("") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.width(100.dp)
                                    )
                                    OutlinedTextField(
                                        value = NVLSW,
                                        onValueChange = { NVLSW = it },
                                        label = { Text("") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.width(100.dp)
                                    )
                                }

                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Near Vision",
                                        style = TextStyle(fontSize = 20.sp)
                                    )
                                    Text(text = "Cylindrical", style = TextStyle(fontSize = 20.sp))
                                }
                                Spacer(modifier = Modifier.width(35.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    OutlinedTextField(
                                        value = NVRCW,
                                        onValueChange = { NVRCW = it },
                                        label = { Text("") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.width(100.dp)
                                    )
                                    OutlinedTextField(
                                        value = NVLCW,
                                        onValueChange = { NVLCW = it },
                                        label = { Text("") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.width(100.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Near Vision",
                                        style = TextStyle(fontSize = 20.sp)
                                    )
                                    Text(
                                        text = "Cylindrical Axis",
                                        style = TextStyle(fontSize = 20.sp)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    OutlinedTextField(
                                        value = NVRCAW,
                                        onValueChange = { NVRCAW = it },
                                        label = { Text("") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.width(100.dp)
                                    )
                                    OutlinedTextField(
                                        value = NVLCAW,
                                        onValueChange = { NVLCAW = it },
                                        label = { Text("") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.width(100.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                Box(
                    modifier = Modifier.fillMaxWidth().border(width = 2.dp, color = Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "New Glasses Observation",
                        fontSize = 30.sp
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))

                Card(elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)) {
                    Column(modifier = Modifier.padding(start = 10.dp)) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Absolute.SpaceEvenly
                        ) {
                            Text(text = "Parameters", style = TextStyle(fontSize = 20.sp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Text(text = "Right Eye", style = TextStyle(fontSize = 20.sp))
                                Text(text = "Left Eye", style = TextStyle(fontSize = 20.sp))
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Distance Vision", style = TextStyle(fontSize = 20.sp))
                                Text(text = "Sphere", style = TextStyle(fontSize = 20.sp))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {

                                OutlinedTextField(
                                    value = DVRSN,
                                    onValueChange = { DVRSN = it },
                                    label = { Text("") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(100.dp)
                                )
                                OutlinedTextField(
                                    value = DVLSN,
                                    onValueChange = { DVLSN = it },
                                    label = { Text("") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(100.dp)
                                )
                            }

                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Distance Vision",
                                    style = TextStyle(fontSize = 20.sp)
                                )
                                Text(text = "Cylindrical", style = TextStyle(fontSize = 20.sp))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                OutlinedTextField(
                                    value = DVRCN,
                                    onValueChange = { DVRCN = it },
                                    label = { Text("") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(100.dp)
                                )
                                OutlinedTextField(
                                    value = DVLCN,
                                    onValueChange = { DVLCN = it },
                                    label = { Text("") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(100.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Distance Vision",
                                    style = TextStyle(fontSize = 20.sp)
                                )
                                Text(text = "Cylindrical Axis", style = TextStyle(fontSize = 20.sp))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                OutlinedTextField(
                                    value = DVRCAN,
                                    onValueChange = { DVRCAN = it },
                                    label = { Text("") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(100.dp)
                                )
                                OutlinedTextField(
                                    value = DVLCAN,
                                    onValueChange = { DVLCAN = it },
                                    label = { Text("") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(100.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row {

                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth().height(1.dp),
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    //Near for Without Glasses
                    Column(modifier = Modifier.padding(start = 10.dp)) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Near Vision", style = TextStyle(fontSize = 20.sp))
                                Text(text = "Sphere", style = TextStyle(fontSize = 20.sp))
                            }
                            Spacer(modifier = Modifier.width(35.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {

                                OutlinedTextField(
                                    value = NVRSN,
                                    onValueChange = { NVRSN = it },
                                    label = { Text("") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(100.dp)
                                )
                                OutlinedTextField(
                                    value = NVLSN,
                                    onValueChange = { NVLSN = it },
                                    label = { Text("") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(100.dp)
                                )
                            }

                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Near Vision",
                                    style = TextStyle(fontSize = 20.sp)
                                )
                                Text(text = "Cylindrical", style = TextStyle(fontSize = 20.sp))
                            }
                            Spacer(modifier = Modifier.width(35.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                OutlinedTextField(
                                    value = NVRCN,
                                    onValueChange = { NVRCN = it },
                                    label = { Text("") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(100.dp)
                                )
                                OutlinedTextField(
                                    value = NVLCN,
                                    onValueChange = { NVLCN = it },
                                    label = { Text("") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(100.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Near Vision",
                                    style = TextStyle(fontSize = 20.sp)
                                )
                                Text(text = "Cylindrical Axis", style = TextStyle(fontSize = 20.sp))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                OutlinedTextField(
                                    value = NVRCAN,
                                    onValueChange = { NVRCAN = it },
                                    label = { Text("") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(100.dp)
                                )
                                OutlinedTextField(
                                    value = NVLCAN,
                                    onValueChange = { NVLCAN = it },
                                    label = { Text("") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(100.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Interpupillary",
                                    style = TextStyle(fontSize = 20.sp)
                                )
                                Text(text = "distance", style = TextStyle(fontSize = 20.sp))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                OutlinedTextField(
                                    value = IPD,
                                    onValueChange = { IPD = it },
                                    label = { Text("") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(100.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ElevatedButton(
                        onClick = {
                            // Create patient data
                            val patientData = PatientData(
                                name = name,
                                docName = docName,
                                phone = phone,
                                aadhar = aadhar,
                                dateOfBirth = dateOfBirth,
                                dateOfVisit = dateOfVisit,
                                withoutGlasses = mapOf(
                                    "Distance Vision Sphere Right" to DVRS,
                                    "Distance Vision Cylinder Right" to DVRC,
                                    "Distance Vision Sphere Left" to DVLS,
                                    "Distance Vision Cylinder Left" to DVLC,
                                    "Distance Vision Cylinder Axis Right" to DVRCA,
                                    "Distance Vision Cylinder Axis Left" to DVLCA,
                                    "Near Vision Sphere Right" to NVRS,
                                    "Near Vision Cylinder Right" to NVRC,
                                    "Near Vision Sphere Left" to NVLS,
                                    "Near Vision Cylinder Left" to NVLC,
                                    "Near Vision Cylinder Axis Right" to NVRCA,
                                    "Near Vision Cylinder Axis Left" to NVLCA
                                ),
                                withGlasses = mapOf(
                                    "Distance Vision Sphere Right" to DVRSW,
                                    "Distance Vision Cylinder Right" to DVRCW,
                                    "Distance Vision Sphere Left" to DVLSW,
                                    "Distance Vision Cylinder Left" to DVLCW,
                                    "Distance Vision Cylinder Axis Right" to DVRCAW,
                                    "Distance Vision Cylinder Axis Left" to DVLCAW,
                                    "Near Vision Sphere Right" to NVRSW,
                                    "Near Vision Cylinder Right" to NVRCW,
                                    "Near Vision Sphere Left" to NVLSW,
                                    "Near Vision Cylinder Left" to NVLCW,
                                    "Near Vision Cylinder Axis Right" to NVRCAW,
                                    "Near Vision Cylinder Axis Left" to NVLCAW
                                ),
                                newGlassPrescription = mapOf(
                                    "Distance Vision Sphere Right" to DVRSN,
                                    "Distance Vision Cylinder Right" to DVRCN,
                                    "Distance Vision Sphere Left" to DVLSN,
                                    "Distance Vision Cylinder Left" to DVLCN,
                                    "Distance Vision Cylinder Axis Right" to DVRCAN,
                                    "Distance Vision Cylinder Axis Left" to DVLCAN,
                                    "Near Vision Sphere Right" to NVRSN,
                                    "Near Vision Cylinder Right" to NVRCN,
                                    "Near Vision Sphere Left" to NVLSN,
                                    "Near Vision Cylinder Left" to NVLCN,
                                    "Near Vision Cylinder Axis Right" to NVRCAN,
                                    "Near Vision Cylinder Axis Left" to NVLCAN,
                                    "Interpupillary distance" to IPD
                                )
                            )

                            exportToPDF(patientData, context ) // Call to export data
                            showDialog = true // Show the dialog
                        },
                        modifier = Modifier.width(250.dp)
                    ) {
                        Text("Save", style = TextStyle(fontSize = 20.sp))
                    }

                    // Show the dialog when showDialog is true
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = { Text(text = "Patient Details") },
                            text = {
                                Column {
                                    Text("Doctor: $docName")
                                    Text("Name: $name")
                                    Text("Phone: $phone")
                                    Text("Aadhar: $aadhar")
                                    Text("Date of Birth: ${dateOfBirth?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "Not Selected"}")
                                    Text("Date of Visit: ${dateOfVisit.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}")

                                    // Display without glasses data
                                    Text("Without Glasses Observations:")
                                    // Iterate through the without glasses data
                                    val withoutGlasses = mapOf(
                                        "Distance Vision Sphere Right" to DVRS,
                                        "Distance Vision Cylinder Right" to DVRC,
                                        "Distance Vision Sphere Left" to DVLS,
                                        "Distance Vision Cylinder Left" to DVLC,
                                        "Distance Vision Cylinder Axis Right" to DVRCA,
                                        "Distance Vision Cylinder Axis Left" to DVLCA,
                                        "Near Vision Sphere Right" to NVRS,
                                        "Near Vision Cylinder Right" to NVRC,
                                        "Near Vision Sphere Left" to NVLS,
                                        "Near Vision Cylinder Left" to NVLC,
                                        "Near Vision Cylinder Axis Right" to NVRCA,
                                        "Near Vision Cylinder Axis Left" to NVLCA
                                    )
                                    withoutGlasses.forEach { (key, value) ->
                                        Text("$key: $value")
                                    }
                                    HorizontalDivider(modifier = Modifier.fillMaxWidth().height(1.dp))

                                    val withGlasses = mapOf(
                                        "Distance Vision Sphere Right" to DVRSW,
                                        "Distance Vision Cylinder Right" to DVRCW,
                                        "Distance Vision Sphere Left" to DVLSW,
                                        "Distance Vision Cylinder Left" to DVLCW,
                                        "Distance Vision Cylinder Axis Right" to DVRCAW,
                                        "Distance Vision Cylinder Axis Left" to DVLCAW,
                                        "Near Vision Sphere Right" to NVRSW,
                                        "Near Vision Cylinder Right" to NVRCW,
                                        "Near Vision Sphere Left" to NVLSW,
                                        "Near Vision Cylinder Left" to NVLCW,
                                        "Near Vision Cylinder Axis Right" to NVRCAW,
                                        "Near Vision Cylinder Axis Left" to NVLCAW
                                    )
                                    withGlasses.forEach { (key, value) ->
                                        Text("$key: $value")
                                    }

                                    HorizontalDivider(modifier = Modifier.fillMaxWidth().height(1.dp))

                                    val newGlassPrescription = mapOf(
                                        "Distance Vision Sphere Right" to DVRSN,
                                        "Distance Vision Cylinder Right" to DVRCN,
                                        "Distance Vision Sphere Left" to DVLSN,
                                        "Distance Vision Cylinder Left" to DVLCN,
                                        "Distance Vision Cylinder Axis Right" to DVRCAN,
                                        "Distance Vision Cylinder Axis Left" to DVLCAN,
                                        "Near Vision Sphere Right" to NVRSN,
                                        "Near Vision Cylinder Right" to NVRCN,
                                        "Near Vision Sphere Left" to NVLSN,
                                        "Near Vision Cylinder Left" to NVLCN,
                                        "Near Vision Cylinder Axis Right" to NVRCAN,
                                        "Near Vision Cylinder Axis Left" to NVLCAN,
                                        "Interpupillary distance" to IPD
                                    )
                                    newGlassPrescription.forEach { (key, value) ->
                                        Text("$key: $value")
                                    }
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = { showDialog = false }) {
                                    Text("OK")
                                }
                            }
                        )
                    }
                }


            }
        }
    }
}

data class PatientData(
    val name: String,
    val docName: String,
    val phone: String,
    val aadhar: String,
    val dateOfBirth: LocalDate?,
    val dateOfVisit: LocalDate,
    val withoutGlasses: Map<String, String>,
    val withGlasses: Map<String, String>,          // New field for "With Glass" observations
    val newGlassPrescription: Map<String, String>  // New field for "New Glass Prescription" observations
)

fun exportToPDF(data: PatientData, context: Context) {
    val pdfDocument = PdfDocument()
    val pageWidth = 595
    val pageHeight = 900
    val margin = 30f // Margin for top and bottom to allow for content space
    val availableHeight = pageHeight - margin * 2 // Available space for content

    var currentPageNumber = 1 // Manually track the page number
    var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
    var page = pdfDocument.startPage(pageInfo)

    var canvas = page.canvas

    // Paint initialization
    val paint = Paint().apply {
        color = android.graphics.Color.BLACK
        textSize = 12f
        style = Paint.Style.FILL
        isAntiAlias = true // Enable anti-aliasing
    }

    val linePaint = Paint().apply {
        color = android.graphics.Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 2f
        isAntiAlias = true // Enable anti-aliasing
    }

    val headingPaint = Paint().apply {
        color = android.graphics.Color.BLACK
        textSize = 16f
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        isAntiAlias = true // Enable anti-aliasing
    }

    // Draw logo in the center
    val logoWidth = 200 // desired width in pixels
    val logoHeight = 100 // desired height in pixels
    val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.kmc) // Ensure this drawable exists
    val scaledLogoBitmap = Bitmap.createScaledBitmap(logoBitmap, logoWidth, logoHeight, false)
    canvas.drawBitmap(scaledLogoBitmap, (pageWidth - logoWidth) / 2f, 20f, paint)
    // Do not recycle the bitmap here yet

    var yPos = margin + 80f // Starting position for content

    // Function to check the page limit and create a new page if needed
    fun checkPageLimitAndCreateNewPage() {
        if (yPos + 30f > availableHeight) { // Ensure Float comparison
            pdfDocument.finishPage(page) // Finish the current page
            currentPageNumber++ // Increment page number
            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create() // Create a new page
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas // Update canvas for the new page
            canvas.drawBitmap(scaledLogoBitmap, (pageWidth - logoWidth) / 2f, 20f, paint) // Redraw logo on the new page
            yPos = margin + 80f // Reset yPos for new page
        }
    }

    // Centered Patient Details heading
    canvas.drawText("Patient Details", pageWidth / 2f, yPos, headingPaint)
    yPos += 30f

    // Patient Details section (no box around this section)
    canvas.drawText("Doctor: ${data.docName}", 10f, yPos, paint)
    yPos += 0f
    canvas.drawText("Name: ${data.name}", pageWidth / 3f, yPos, paint)
    yPos += 20f
    canvas.drawText("Phone: ${data.phone}", 10f, yPos, paint)
    yPos += 0f
    canvas.drawText("Aadhar: ${data.aadhar}", pageWidth / 3f, yPos, paint)
    yPos += 20f
    canvas.drawText(
        "Date of Birth: ${data.dateOfBirth?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "Not Selected"}",
        10f,
        yPos,
        paint
    )
    yPos += 0f
    canvas.drawText(
        "Date of Visit: ${data.dateOfVisit.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
        pageWidth / 3f,
        yPos,
        paint
    )

    // Without Glasses Section
    yPos += 40f
    checkPageLimitAndCreateNewPage() // Check if we need a new page
    canvas.drawText("Without Glasses Observations", pageWidth / 2f, yPos, headingPaint)
    yPos += 20f

    // Draw table for Without Glasses
    yPos = drawObservationTable(canvas, data.withoutGlasses, yPos, pageWidth, pageHeight, linePaint, paint)

    // With Glasses Section
    yPos += 20f // Add space between sections
    checkPageLimitAndCreateNewPage() // Check if we need a new page
    canvas.drawText("With Glasses Observations", pageWidth / 2f, yPos, headingPaint)
    yPos += 20f

    // Draw table for With Glasses
    yPos = drawObservationTable(canvas, data.withGlasses, yPos, pageWidth, pageHeight, linePaint, paint)

    // New Glass Prescription Section
    yPos += 20f // Add space between sections
    checkPageLimitAndCreateNewPage() // Check if we need a new page
    canvas.drawText("New Glass Prescription", pageWidth / 2f, yPos, headingPaint)
    yPos += 20f

    // Draw table for New Glass Prescription
    yPos = drawObservationTable(canvas, data.newGlassPrescription, yPos, pageWidth, pageHeight, linePaint, paint)

    // Finish the last page
    pdfDocument.finishPage(page)

    // Save the PDF to a file
    val filePath = context.getExternalFilesDir(null)?.absolutePath + "/${data.name} consulted by ${data.docName}.pdf"
    val file = File(filePath)
    try {
        pdfDocument.writeTo(FileOutputStream(file))
        Toast.makeText(context, "PDF exported to ${file.absolutePath}", Toast.LENGTH_LONG).show()
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to export PDF", Toast.LENGTH_LONG).show()
    }

    // Close and recycle resources
    pdfDocument.close()
    scaledLogoBitmap.recycle() // Recycle the bitmap
}






// Main function to draw the observation table on a canvas
private fun drawObservationTable(
    canvas: Canvas,
    observations: Map<String, String>,
    startY: Float,
    pageWidth: Int,
    pageHeight: Int, // Added pageHeight parameter
    linePaint: Paint,
    textPaint: Paint // Add textPaint parameter here
): Float {
    val cellHeight = 20f // Height of each cell
    var yPos = startY
    val padding = 10f // Padding for the text inside the cells

    // Define the width for each column
    val columnWidth = pageWidth / 3.1f // Equal width for each column
    val column1X = 10f  // X position for the first column (parameters)
    val column2X = column1X + columnWidth // X position for the second column (right eye)
    val column3X = column2X + columnWidth // X position for the third column (left eye)

    // Draw top horizontal line
    canvas.drawLine(column1X, yPos, column3X + columnWidth, yPos, linePaint)

    // Draw table headers
    yPos += cellHeight
    drawTextCentered(canvas, "Parameter", column1X, yPos, cellHeight, columnWidth, textPaint)
    drawTextCentered(canvas, "Right Eye", column2X, yPos, cellHeight, columnWidth, textPaint)
    drawTextCentered(canvas, "Left Eye", column3X, yPos, cellHeight, columnWidth, textPaint)

    // Draw horizontal line for header
    yPos += cellHeight
    canvas.drawLine(column1X, yPos, column3X + columnWidth, yPos, linePaint)

    // List of parameters for both eyes
    val parameters = listOf(
        "Distance Vision Sphere",
        "Distance Vision Cylinder",
        "Distance Vision Cylinder Axis",
        "Near Vision Sphere",
        "Near Vision Cylinder",
        "Near Vision Cylinder Axis"
    )

    // Loop through parameters and draw table rows
    parameters.forEach { parameter ->
        // Check if yPos exceeds the page height
        if (yPos + cellHeight > pageHeight) {
            // Handle page overflow here (if this were a PDF, create new page)
            // This is just for illustration
            yPos = startY + cellHeight // Reset yPos to the start for the new page
        }

        val rightEyeKey = "$parameter Right"
        val leftEyeKey = "$parameter Left"

        val rightEyeData = observations[rightEyeKey] ?: ""
        val leftEyeData = observations[leftEyeKey] ?: ""

        // Draw parameter and values
        drawTextCentered(canvas, parameter, column1X, yPos, cellHeight, columnWidth, textPaint)
        drawTextCentered(canvas, rightEyeData, column2X, yPos, cellHeight, columnWidth, textPaint)
        drawTextCentered(canvas, leftEyeData, column3X, yPos, cellHeight, columnWidth, textPaint)

        // Draw horizontal lines for each row
        yPos += cellHeight
        canvas.drawLine(column1X, yPos, column3X + columnWidth, yPos, linePaint)
    }

    // Draw vertical lines for the table
    canvas.drawLine(column1X, startY, column1X, yPos, linePaint)
    canvas.drawLine(column2X, startY, column2X, yPos, linePaint)
    canvas.drawLine(column3X, startY, column3X, yPos, linePaint)
    canvas.drawLine(column3X + columnWidth, startY, column3X + columnWidth, yPos, linePaint)

    // Return updated yPos for next content, adding some space after the table
    return yPos + 20f // Add space after the table
}

// Helper function to draw centered text in a table cell
private fun drawTextCentered(
    canvas: Canvas,
    text: String,
    x: Float,
    y: Float,
    cellHeight: Float,
    columnWidth: Float,
    paint: Paint
) {
    // Calculate the baseline for vertical centering
    val bounds = Rect()
    paint.getTextBounds(text, 0, text.length, bounds)
    val textHeight = bounds.height()
    val textWidth = paint.measureText(text)
    val textX = x + (columnWidth - textWidth) / 2
    val textY = y + (cellHeight + textHeight) / 2 - 4 // Adjusting for vertical centering
    canvas.drawText(text, textX, textY, paint)
}





// Function to get data for both eyes
private fun getEyeData(key: String, value: String): Pair<String, String> {
    return when (key) {
        "Distance Vision Right Sphere", "Distance Vision Right Cylinder", "Distance Vision Right Cylinder Axis", "Near Vision Right Sphere", "Near Vision Right Cylinder", "Near Vision Right Cylinder Axis" -> Pair(
            value,
            ""
        ) // Right Eye data
        "Distance Vision Left Sphere", "Distance Vision Left Cylinder", "Distance Vision Left Cylinder Axis", "Near Vision Left Sphere", "Near Vision Left Cylinder", "Near Vision Left Cylinder Axis" -> Pair(
            "",
            value
        ) // Left Eye data
        "Interpupillary distance" -> Pair(value, value)
        else -> Pair("", "") // Default case if key doesn't match
    }
}