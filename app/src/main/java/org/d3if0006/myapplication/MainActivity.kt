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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import android.app.DatePickerDialog
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

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
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var scheduleList = remember { mutableStateListOf<String>() }
    var editingIndex by remember { mutableStateOf<Int?>(null) }
    var showActionDialog by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        Text(
            text = "Masukkan Jadwal Bimbingan",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showDatePicker = true },
            modifier = Modifier.padding(8.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(text = "Set Tanggal & Waktu", color = Color.White)
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (scheduleList.isNotEmpty()) {
            Text(
                text = "Jadwal Terpilih:",
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Start)
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(scheduleList.size) { index ->
                TimeCard(
                    time = scheduleList[index],
                    onEdit = { showActionDialog = index }
                )
            }
        }
    }

    if (showActionDialog != null) {
        AlertDialog(
            onDismissRequest = { showActionDialog = null },
            title = { Text("Pilih Aksi") },
            text = {
                Column {
                    TextButton(onClick = {
                        editingIndex = showActionDialog
                        val parts = scheduleList[showActionDialog!!].split(" ")
                        val day = parts[1].toInt() // Tanggal
                        val timeParts = parts[2].split(":")
                        val hour = timeParts[0].toInt()
                        val minute = timeParts[1].toInt()

                        selectedDate = LocalDate.of(LocalDate.now().year, LocalDate.now().month, day)
                        selectedTime = LocalTime.of(hour, minute)
                        showDatePicker = true
                        showActionDialog = null
                    }) {
                        Text("Ubah Tanggal & Waktu")
                    }
                    TextButton(onClick = {
                        scheduleList.removeAt(showActionDialog!!)
                        showActionDialog = null
                    }) {
                        Text("Hapus Tanggal & Waktu")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                Button(onClick = { showActionDialog = null }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showDatePicker) {
        val current = Calendar.getInstance()
        val minDate = current.timeInMillis
        val datePickerDialog = DatePickerDialog(
            LocalContext.current,
            { _, year, month, dayOfMonth ->
                val selected = LocalDate.of(year, month + 1, dayOfMonth)
                if (!selected.isBefore(LocalDate.now())) {
                    selectedDate = selected
                    showDatePicker = false
                    showTimePicker = true
                }
            },
            selectedDate?.year ?: current.get(Calendar.YEAR),
            selectedDate?.monthValue?.minus(1) ?: current.get(Calendar.MONTH),
            selectedDate?.dayOfMonth ?: current.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = minDate
        datePickerDialog.show()
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            onTimeSelected = { time ->
                if (time.hour in 8..16) { // Membatasi jam dari 08:00 sampai 17:00
                    selectedTime = time
                    if (selectedDate != null && selectedTime != null) {
                        val dateTime = LocalDateTime.of(selectedDate, selectedTime)
                        val formatted = dateTime.format(DateTimeFormatter.ofPattern("EEEE, dd HH:mm"))
                        if (editingIndex != null) {
                            scheduleList[editingIndex!!] = formatted
                        } else {
                            scheduleList.add(formatted)
                        }
                        editingIndex = null
                    }
                }
                showTimePicker = false
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
            .clickable(onClick = onEdit),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(text = time, fontSize = 16.sp, color = Color.Black)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onTimeSelected: (LocalTime) -> Unit
) {
    var hour by remember { mutableStateOf(8) } // Default jam 08:00
    var minute by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Pilih Waktu") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row {
                    Text("Jam: ")
                    NumberPicker(value = hour, onValueChange = { hour = it }, range = 8..16) // Membatasi jam dari 08:00 sampai 17:00
                }
                Row {
                    Text("Menit: ")
                    NumberPicker(value = minute, onValueChange = { minute = it }, range = 0..59)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onTimeSelected(LocalTime.of(hour, minute))
                onDismissRequest()
            }) {
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
    Row(verticalAlignment = Alignment.CenterVertically) {
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
