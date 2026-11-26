package sa.edu.iau.cshj.masarak.ui.screens.people

import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import sa.edu.iau.cshj.masarak.ui.viewmodel.TrainingViewModel
import sa.edu.iau.cshj.masarak.ui.viewmodel.MasarakViewModelFactory
import sa.edu.iau.cshj.masarak.data.dao.SupervisorPersonRow
import sa.edu.iau.cshj.masarak.ui.components.AppBottomBar
import sa.edu.iau.cshj.masarak.ui.components.BottomBarItem

internal val PeopleNavy = Color(0xFF0C0D45)
internal val PeopleInk = Color(0xFF191928)
internal val PeopleMuted = Color(0xFF505058)
internal val PeoplePaper = Color(0xFFF6F5F3)
internal val PeopleLine = Color(0xFFDFDEDA)
internal val PeopleMint = Color(0xFFCBEBED)
internal val PeopleTeal = Color(0xFF00696E)
internal val PeopleLilac = Color(0xFFE3E4F2)

@Composable
fun PeopleListScreen() =
    PeopleListScreen(isAcademic = true, supervisorId = null)

@Composable
fun PeopleListScreen(
    isAcademic: Boolean,
    supervisorId: Long? = null,
    onPersonClick: (String, Long) -> Unit = { _, _ -> },
    onLinkClick: () -> Unit = {},
    onBottomBarItemSelected: (BottomBarItem) -> Unit = {}
) {
    val preview = LocalInspectionMode.current

    if (preview) {
        PeopleListLayout(
            academic = isAcademic,
            preview = true,
            onPersonClick = onPersonClick,
            onLinkClick = onLinkClick,
            onBottomBarItemSelected = onBottomBarItemSelected
        )
    } else {
        val trainingViewModel: TrainingViewModel =
            viewModel(factory = MasarakViewModelFactory())

        val people by trainingViewModel.people.collectAsState()
        val isLoading by trainingViewModel.peopleLoading.collectAsState()
        val error by trainingViewModel.peopleError.collectAsState()

        val reviewStage = if (isAcademic) "Academic" else "Field"

        LaunchedEffect(supervisorId, reviewStage) {
            if (supervisorId != null && supervisorId > 0L) {
                trainingViewModel.loadPeopleForSupervisor(
                    supervisorId,
                    reviewStage
                )
            }
        }

        PeopleListLayout(
            academic = isAcademic,
            preview = false,
            people = people,
            supervisorId = supervisorId,
            isLoading = isLoading,
            error = error,
            onPersonClick = onPersonClick,
            onLinkClick = onLinkClick,
            onBottomBarItemSelected = onBottomBarItemSelected
        )
    }
}

@Composable
internal fun PeopleListLayout(
    academic: Boolean,
    preview: Boolean,
    people: List<SupervisorPersonRow> = emptyList(),
    supervisorId: Long? = null,
    isLoading: Boolean = false,
    error: String? = null,
    onPersonClick: (String, Long) -> Unit = { _, _ -> },
    onLinkClick: () -> Unit = {},
    onBottomBarItemSelected: (BottomBarItem) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredPeople = people.filter { person ->
        person.fullName.contains(searchQuery, ignoreCase = true) ||
                person.email.contains(searchQuery, ignoreCase = true)
    }

    PeoplePage(
        academic = academic,
        preview = preview,
        onBottomBarItemSelected = onBottomBarItemSelected,
        header = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(53.dp)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PeopleText(
                    if (academic) "Students" else "Trainees",
                    22,
                    bold = true,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    Modifier
                        .size(37.dp)
                        .background(
                            Color.White,
                            RoundedCornerShape(13.dp)
                        )
                        .clickable(onClick = onLinkClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.PersonAdd,
                        "Link a person",
                        Modifier.size(19.dp),
                        tint = PeopleInk
                    )
                }
            }
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp, 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!preview) {
                PeopleSearchField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = if (academic) {
                        "Search students"
                    } else {
                        "Search trainees"
                    }
                )
            }

            PeopleCard {
                when {
                    preview -> {
                        PeopleText(
                            "Preview",
                            13,
                            PeopleMuted,
                            modifier = Modifier.padding(20.dp)
                        )
                    }

                    supervisorId == null || supervisorId <= 0L -> {
                        PeopleText(
                            "Unable to identify the current supervisor",
                            13,
                            PeopleMuted,
                            modifier = Modifier.padding(20.dp)
                        )
                    }

                    isLoading -> {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = PeopleTeal
                            )
                        }
                    }

                    error != null -> {
                        PeopleText(
                            error,
                            13,
                            Color(0xFFB3261E),
                            modifier = Modifier.padding(20.dp)
                        )
                    }

                    filteredPeople.isEmpty() -> {
                        PeopleText(
                            if (searchQuery.isBlank()) {
                                "No linked people found"
                            } else {
                                "No search results found"
                            },
                            13,
                            PeopleMuted,
                            modifier = Modifier.padding(20.dp)
                        )
                    }

                    else -> {
                        filteredPeople.forEachIndexed { index, person ->

                            if (index > 0) {
                                HorizontalDivider(color = PeopleLine)
                            }

                            val initials = person.fullName
                                .split(" ")
                                .mapNotNull { it.firstOrNull() }
                                .take(2)
                                .joinToString("")
                                .uppercase()

                            PeopleListRow(
                                name = person.fullName,
                                status = person.linkStatus,
                                initials = initials,
                                onClick = {
                                    onPersonClick(
                                        person.fullName,
                                        person.recordId
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PeopleSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(
            1.dp,
            Color(0xFF96969D)
        )
    ) {
        Row(
            Modifier
                .height(50.dp)
                .padding(horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.Search,
                null,
                Modifier.size(18.dp),
                tint = PeopleMuted
            )

            Spacer(Modifier.width(11.dp))

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(
                    color = PeopleInk,
                    fontSize = 13.sp
                ),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            PeopleText(
                                placeholder,
                                13,
                                PeopleMuted
                            )
                        }

                        innerTextField()
                    }
                }
            )

            if (value.isNotEmpty()) {
                Icon(
                    Icons.Outlined.Close,
                    "Clear search",
                    Modifier
                        .size(18.dp)
                        .clickable {
                            onValueChange("")
                        },
                    tint = PeopleMuted
                )
            }
        }
    }
}

