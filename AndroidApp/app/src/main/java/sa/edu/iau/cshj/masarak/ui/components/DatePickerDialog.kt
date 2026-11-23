package sa.edu.iau.cshj.masarak.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import sa.edu.iau.cshj.masarak.ui.screens.tasks.StudentTaskForm
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import java.time.LocalDate
import java.time.YearMonth
import androidx.compose.foundation.clickable
private val DateNavy = Color(0xFF0C0D45)

@Composable

fun DatePickerDialog(
    onDismiss: () -> Unit = {},
    onDateSelected: (Long) -> Unit = {}
) {
    Dialog(onDismissRequest = onDismiss) {
        DatePickerLayout(
            onDismiss = onDismiss,
            onDateSelected = onDateSelected
        )    }
}


@Composable

private fun DatePickerLayout(
    onDismiss: () -> Unit = {},
    onDateSelected: (Long) -> Unit = {}
) {


    val today = LocalDate.now()

    var selectedDay by rememberSaveable {
        mutableIntStateOf(today.dayOfMonth)
    }

    var displayedMonth by rememberSaveable {
        mutableIntStateOf(today.monthValue)
    }

    var displayedYear by rememberSaveable {
        mutableIntStateOf(today.year)
    }
    val currentMonth = YearMonth.of(displayedYear, displayedMonth)

    val safeSelectedDay = selectedDay.coerceIn(
        1,
        currentMonth.lengthOfMonth()
    )

    Surface(
        modifier = Modifier.widthIn(max = 340.dp).fillMaxWidth(),
        shape = RoundedCornerShape(22.dp), color = Color.White
    ) {
        Column(Modifier.padding(18.dp)) {
            Text("Select date", color = Color(0xFF55555F), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(16.dp))
            Text("Mon, 14 Sep", color = DateNavy, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFE3E3E8))
            Row(Modifier.fillMaxWidth().height(44.dp), verticalAlignment = Alignment.CenterVertically) {

                IconButton(
                    onClick = {
                        val previousMonth =
                            YearMonth.of(displayedYear, displayedMonth)
                                .minusMonths(1)

                        displayedMonth = previousMonth.monthValue
                        displayedYear = previousMonth.year
                    }
                ) {
                    Icon(
                        Icons.Outlined.ChevronLeft,
                        contentDescription = "Previous month",
                        tint = DateNavy
                    )
                }
                Text(
                    "${
                        YearMonth.of(displayedYear, displayedMonth).month.name.lowercase()
                            .replaceFirstChar { it.uppercase() }
                    } $displayedYear", Modifier.weight(1f).padding(horizontal = 10.dp),
                    color = DateNavy, fontWeight = FontWeight.SemiBold
                )

                IconButton(
                    onClick = {
                        val nextMonth =
                            YearMonth.of(displayedYear, displayedMonth)
                                .plusMonths(1)

                        if (!nextMonth.atDay(1).isAfter(today)) {
                            displayedMonth = nextMonth.monthValue
                            displayedYear = nextMonth.year
                        }
                    }
                ) {
                    Icon(
                        Icons.Outlined.ChevronRight,
                        contentDescription = "Next month",
                        tint = DateNavy
                    )
                }
            }
            Row(Modifier.fillMaxWidth()) {
                listOf("S", "M", "T", "W", "T", "F", "S").forEach {
                    Box(Modifier.weight(1f).height(30.dp), contentAlignment = Alignment.Center) {
                        Text(it, color = Color(0xFF636575), fontSize = 12.sp)
                    }
                }
            }

            val month = YearMonth.of(displayedYear, displayedMonth)

            val firstDayOffset =
                month.atDay(1).dayOfWeek.value % 7

            val days =
                List(firstDayOffset) { "" } +
                        (1..month.lengthOfMonth()).map { it.toString() }

            val paddedDays =
                days + List((7 - days.size % 7) % 7) { "" }
            paddedDays.chunked(7).forEach { week ->                Row(Modifier.fillMaxWidth()) {
                    week.forEach { day ->
                        Box(Modifier.weight(1f).height(36.dp), contentAlignment = Alignment.Center) {

                            val dayNumber = day.toIntOrNull()
                            val isSelected = dayNumber != null &&
                                    dayNumber == safeSelectedDay &&
                                    !month.atDay(dayNumber).isAfter(today)

                            val isEnabled = dayNumber != null &&
                                    !month.atDay(dayNumber ?: 1).isAfter(today)

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable(enabled = isEnabled) {
                                        if (dayNumber != null) {
                                            selectedDay = dayNumber
                                        }
                                    }
                                    .background(
                                        if (isSelected) DateNavy else Color.Transparent,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day,
                                    color = when {
                                        isSelected -> Color.White
                                        !isEnabled -> Color(0xFF9C9CA5)
                                        else -> DateNavy
                                    },
                                    fontSize = 13.sp
                                )
                            }

                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("Choose the day you completed the task. Future days aren’t available.", color = Color(0xFF55555F), fontSize = 12.sp, lineHeight = 16.sp)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Cancel", color = Color(0xFF00696E)) }

                TextButton(
                    onClick = {
                        val safeDay = selectedDay.coerceIn(
                            1,
                            YearMonth.of(displayedYear, displayedMonth).lengthOfMonth()
                        )

                        val chosenDate = LocalDate.of(
                            displayedYear,
                            displayedMonth,
                            safeDay
                        )

                        if (!chosenDate.isAfter(today)) {
                            onDateSelected(
                                chosenDate.atStartOfDay(
                                    java.time.ZoneId.systemDefault()
                                ).toInstant().toEpochMilli()
                            )
                            onDismiss()
                        }
                    }
                ) {
                    Text(
                        "OK",
                        color = Color(0xFF00696E),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(name = "Date Picker Dialog", widthDp = 390, heightDp = 844)
@Composable
private fun DatePickerPreview() {
    MaterialTheme {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(Modifier.fillMaxSize().background(DateNavy)) {
                DatePreviewStatusBar()
                Box(Modifier.weight(1f).fillMaxWidth()) {
                    StudentTaskForm(filled = false, dateFocused = true)
                }
                Box(Modifier.fillMaxWidth().height(17.dp), contentAlignment = Alignment.Center) {
                    Box(Modifier.width(100.dp).height(4.dp)
                        .background(Color.White.copy(alpha = .45f), CircleShape))
                }
            }
            Box(Modifier.matchParentSize().background(DateNavy.copy(alpha = .32f)))
            Box(Modifier.fillMaxWidth().padding(horizontal = 40.dp), contentAlignment = Alignment.Center) { DatePickerLayout() }
        }
    }
}

@Composable
private fun DatePreviewStatusBar() {
    Box(Modifier.fillMaxWidth().height(28.dp).background(Color(0xFFE2ECF9)).padding(horizontal = 20.dp)) {
        Text("9:41", modifier = Modifier.align(Alignment.CenterStart),
            color = DateNavy, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Canvas(Modifier.align(Alignment.CenterEnd).size(50.dp, 14.dp)) {
            val u = size.height / 14f
            repeat(4) { i ->
                val h = (3 + i * 2) * u
                drawRect(DateNavy, Offset(i * 3 * u, 12 * u - h), Size(2 * u, h))
            }
            listOf(12f, 8f, 4f).forEach { d ->
                drawArc(DateNavy, 225f, 90f, false,
                    topLeft = Offset((23f - d / 2) * u, (11f - d / 2) * u),
                    size = Size(d * u, d * u), style = Stroke(1.2f * u))
            }
            drawCircle(DateNavy, u, Offset(23 * u, 11 * u))
            drawRect(DateNavy, Offset(36 * u, 3 * u), Size(7 * u, 10 * u))
            drawRect(DateNavy, Offset(38 * u, 1.5f * u), Size(3 * u, 2 * u))
        }
    }
}
