package sa.edu.iau.cshj.masarak.ui.screens.training

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import sa.edu.iau.cshj.masarak.ui.components.AppBottomBar
import sa.edu.iau.cshj.masarak.ui.components.BottomBarItem
import sa.edu.iau.cshj.masarak.ui.viewmodel.DEMO_STUDENT_EMAIL
import sa.edu.iau.cshj.masarak.ui.viewmodel.MasarakViewModelFactory
import sa.edu.iau.cshj.masarak.ui.viewmodel.MyTrainingUiState
import sa.edu.iau.cshj.masarak.ui.viewmodel.SupervisorUi
import sa.edu.iau.cshj.masarak.ui.viewmodel.TrainingViewModel
import sa.edu.iau.cshj.masarak.ui.viewmodel.TrainingWeekUi

internal val TrainingNavy = Color(0xFF0C0D45)
internal val TrainingInk = Color(0xFF191925)
internal val TrainingMuted = Color(0xFF55555D)
internal val TrainingLine = Color(0xFFDADAD7)
internal val TrainingTeal = Color(0xFF00696E)
internal val TrainingAqua = Color(0xFF75CBD1)
internal val TrainingError = Color(0xFFB3261E)
private val TrainingPaper = Color(0xFFF7F7F4)

@Composable
internal fun TrainingPage(
    selected: BottomBarItem,
    academic: Boolean = false,
    scrolled: Boolean = false,
    previewFrame: Boolean = false,
    onTabSelected: (BottomBarItem) -> Unit = {},
    header: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val background = if (academic) {
        Brush.verticalGradient(0f to TrainingPaper, .28f to TrainingPaper, 1f to Color(0xFFDCEAF3))
    } else {
        Brush.verticalGradient(0f to Color(0xFFDCE8F4), .24f to TrainingPaper, 1f to TrainingPaper)
    }
    Column(Modifier.fillMaxSize().background(background).then(
        if (previewFrame) Modifier else Modifier.statusBarsPadding()
    )) {
        if (previewFrame) TrainingPreviewStatusBar()
        header()
        Column(
            Modifier.weight(1f).fillMaxWidth()
                .verticalScroll(rememberScrollState(if (scrolled) Int.MAX_VALUE else 0))
                .padding(start = 18.dp, end = 18.dp, top = if (previewFrame) 5.dp else 12.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
        Column(
            Modifier.fillMaxWidth()
                .background(TrainingNavy)
                .navigationBarsPadding()
        ) {
            AppBottomBar(
                selectedItem = selected,
                thirdTabLabel = if (academic) "Students" else "My Training",
                chatUnreadCount = if (academic) 0 else 2,
                onItemSelected = onTabSelected
            )
            if (previewFrame) {
                Box(Modifier.fillMaxWidth().height(17.dp), contentAlignment = Alignment.Center) {
                    Box(Modifier.width(100.dp).height(4.dp)
                        .background(Color.White.copy(alpha = .45f), CircleShape))
                }
            }
        }
    }
}

@Composable
private fun TrainingPreviewStatusBar() {
    Box(Modifier.fillMaxWidth().height(28.dp).padding(horizontal = 20.dp)) {
        TrainingText("9:41", 12, weight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterStart))
        Canvas(Modifier.align(Alignment.CenterEnd).size(50.dp, 14.dp)) {
            val u = size.height / 14f
            repeat(4) { i ->
                val h = (3 + i * 2) * u
                drawRect(TrainingInk, Offset(i * 3 * u, 12 * u - h), Size(2 * u, h))
            }
            listOf(12f, 8f, 4f).forEach { d ->
                drawArc(TrainingInk, 225f, 90f, false,
                    topLeft = Offset((23f - d / 2) * u, (11f - d / 2) * u),
                    size = Size(d * u, d * u), style = Stroke(1.2f * u))
            }
            drawCircle(TrainingInk, u, Offset(23 * u, 11 * u))
            drawRect(TrainingInk, Offset(36 * u, 3 * u), Size(7 * u, 10 * u))
            drawRect(TrainingInk, Offset(38 * u, 1.5f * u), Size(3 * u, 2 * u))
        }
    }
}

@Composable
internal fun TrainingTitle(title: String, back: Boolean = false, subtitle: String? = null, onBack: () -> Unit = {}) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (back) {
            Icon(Icons.Outlined.ArrowBack, "Back", Modifier.size(21.dp).clickable(onClick = onBack), tint = TrainingInk)
            Spacer(Modifier.width(18.dp))
        }
        Column {
            Text(title, color = TrainingInk, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 24.sp)
            if (subtitle != null) Text(subtitle, color = TrainingMuted, fontSize = 12.sp, lineHeight = 16.sp)
        }
    }
}