@Composable
private fun PeopleListRow(
    name: String,
    status: String,
    initials: String?,
    alert: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 67.dp)
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(
                horizontal = 14.dp,
                vertical = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (initials != null) {
            PeopleAvatar(initials)
        } else {
            PeopleTile(
                Icons.Outlined.Link,
                background = Color(0xFFF0EFEB),
                tint = PeopleMuted
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            PeopleText(
                name,
                14,
                bold = true
            )

            Spacer(Modifier.height(3.dp))

            val emphasized =
                status.contains("review", ignoreCase = true) || alert

            val color = if (alert) {
                Color(0xFF9D2427)
            } else if (emphasized) {
                PeopleTeal
            } else {
                PeopleMuted
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                if (alert) {
                    Icon(
                        Icons.Outlined.Warning,
                        null,
                        Modifier.size(12.dp),
                        tint = color
                    )
                }

                PeopleText(
                    status,
                    12,
                    color,
                    bold = emphasized
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        if (initials != null) {
            Icon(
                Icons.Outlined.ChevronRight,
                null,
                Modifier.size(19.dp),
                tint = PeopleInk
            )
        }
    }
}

@Composable
internal fun PeoplePage(
    academic: Boolean,
    preview: Boolean,
    header: @Composable () -> Unit,
    action: (@Composable () -> Unit)? = null,
    onBottomBarItemSelected: (BottomBarItem) -> Unit = {},
    body: @Composable () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(PeoplePaper)
    ) {
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        0f to Color(0xFFD8E5ED),
                        .27f to PeoplePaper,
                        1f to PeoplePaper
                    )
                )
        ) {
            if (preview) {
                PeopleStatusBar()
            } else {
                Spacer(Modifier.statusBarsPadding())
            }

            header()

            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                body()
            }

            action?.invoke()
        }

        if (preview) {
            PeopleBottomBar(academic, true)
        } else {
            Box(Modifier.fillMaxWidth().background(PeopleNavy).navigationBarsPadding()) {
                AppBottomBar(
                    selectedItem = BottomBarItem.MyTraining,
                    thirdTabLabel = if (academic) {
                        "Students"
                    } else {
                        "Trainees"
                    },
                    onItemSelected = onBottomBarItemSelected
                )
            }
        }
    }
}

@Composable
internal fun PeopleStatusBar() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(32.dp)
            .padding(horizontal = 22.dp)
    ) {
        PeopleText(
            "9:41",
            13,
            bold = true,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        Box(
            Modifier
                .align(Alignment.Center)
                .size(15.dp)
                .background(PeopleInk, CircleShape)
        )

        Canvas(
            Modifier
                .align(Alignment.CenterEnd)
                .size(53.dp, 15.dp)
        ) {
            val u = size.height / 15f

            repeat(4) { i ->
                val h = (4 + 2 * i) * u

                drawRect(
                    PeopleInk,
                    Offset(i * 3 * u, 13 * u - h),
                    Size(2 * u, h)
                )
            }

            listOf(13f, 9f, 5f).forEach { d ->
                drawArc(
                    PeopleInk,
                    225f,
                    90f,
                    false,
                    Offset(
                        (24 - d / 2) * u,
                        (12 - d / 2) * u
                    ),
                    Size(d * u, d * u),
                    style = Stroke(1.15f * u)
                )
            }

            drawCircle(
                PeopleInk,
                .9f * u,
                Offset(24 * u, 12 * u)
            )

            drawRect(
                PeopleInk,
                Offset(38 * u, 3 * u),
                Size(8 * u, 10 * u)
            )

            drawRect(
                PeopleInk,
                Offset(40 * u, 1.5f * u),
                Size(4 * u, 2 * u)
            )
        }
    }
}

