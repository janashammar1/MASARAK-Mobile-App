package sa.edu.iau.cshj.masarak

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.delay
import sa.edu.iau.cshj.masarak.ui.components.AppBottomBar
import sa.edu.iau.cshj.masarak.ui.components.BottomBarItem
import sa.edu.iau.cshj.masarak.ui.components.LinkPersonSheet
import sa.edu.iau.cshj.masarak.ui.components.LocalChatUnreadCount
import sa.edu.iau.cshj.masarak.ui.screens.auth.LoginScreen
import sa.edu.iau.cshj.masarak.ui.screens.chat.RoomChatRoute
import sa.edu.iau.cshj.masarak.ui.screens.chat.RoomChatViewModel
import sa.edu.iau.cshj.masarak.ui.screens.chat.RoomConversationRoute
import sa.edu.iau.cshj.masarak.ui.screens.home.StudentHomeScreen
import sa.edu.iau.cshj.masarak.ui.screens.home.SupervisorHomeScreen
import sa.edu.iau.cshj.masarak.ui.screens.notifications.NotificationsScreen
import sa.edu.iau.cshj.masarak.ui.screens.onboarding.SplashScreen
import sa.edu.iau.cshj.masarak.ui.screens.onboarding.TaskDetailsScreen
import sa.edu.iau.cshj.masarak.ui.screens.onboarding.TasksScreen
import sa.edu.iau.cshj.masarak.ui.screens.onboarding.WelcomeScreen
import sa.edu.iau.cshj.masarak.ui.screens.onboarding.sampleTask
import sa.edu.iau.cshj.masarak.ui.screens.people.PeopleListScreen
import sa.edu.iau.cshj.masarak.ui.screens.people.PersonDetailScreen
import sa.edu.iau.cshj.masarak.ui.screens.profile.ProfileRoute
import sa.edu.iau.cshj.masarak.ui.screens.tasks.AddEditTaskScreen
import sa.edu.iau.cshj.masarak.ui.screens.tasks.ReviewRelevanceScreen
import sa.edu.iau.cshj.masarak.ui.screens.tasks.RoomFieldReviewRoute
import sa.edu.iau.cshj.masarak.ui.screens.training.AcademicWeeklyEvaluationScreen
import sa.edu.iau.cshj.masarak.ui.screens.training.MyTrainingScreen
import sa.edu.iau.cshj.masarak.ui.screens.training.WeeklyEvaluationScreen
import sa.edu.iau.cshj.masarak.ui.viewmodel.AuthViewModel
import sa.edu.iau.cshj.masarak.ui.viewmodel.MasarakViewModelFactory
import sa.edu.iau.cshj.masarak.ui.viewmodel.StudentHomeViewModel
import sa.edu.iau.cshj.masarak.ui.viewmodel.TaskViewModel
import java.util.Locale