@Composable
internal fun TrainingCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
        color = Color.White, border = BorderStroke(1.dp, TrainingLine)
    ) {
        Column(Modifier.fillMaxWidth().padding(14.dp), content = content)
    }
}

@Composable
internal fun TrainingText(
    value: String, size: Int = 13, color: Color = TrainingInk,
    weight: FontWeight = FontWeight.Normal, modifier: Modifier = Modifier
) {
    Text(value, modifier, color = color, fontSize = size.sp, lineHeight = (size + 4).sp, fontWeight = weight)
}

/** Read-only box (kept for the design). */
@Composable
internal fun TrainingField(value: String, modifier: Modifier = Modifier, suffix: String? = null, multiline: Boolean = false) {
    Surface(
        modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(15.dp), color = Color.White,
        border = BorderStroke(1.dp, Color(0xFF8A898F))
    ) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = if (multiline) 75.dp else 52.dp)
                .padding(horizontal = 15.dp, vertical = 12.dp),
            verticalAlignment = if (multiline) Alignment.Top else Alignment.CenterVertically
        ) {
            TrainingText(value, 15, Color(0xFF3A365D), modifier = Modifier.weight(1f))
            if (suffix != null) {
                Spacer(Modifier.width(8.dp))
                TrainingText(suffix, 14, TrainingMuted)
            }
        }
    }
}

/** Same look as TrainingField, but the user can type in it. */
@Composable
internal fun TrainingInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    suffix: String? = null,
    placeholder: String = "",
    multiline: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    minHeight: Int = if (multiline) 75 else 52,
    isError: Boolean = false
) {
    Surface(
        modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(15.dp), color = Color.White,
        border = BorderStroke(1.dp, if (isError) TrainingError else Color(0xFF8A898F))
    ) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = minHeight.dp)
                .padding(horizontal = 15.dp, vertical = 12.dp),
            verticalAlignment = if (multiline) Alignment.Top else Alignment.CenterVertically
        ) {
            Box(Modifier.weight(1f)) {
                if (value.isEmpty() && placeholder.isNotEmpty()) TrainingText(placeholder, 15, Color(0xFF9A99A0))
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = !multiline,
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    textStyle = TextStyle(color = Color(0xFF3A365D), fontSize = 15.sp, lineHeight = 19.sp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (suffix != null) {
                Spacer(Modifier.width(8.dp))
                TrainingText(suffix, 14, TrainingMuted)
            }
        }
    }
}