@Composable
private fun PeopleBottomBar(academic: Boolean, preview: Boolean) {
    Column(Modifier.fillMaxWidth().background(PeopleNavy)) {
        Box(Modifier.fillMaxWidth().background(PeopleNavy).navigationBarsPadding()) {
            AppBottomBar(
                selectedItem = BottomBarItem.MyTraining,
                thirdTabLabel = if (academic) "Students" else "Trainees",
                chatUnreadCount = 0,
                onItemSelected = {}
            )
        }
        if (preview) {
            Box(Modifier.fillMaxWidth().height(17.dp), contentAlignment = Alignment.Center) {
                Box(Modifier.width(100.dp).height(4.dp)
                    .background(Color.White.copy(alpha = .45f), CircleShape))
            }
        }
    }
}

@Composable
internal fun PeopleGestureBar(dark: Boolean) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(19.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .width(101.dp)
                .height(3.dp)
                .background(
                    if (dark) {
                        Color(0xFF81849E)
                    } else {
                        Color(0xFFACADB2)
                    },
                    CircleShape
                )
        )
    }
}

@Composable
internal fun PeopleText(
    value: String,
    size: Int,
    color: Color = PeopleInk,
    bold: Boolean = false,
    modifier: Modifier = Modifier
) {
    Text(
        value,
        modifier,
        color = color,
        fontSize = size.sp,
        lineHeight = (size + 4).sp,
        fontWeight = if (bold) {
            FontWeight.SemiBold
        } else {
            FontWeight.Normal
        }
    )
}

@Composable
internal fun PeopleTitle(
    title: String,
    subtitle: String? = null,
    onBackClick: () -> Unit = {}
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 19.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Outlined.ArrowBack,
            "Back",
            Modifier
                .size(21.dp)
                .clickable(onClick = onBackClick),
            tint = PeopleInk
        )

        Spacer(Modifier.width(16.dp))

        Column {
            PeopleText(
                title,
                17,
                bold = true
            )

            if (subtitle != null) {
                PeopleText(
                    subtitle,
                    12,
                    PeopleMuted
                )
            }
        }
    }
}

@Composable
internal fun PeopleCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        color = Color.White,
        border = BorderStroke(1.dp, PeopleLine)
    ) {
        Column(content = content)
    }
}

@Composable
internal fun PeopleAvatar(
    initials: String,
    small: Boolean = false
) {
    Box(
        Modifier
            .size(if (small) 29.dp else 37.dp)
            .background(PeopleMint, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        PeopleText(
            initials,
            if (small) 10 else 13,
            Color(0xFF112F38),
            bold = true
        )
    }
}

@Composable
internal fun PeopleTile(
    icon: ImageVector,
    background: Color = PeopleLilac,
    tint: Color = PeopleNavy
) {
    Box(
        Modifier
            .size(37.dp)
            .background(
                background,
                RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            null,
            Modifier.size(20.dp),
            tint = tint
        )
    }
}

@Composable
internal fun PeopleInput(
    icon: ImageVector,
    placeholder: String
) {
    Surface(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(
            1.dp,
            Color(0xFF96969D)
        )
    ) {
        Row(
            Modifier
                .height(50.dp)
                .padding(horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                null,
                Modifier.size(18.dp),
                tint = PeopleMuted
            )

            Spacer(Modifier.width(11.dp))

            PeopleText(
                placeholder,
                13,
                PeopleMuted
            )
        }
    }
}

@Preview(
    name = "Academic · Students",
    widthDp = 390,
    heightDp = 844,
    showSystemUi = false
)
@Composable
private fun StudentsPreview() =
    MaterialTheme {
        PeopleListLayout(
            academic = true,
            preview = true
        )
    }

@Preview(
    name = "Field · Trainees",
    widthDp = 390,
    heightDp = 844,
    showSystemUi = false
)
@Composable
private fun TraineesPreview() =
    MaterialTheme {
        PeopleListLayout(
            academic = false,
            preview = true
        )
    }

