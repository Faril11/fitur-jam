package org.d3if0006.myapplication

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TeacherScheduleScreen()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TeacherScheduleScreen() {
    // State untuk menyimpan waktu yang dipilih (dalam bentuk String)
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
    var showTimePicker by remember { mutableStateOf(false) }
    var timeList = remember { mutableStateListOf<String>() } // Menyimpan waktu sebagai String
    var editingTimeIndex by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Teling",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Red)
                .padding(vertical = 8.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Judul jadwal
        Text(
            text = "Masukkan Jadwal Bimbingan",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Kolom input untuk mengisi jadwal
        Button(
            onClick = { showTimePicker = true },
            modifier = Modifier.padding(start = 8.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(
                text = "Set Waktu",
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Menampilkan waktu yang dipilih
        if (timeList.isNotEmpty()) {
            Text(
                text = "Waktu Terpilih:",
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Start)
            )
        }

        // Menampilkan daftar waktu yang dipilih dalam grid (3 kolom)
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(timeList.size) { index ->
                TimeCard(
                    time = timeList[index],
                    onEdit = {
                        editingTimeIndex = index
                        showTimePicker = true
                    }
                )
            }
        }
    }

    // Dialog TimePicker
    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            onTimeSelected = { time ->
                // Update waktu yang ada jika sedang dalam mode edit
                if (editingTimeIndex != null) {
                    timeList[editingTimeIndex ?: 0] = time.format(DateTimeFormatter.ofPattern("HH:mm"))
                } else {
                    // Menambah waktu baru jika tidak dalam mode edit
                    timeList.add(time.format(DateTimeFormatter.ofPattern("HH:mm")))
                }
                showTimePicker = false
                editingTimeIndex = null
            }
        )
    }
}

@Composable
fun TimeCard(time: String, onEdit: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .height(50.dp)
            .clickable(onClick = onEdit), // Ubah menjadi clickable untuk mengedit waktu
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = time,
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onTimeSelected: (LocalTime) -> Unit
) {
    var hour by remember { mutableStateOf(0) }
    var minute by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text("Pilih Waktu")
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Text("Jam: ")
                    NumberPicker(
                        value = hour,
                        onValueChange = { hour = it },
                        range = 0..23
                    )
                }
                Row {
                    Text("Menit: ")
                    NumberPicker(
                        value = minute,
                        onValueChange = { minute = it },
                        range = 0..59
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Membuat LocalTime menggunakan hour dan minute
                    onTimeSelected(LocalTime.of(hour, minute))
                    onDismissRequest()
                }
            ) {
                Text("Set Waktu")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun NumberPicker(value: Int, onValueChange: (Int) -> Unit, range: IntRange) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = { if (value > range.first) onValueChange(value - 1) }) {
            Text("-")
        }
        Text(
            text = value.toString(),
            modifier = Modifier.padding(horizontal = 8.dp),
            fontSize = 24.sp
        )
        Button(onClick = { if (value < range.last) onValueChange(value + 1) }) {
            Text("+")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewTeacherScheduleScreen() {
    TeacherScheduleScreen()
}