@Composable
internal fun TrainingAction(label: String, onClick: () -> Unit = {}, enabled: Boolean = true, loading: Boolean = false) {
    Button(
        onClick = onClick, enabled = enabled && !loading, modifier = Modifier.fillMaxWidth().height(46.dp),
        shape = RoundedCornerShape(15.dp),
        colors = ButtonDefaults.buttonColors(containerColor = TrainingNavy, contentColor = Color.White)
    ) {
        if (loading) CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
        else Text(label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

/** Loading / error / empty message inside a card. */
@Composable
internal fun TrainingMessage(text: String, loading: Boolean = false, onRetry: (() -> Unit)? = null) {
    TrainingCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (loading) {
                CircularProgressIndicator(Modifier.size(18.dp), color = TrainingNavy, strokeWidth = 2.dp)
                Spacer(Modifier.width(10.dp))
            }
            TrainingText(text, 14, TrainingMuted, modifier = Modifier.weight(1f))
            if (onRetry != null) {
                TextButton(onClick = onRetry) { Text("Retry", color = TrainingNavy, fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}

@Composable
private fun TrainingGlyph(icon: ImageVector, tint: Color = TrainingTeal, size: Int = 16) {
    Icon(icon, null, Modifier.size(size.dp), tint = tint)
}

// ---------------------------------------------------------------------
// My Training screen
// ---------------------------------------------------------------------

@Composable
fun MyTrainingScreen(
    onTabSelected: (BottomBarItem) -> Unit = {},
    studentEmail: String = DEMO_STUDENT_EMAIL // replace with the logged-in user's email after login is connected
) {
    val trainingViewModel: TrainingViewModel = viewModel(factory = MasarakViewModelFactory())
    val state by trainingViewModel.myTraining.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(studentEmail) {
        trainingViewModel.loadStudentTraining(studentEmail)
    }

    MyTrainingLayout(
        state = state,
        onTabSelected = onTabSelected,
        onRetry = { trainingViewModel.loadStudentTraining(studentEmail) },
        onSubmitWeek = { days, hours, reportName, reportRef, done ->
            scope.launch {
                done(trainingViewModel.submitCurrentWeek(days, hours, reportName, reportRef))
            }
        }
    )
}

@Composable
private fun MyTrainingLayout(
    state: MyTrainingUiState,
    previewFrame: Boolean = false,
    showTrainingRecord: Boolean = true,
    onTabSelected: (BottomBarItem) -> Unit = {},
    onRetry: () -> Unit = {},
    onSubmitWeek: (String, String, String?, String?, (String?) -> Unit) -> Unit = { _, _, _, _, _ -> }
) {
    TrainingPage(BottomBarItem.MyTraining, previewFrame = previewFrame, onTabSelected = onTabSelected, header = { TrainingTitle("My Training") }) {
        when {
            state.isLoading -> TrainingMessage("Loading your training…", loading = true)
            state.error != null -> TrainingMessage(state.error, onRetry = onRetry)
            !state.hasRecord -> TrainingMessage("You don't have a training record yet.")
            else -> {
                if (showTrainingRecord) TrainingRecord(state)
                when {
                    state.notStarted -> TrainingMessage("Your training starts on ${state.period.substringBefore(" –")}.")
                    state.canSubmitCurrentWeek -> CurrentTrainingWeek(state, onSubmitWeek)
                    state.ended && state.pastWeeks.isEmpty() -> TrainingMessage("Your training period has ended.")
                }
                state.pastWeeks.forEach { TrainingWeekCard(it) }
            }
        }
    }
}

@Composable
private fun TrainingRecord(state: MyTrainingUiState) {
    TrainingCard {
        TrainingText("TRAINING RECORD", 11, TrainingMuted, FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        TrainingText(state.organization, 15, weight = FontWeight.Bold)
        TrainingText("${state.period} · ${state.confirmedHours} of ${state.targetHours} h confirmed", 12, TrainingMuted)
        HorizontalDivider(Modifier.padding(vertical = 10.dp), color = TrainingLine)
        SupervisorRow(state.fieldSupervisor, academic = false)
        Spacer(Modifier.height(12.dp))
        SupervisorRow(state.academicSupervisor, academic = true)
    }
}

@Composable
private fun SupervisorRow(supervisor: SupervisorUi, academic: Boolean) {
    val role = if (academic) "Academic Supervisor" else "Field Supervisor"
    val accepted = supervisor.status == "accepted" && supervisor.name != null
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(31.dp).background(if (accepted) Color(0xFFF8E5B9) else Color(0xFFF1F1EE), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (accepted) TrainingText(initials(supervisor.name!!), 11, weight = FontWeight.Bold)
            else TrainingGlyph(if (academic) Icons.Outlined.AccountBalance else Icons.Outlined.Work, TrainingMuted)
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            if (accepted) {
                TrainingText(supervisor.name!!, 13, weight = FontWeight.Bold)
                TrainingText(
                    if (academic) "$role · grades your weekly evaluation"
                    else "$role · reviews tasks and confirms attendance",
                    12, TrainingMuted
                )
            } else {
                TrainingText(role, 13, weight = FontWeight.Bold)
                TrainingText(
                    if (supervisor.status == "pending") "Linking request waiting in Profile" else "Not linked yet · add one in Profile",
                    12, TrainingTeal, FontWeight.SemiBold
                )
            }
        }
        if (!accepted) TrainingGlyph(Icons.Outlined.ChevronRight, TrainingMuted)
    }
}

private fun initials(name: String): String =
    name.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }

@Composable
private fun CurrentTrainingWeek(
    state: MyTrainingUiState,
    onSubmit: (String, String, String?, String?, (String?) -> Unit) -> Unit
) {
    val context = LocalContext.current
    val pending = state.currentWeekPending

    // Form fields (filled from the database if this week was already sent)
    var days by remember(pending?.weekId) { mutableStateOf(pending?.daysAttended?.toString().orEmpty()) }
    var hours by remember(pending?.weekId) { mutableStateOf(pending?.hours.orEmpty()) }
    var reportName by remember(pending?.weekId) { mutableStateOf(pending?.reportFileName) }
    var reportRef by remember(pending?.weekId) { mutableStateOf(pending?.reportFileRef) }
    var error by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }

    // File picker for the weekly report (not available inside Android Studio preview)
    val pickReport = if (LocalInspectionMode.current) null else rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            // Keep access to the file after the app restarts
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            reportName = fileName(context, uri)
            reportRef = uri.toString()
            error = null
        }
    }

    TrainingCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                TrainingText("Week ${state.currentWeekNo}", 17, weight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                TrainingText(state.currentWeekRange, 13, TrainingMuted)
            }
            if (pending != null) StatusBadge(confirmed = false)
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                TrainingText("Days attended", 12, weight = FontWeight.SemiBold)
                TrainingInput(
                    days, { days = it.filter(Char::isDigit).take(1); error = null; saved = false },
                    suffix = "days", placeholder = "0", keyboardType = KeyboardType.Number
                )
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                TrainingText("Training hours", 12, weight = FontWeight.SemiBold)
                TrainingInput(
                    hours, { hours = it.filter { c -> c.isDigit() || c == '.' }.take(5); error = null; saved = false },
                    suffix = "h", placeholder = "0", keyboardType = KeyboardType.Decimal
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        TrainingText("Weekly report", 12, weight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        val currentReport = reportName
        if (currentReport != null) {
            TrainingReport(
                currentReport,
                onOpen = { reportRef?.let { openReport(context, it) } },
                onRemove = { reportName = null; reportRef = null; saved = false }
            )
        } else {
            OutlinedButton(
                onClick = {
                    pickReport?.launch(
                        arrayOf(
                            "application/pdf",
                            "application/msword",
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                            "image/*"
                        )
                    )
                },
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, TrainingLine)
            ) {
                TrainingGlyph(Icons.Outlined.AttachFile)
                Spacer(Modifier.width(6.dp))
                Text("Attach report", color = TrainingNavy, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(Modifier.height(12.dp))

        val fieldName = state.fieldSupervisor.name?.takeIf { state.fieldSupervisor.status == "accepted" }
        when {
            error != null -> TrainingText(error!!, 13, TrainingError, FontWeight.Medium)
            saved -> TrainingText("Week saved. ${fieldName ?: "Your field supervisor"} will confirm it.", 13, TrainingTeal, FontWeight.Medium)
            pending != null -> TrainingText("Sent · waiting for ${fieldName ?: "your field supervisor"} to confirm. You can still update it.", 13, TrainingMuted)
            fieldName != null -> TrainingText("Ready to send. $fieldName will confirm it.", 13, TrainingMuted)
            else -> TrainingText("No field supervisor linked yet. You can still submit the week.", 13, TrainingMuted)
        }
        Spacer(Modifier.height(10.dp))
        TrainingAction(
            label = if (pending != null) "Update week" else "Submit week",
            loading = saving,
            onClick = {
                saving = true
                onSubmit(days, hours, reportName, reportRef) { result ->
                    saving = false
                    error = result
                    saved = result == null
                }
            }
        )
    }
}

@Composable
private fun TrainingReport(fileName: String, onOpen: () -> Unit = {}, onRemove: (() -> Unit)? = null) {
    Surface(
        shape = RoundedCornerShape(8.dp), color = Color(0xFFF1F1EB), border = BorderStroke(1.dp, TrainingLine),
        modifier = Modifier.clickable(onClick = onOpen)
    ) {
        Row(Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TrainingGlyph(Icons.Outlined.Description)
            TrainingText(fileName, 12, weight = FontWeight.Medium, modifier = Modifier.widthIn(max = 220.dp))
            if (onRemove != null) {
                Icon(Icons.Outlined.Close, "Remove report", Modifier.size(15.dp).clickable(onClick = onRemove), tint = TrainingMuted)
            }
        }
    }
}

@Composable
private fun StatusBadge(confirmed: Boolean) {
    val background = if (confirmed) Color(0xFFC9EAC6) else Color(0xFFF8E5B9)
    val ink = if (confirmed) Color(0xFF143B22) else Color(0xFF5C4300)
    Surface(shape = RoundedCornerShape(8.dp), color = background) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
            TrainingGlyph(if (confirmed) Icons.Outlined.CheckCircle else Icons.Outlined.Schedule, ink, 14)
            Spacer(Modifier.width(4.dp))
            TrainingText(if (confirmed) "Confirmed by Field Supervisor" else "Waiting for confirmation", 12, ink, FontWeight.SemiBold)
        }
    }
}

@Composable
private fun TrainingWeekCard(week: TrainingWeekUi) {
    val context = LocalContext.current
    TrainingCard {
        Row {
            Box(Modifier.size(38.dp).background(Color(0xFFCEF0EF), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center) {
                TrainingGlyph(Icons.Outlined.EventAvailable, size = 21)
            }
            Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) {
                TrainingText("Week ${week.weekNo}", 17, weight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                TrainingText(week.dateRange, 13, TrainingMuted)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TrainingGlyph(Icons.Outlined.PersonOutline)
                    TrainingText("${week.daysAttended} days")
                    Spacer(Modifier.width(6.dp))
                    TrainingGlyph(Icons.Outlined.AccessTime)
                    TrainingText("${week.hours} h")
                }
                Spacer(Modifier.height(10.dp))
                TrainingReport(week.reportFileName, onOpen = { openReport(context, week.reportFileRef) })
                Spacer(Modifier.height(10.dp))
                StatusBadge(week.confirmed)
            }
        }
    }
}

// ---------- helpers for the report file ----------

private fun fileName(context: Context, uri: Uri): String =
    runCatching {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0) else null
        }
    }.getOrNull() ?: "weekly-report"

private fun openReport(context: Context, fileRef: String) {
    val uri = Uri.parse(fileRef)
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, context.contentResolver.getType(uri) ?: "*/*")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
    } catch (_: SecurityException) {
    }
}

// ---------- Previews (sample data only, no database) ----------

private val previewState = MyTrainingUiState(
    isLoading = false, hasRecord = true,
    organization = "Nakheel Tech · Software development",
    period = "31 August – 20 November", confirmedHours = "50", targetHours = "240",
    fieldSupervisor = SupervisorUi("Eng. Khalid Alotaibi", "accepted"),
    academicSupervisor = SupervisorUi(null, "pending"),
    currentWeekNo = 3, currentWeekRange = "14–18 September", canSubmitCurrentWeek = true,
    pastWeeks = listOf(
        TrainingWeekUi(2, 2, "7–11 September", 5, "30", "week-2-report.pdf", "", true),
        TrainingWeekUi(1, 1, "31 August–4 September", 4, "20", "week-1-report.pdf", "", true)
    )
)

@Preview(name = "My Training", widthDp = 390, heightDp = 844)
@Composable
private fun MyTrainingPreview() = MaterialTheme { MyTrainingLayout(previewState, previewFrame = true) }

@Preview(name = "My Training · scrolled", widthDp = 390, heightDp = 844)
@Composable
private fun MyTrainingScrolledPreview() = MaterialTheme { MyTrainingLayout(previewState, previewFrame = true, showTrainingRecord = false) }