@Composable
fun AppNavGraph() {
    val nav = rememberNavController()

    val authViewModel: AuthViewModel = viewModel(
        factory = MasarakViewModelFactory()
    )

    val currentUser =
        authViewModel.currentUser.collectAsState().value

    val loginError =
        authViewModel.loginError.collectAsState().value

    val isLoggingIn =
        authViewModel.isLoggingIn.collectAsState().value

    val studentContext: StudentHomeViewModel = viewModel(
        key = "navigation-student-context",
        factory = StudentHomeViewModel.Factory
    )

    val studentState =
        studentContext.state.collectAsState().value

    val studentAccount = currentUser?.takeIf {
        it.role.trim().equals(
            "student",
            ignoreCase = true
        )
    }

    val activeRecordId = studentState.recordId.takeIf {
        studentAccount != null &&
                studentState.userId == studentAccount.userId &&
                !studentState.isLoading &&
                studentState.error == null
    }

    LaunchedEffect(studentAccount?.userId) {
        studentContext.load(studentAccount)
    }

    val roomChat: RoomChatViewModel = viewModel(
        key = "room-chat-" + (currentUser?.userId ?: 0L),
        factory = RoomChatViewModel.Factory
    )

    DisposableEffect(roomChat) {
        onDispose {
            roomChat.bind(null)
        }
    }

    LaunchedEffect(currentUser?.userId) {
        roomChat.bind(currentUser?.userId)
    }

    val chatInbox =
        roomChat.inbox.collectAsState().value

    val unreadChatCount =
        chatInbox.sumOf { it.unreadCount }

    val selectedTask = remember {
        mutableStateOf(sampleTask)
    }

    fun normalizedRole(role: String): String? {
        return when (role.trim().lowercase(Locale.ROOT)) {
            "student" -> "student"
            "field", "field supervisor" -> "field"
            "academic", "academic supervisor" -> "academic"
            else -> null
        }
    }

    fun home(role: String): String {
        return if (role == "student") {
            "student_home"
        } else {
            "supervisor/$role"
        }
    }

    fun tabLabel(role: String): String {
        return when (role) {
            "academic" -> "Students"
            "field" -> "Trainees"
            else -> "My Training"
        }
    }

    fun returnToLogin() {
        nav.navigate("login") {
            popUpTo(nav.graph.id) {
                inclusive = false
                saveState = false
            }

            launchSingleTop = true
            restoreState = false
        }
    }

    fun logout() {
        authViewModel.logout()
        selectedTask.value = sampleTask
        returnToLogin()
    }

    fun selectTab(
        role: String,
        item: BottomBarItem
    ) {
        val route = when (item) {
            BottomBarItem.Home -> home(role)

            BottomBarItem.Chat -> "chat/$role"

            BottomBarItem.Profile -> "profile/$role"

            BottomBarItem.MyTraining -> {
                if (role == "student") {
                    "my_training"
                } else {
                    "people/$role"
                }
            }
        }

        nav.navigate(route) {
            popUpTo(home(role))
            launchSingleTop = true
        }
    }

    LaunchedEffect(
        currentUser?.userId,
        currentUser?.role
    ) {
        val user = currentUser ?: return@LaunchedEffect

        val role = normalizedRole(user.role)
            ?: return@LaunchedEffect

        nav.navigate(home(role)) {
            popUpTo(nav.graph.id) {
                inclusive = false
                saveState = false
            }

            launchSingleTop = true
            restoreState = false
        }
    }

    CompositionLocalProvider(
        LocalChatUnreadCount provides unreadChatCount
    ) {
        NavHost(
            navController = nav,
            startDestination = "splash"
        ) {
            composable("splash") {
                SplashScreen()

                LaunchedEffect(Unit) {
                    delay(1500)

                    nav.navigate("welcome") {
                        popUpTo("splash") {
                            inclusive = true
                        }
                    }
                }
            }

            composable("welcome") {
                WelcomeScreen(
                    onGetStarted = {
                        nav.navigate("login")
                    }
                )
            }

            composable("login") {
                LoginScreen(
                    errorMessage = loginError,
                    isLoading = isLoggingIn,
                    onInputChanged =
                        authViewModel::clearLoginError,
                    onSignIn = { email, password ->
                        authViewModel.login(
                            email,
                            password
                        )
                    }
                )
            }

            dialog("demo_roles") {
                Surface(
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Choose a demo role",
                            style =
                                MaterialTheme.typography.titleLarge
                        )

                        listOf(
                            "Student" to "student_home",
                            "Field Supervisor" to
                                    "supervisor/field",
                            "Academic Supervisor" to
                                    "supervisor/academic"
                        ).forEach { (label, route) ->
                            TextButton(
                                onClick = {
                                    nav.popBackStack()
                                    nav.navigate(route)
                                }
                            ) {
                                Text(label)
                            }
                        }

                        TextButton(
                            onClick = {
                                nav.popBackStack()
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            }

            composable("supervisor/{role}") { entry ->
                val role = entry.arguments
                    ?.getString("role")
                    ?: "field"

                SupervisorHomeScreen(
                    isAcademic = role == "academic",
                    currentUser = currentUser,
                    onPeopleClick = {
                        nav.navigate("people/$role")
                    },
                    onNotifications = {
                        nav.navigate("notifications/$role")
                    },
                    onTabSelected = {
                        selectTab(role, it)
                    },
                    onPersonClick = { name, recordId ->
                        nav.navigate(
                            "person/$role/${Uri.encode(name)}/$recordId"
                        )
                    }
                )
            }

            composable("people/{role}") { entry ->
                val role = entry.arguments
                    ?.getString("role")
                    ?: "field"

                PeopleListScreen(
                    isAcademic = role == "academic",
                    supervisorId = currentUser?.userId,
                    onLinkClick = {
                        nav.navigate("link_person/$role")
                    },
                    onPersonClick = { name, recordId ->
                        nav.navigate(
                            "person/$role/${Uri.encode(name)}/$recordId"
                        )
                    },
                    onBottomBarItemSelected = {
                        selectTab(role, it)
                    }
                )
            }

            composable(
                "person/{role}/{name}/{recordId}"
            ) { entry ->
                val academic = entry.arguments
                    ?.getString("role") == "academic"

                val recordId = entry.arguments
                    ?.getString("recordId")
                    ?.toLongOrNull()
                    ?: 0L

                val personName = entry.arguments
                    ?.getString("name")

                PersonDetailScreen(
                    isAcademic = academic,
                    personName = personName,
                    recordId = recordId,
                    currentUserId = currentUser?.userId,
                    onBackClick = {
                        nav.popBackStack()
                    },
                    onReviewClick = { submissionId ->
                        nav.navigate(
                            "review_relevance/$submissionId"
                        )
                    },
                    onFieldReview = if (!academic) {
                        { submissionId ->
                            nav.navigate(
                                "review_task/$submissionId"
                            )
                        }
                    } else {
                        null
                    },
                    onEvaluation = if (
                        academic && recordId != 0L
                    ) {
                        {
                            nav.navigate(
                                "academic_weekly_evaluation/$recordId"
                            )
                        }
                    } else {
                        null
                    }
                )
            }

            composable("link_person/{role}") { entry ->
                LinkPersonSheet(
                    isAcademic = entry.arguments
                        ?.getString("role") == "academic",
                    currentUserId = currentUser?.userId,
                    onDismiss = {
                        nav.popBackStack()
                    }
                )
            }

            composable(
                "review_task/{submissionId}"
            ) { entry ->
                val submissionId = entry.arguments
                    ?.getString("submissionId")
                    ?.toLongOrNull()

                val userId = currentUser?.userId

                if (
                    submissionId != null &&
                    userId != null
                ) {
                    RoomFieldReviewRoute(
                        submissionId = submissionId,
                        reviewerId = userId,
                        onBack = {
                            nav.popBackStack()
                        },
                        onTab = {
                            selectTab("field", it)
                        }
                    )
                } else {
                    TextButton(
                        onClick = {
                            nav.popBackStack()
                        }
                    ) {
                        Text("Back")
                    }
                }
            }

            composable(
                "review_relevance/{submissionId}"
            ) { entry ->
                val submissionId = entry.arguments
                    ?.getString("submissionId")
                    ?.toLongOrNull()

                if (submissionId != null) {
                    ReviewRelevanceScreen(
                        submissionId = submissionId,
                        reviewerId = currentUser?.userId,
                        onBackClick = {
                            nav.popBackStack()
                        }
                    )
                } else {
                    TextButton(
                        onClick = {
                            nav.popBackStack()
                        }
                    ) {
                        Text("Back")
                    }
                }
            }

            composable("student_home") {
                StudentHomeScreen(
                    currentUser = currentUser,
                    onWeeklyEvaluation = {
                        nav.navigate("weekly_evaluation")
                    },
                    onTasksClick = {
                        nav.navigate("tasks")
                    },
                    onNotifications = {
                        nav.navigate(
                            "notifications/student"
                        )
                    },
                    onTabSelected = {
                        selectTab("student", it)
                    },
                    onAddTask = {
                        nav.navigate("task_form")
                    }
                )
            }

            composable(
                "notifications/{role}"
            ) { entry ->
                val role = entry.arguments
                    ?.getString("role")
                    ?: "student"

                NotificationsScreen(
                    role = role,
                    currentUser = currentUser,
                    onBack = {
                        nav.popBackStack()
                    },
                    onTabSelected = {
                        selectTab(role, it)
                    }
                )
            }

            composable("chat/{role}") {
                val role = currentUser?.role
                    ?.let(::normalizedRole)
                    ?: "student"

                RoomChatRoute(
                    vm = roomChat,
                    thirdTabLabel = tabLabel(role),
                    academic = role == "academic",
                    onConversation = { id ->
                        nav.navigate(
                            "conversation/$role/$id"
                        )
                    },
                    onTab = {
                        selectTab(role, it)
                    }
                )
            }

            composable(
                "conversation/{role}/{id}"
            ) { entry ->
                val role = currentUser?.role
                    ?.let(::normalizedRole)
                    ?: "student"

                val id = entry.arguments
                    ?.getString("id")
                    ?.toLongOrNull()

                val userId = currentUser?.userId

                if (id != null && userId != null) {
                    RoomConversationRoute(
                        vm = roomChat,
                        userId = userId,
                        conversationId = id,
                        thirdTabLabel = tabLabel(role),
                        onBack = {
                            nav.popBackStack()
                        },
                        onTab = {
                            selectTab(role, it)
                        }
                    )
                } else {
                    TextButton(
                        onClick = {
                            nav.popBackStack()
                        }
                    ) {
                        Text("Back to Chat")
                    }
                }
            }

            composable("profile/{role}") {
                val user = currentUser

                val role = user?.let {
                    normalizedRole(it.role)
                }

                if (user == null || role == null) {
                    LaunchedEffect(Unit) {
                        logout()
                    }
                } else {
                    ProfileRoute(
                        userId = user.userId,
                        onLogout = {
                            logout()
                        },
                        bottomBar = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Color(0xFF0C0D45)
                                    )
                                    .navigationBarsPadding()
                            ) {
                                AppBottomBar(
                                    selectedItem =
                                        BottomBarItem.Profile,
                                    thirdTabLabel =
                                        tabLabel(role),
                                    chatUnreadCount = 0,
                                    onItemSelected = {
                                        selectTab(role, it)
                                    }
                                )
                            }
                        }
                    )
                }
            }

            composable("my_training") {
                MyTrainingScreen(
                    studentEmail =
                        studentAccount?.email.orEmpty(),
                    onTabSelected = {
                        selectTab("student", it)
                    }
                )
            }

            composable("weekly_evaluation") {
                WeeklyEvaluationScreen(
                    studentEmail =
                        studentAccount?.email.orEmpty(),
                    onBack = {
                        nav.popBackStack()
                    },
                    onTabSelected = {
                        selectTab("student", it)
                    }
                )
            }

            composable(
                "academic_weekly_evaluation/{recordId}"
            ) { entry ->
                AcademicWeeklyEvaluationScreen(
                    academicEmail =
                        currentUser?.email.orEmpty(),
                    recordId = entry.arguments
                        ?.getString("recordId")
                        ?.toLongOrNull()
                        ?: 0L,
                    onBack = {
                        nav.popBackStack()
                    },
                    onTabSelected = {
                        selectTab("academic", it)
                    }
                )
            }

            composable("tasks") {
                val taskViewModel: TaskViewModel = viewModel(
                    key = "student-tasks-${studentAccount?.userId}-$activeRecordId",
                    factory = MasarakViewModelFactory()
                )

                if (activeRecordId == null) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = if (studentState.isLoading) {
                                "Loading training..."
                            } else {
                                studentState.error
                                    ?: "No training record is available for your account."
                            }
                        )

                        TextButton(
                            onClick = {
                                nav.popBackStack()
                            }
                        ) {
                            Text("Back")
                        }
                    }

                    return@composable
                }

                TasksScreen(
                    taskViewModel = taskViewModel,
                    recordId = activeRecordId,
                    onBackClick = {
                        nav.popBackStack()
                    },
                    onTaskClick = {
                        selectedTask.value = it
                        nav.navigate("task_details")
                    },
                    onAddTask = {
                        nav.navigate("task_form")
                    }
                )
            }

            composable("task_details") {
                TaskDetailsScreen(
                    task = selectedTask.value,
                    onBackClick = {
                        nav.popBackStack()
                    },
                    onEditClick = {
                        val submissionId =
                            selectedTask.value.submissionId

                        if (submissionId != null) {
                            nav.navigate(
                                "task_form?submissionId=$submissionId"
                            )
                        }
                    }
                )
            }

            composable(
                route = "task_form?submissionId={submissionId}",
                arguments = listOf(
                    navArgument("submissionId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { backStackEntry ->
                AddEditTaskScreen(
                    recordId = activeRecordId,
                    onBackClick = {
                        nav.popBackStack()
                    },
                    submissionId = backStackEntry.arguments
                        ?.getLong("submissionId")
                        ?.takeIf { it > 0L }
                )
            }
        }
    }
}