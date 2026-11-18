package sa.edu.iau.cshj.masarak

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import sa.edu.iau.cshj.masarak.ui.components.LinkPersonSheet
import sa.edu.iau.cshj.masarak.ui.screens.tasks.ReviewTaskScreen
import sa.edu.iau.cshj.masarak.ui.screens.training.MyTrainingScreen
import sa.edu.iau.cshj.masarak.ui.screens.training.WeeklyEvaluationScreen
import sa.edu.iau.cshj.masarak.ui.screens.training.AcademicWeeklyEvaluationScreen
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.graphics.Color
import sa.edu.iau.cshj.masarak.ui.components.AppBottomBar
import sa.edu.iau.cshj.masarak.ui.components.BottomBarItem
import sa.edu.iau.cshj.masarak.ui.screens.chat.*
import sa.edu.iau.cshj.masarak.ui.screens.notifications.NotificationsScreen
import sa.edu.iau.cshj.masarak.ui.screens.profile.ProfileScreen
import sa.edu.iau.cshj.masarak.ui.screens.profile.demoProfile
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.navigation.compose.dialog
import sa.edu.iau.cshj.masarak.ui.screens.home.SupervisorHomeScreen
import sa.edu.iau.cshj.masarak.ui.screens.people.PeopleListScreen
import sa.edu.iau.cshj.masarak.ui.screens.people.PersonDetailScreen
import sa.edu.iau.cshj.masarak.ui.screens.tasks.ReviewRelevanceScreen
import sa.edu.iau.cshj.masarak.ui.screens.auth.LoginScreen
import sa.edu.iau.cshj.masarak.ui.screens.home.StudentHomeScreen
import sa.edu.iau.cshj.masarak.ui.screens.onboarding.SplashScreen
import sa.edu.iau.cshj.masarak.ui.screens.onboarding.WelcomeScreen
import sa.edu.iau.cshj.masarak.ui.screens.onboarding.TaskDetailsScreen
import sa.edu.iau.cshj.masarak.ui.screens.onboarding.TasksScreen
import sa.edu.iau.cshj.masarak.ui.screens.onboarding.sampleTask
import sa.edu.iau.cshj.masarak.ui.screens.tasks.AddEditTaskScreen

@Composable
fun AppNavGraph() {
    val nav = rememberNavController()
    val selectedTask = remember { mutableStateOf(sampleTask) }
    fun home(role: String) = if (role == "student") "student_home" else "supervisor/$role"
    fun tabLabel(role: String) = when (role) {
        "academic" -> "Students"
        "field" -> "Trainees"
        else -> "My Training"
    }
    fun selectTab(role: String, item: BottomBarItem) {
        val route = when (item) {
            BottomBarItem.Home -> home(role)
            BottomBarItem.Chat -> "chat/$role"
            BottomBarItem.Profile -> "profile/$role"
            BottomBarItem.MyTraining -> if (role != "student") "people/$role" else "my_training"
            else -> return
        }
        nav.navigate(route) {
            popUpTo(home(role))
            launchSingleTop = true
        }
    }

    NavHost(navController = nav, startDestination = "splash") {
        composable("splash") {
            SplashScreen()
            LaunchedEffect(Unit) {
                delay(1500)
                nav.navigate("welcome") { popUpTo("splash") { inclusive = true } }
            }
        }
        composable("welcome") {
            WelcomeScreen(onGetStarted = { nav.navigate("login") })
        }
        composable("login") {
            LoginScreen(onSignIn = { _, _ -> nav.navigate("demo_roles") })
        }
        dialog("demo_roles") {
            Surface(shape = MaterialTheme.shapes.large) {
                Column(Modifier.padding(24.dp)) {
                    Text("Choose a demo role", style = MaterialTheme.typography.titleLarge)
                    listOf("Student" to "student_home", "Field Supervisor" to "supervisor/field",
                        "Academic Supervisor" to "supervisor/academic").forEach { (label, route) ->
                        TextButton(onClick = {
                            nav.popBackStack()
                            nav.navigate(route)
                        }) { Text(label) }
                    }
                    TextButton(onClick = { nav.popBackStack() }) { Text("Cancel") }
                }
            }
        }
        composable("supervisor/{role}") { entry ->
            val role = entry.arguments?.getString("role") ?: "field"
            SupervisorHomeScreen(
                isAcademic = role == "academic",
                onPeopleClick = { nav.navigate("people/$role") },
                onNotifications = { nav.navigate("notifications/$role") },
                onTabSelected = { selectTab(role, it) }
            )
        }
        composable("people/{role}") { entry ->
            val role = entry.arguments?.getString("role") ?: "field"
            PeopleListScreen(isAcademic = role == "academic", onLinkClick = { nav.navigate("link_person/$role") }, onPersonClick = { name ->
                nav.navigate("person/$role/${Uri.encode(name)}")
            })
        }
        composable("person/{role}/{name}") { entry ->
            val academic = entry.arguments?.getString("role") == "academic"
            PersonDetailScreen(isAcademic = academic, personName = entry.arguments?.getString("name"),
                onBackClick = { nav.popBackStack() }, onReviewClick = { nav.navigate("review_relevance") },
                onFieldReview = if (!academic) ({ nav.navigate("review_task") }) else null,
                onEvaluation = if (academic && entry.arguments?.getString("name") == "Noura Almutairi") {
                    { nav.navigate("academic_weekly_evaluation") }
                } else null)
        }
        composable("link_person/{role}") { entry ->
            LinkPersonSheet(isAcademic = entry.arguments?.getString("role") == "academic",
                onDismiss = { nav.popBackStack() })
        }
        composable("review_task") {
            ReviewTaskScreen(
                onBack = { nav.popBackStack() },
                onApprove = { nav.popBackStack() },
                onReturn = { nav.popBackStack() },
                onIncomplete = { nav.popBackStack() },
                chatUnreadCount = 1,
                onBottomBarItemSelected = { selectTab("field", it) }
            )
        }
        composable("review_relevance") {
            ReviewRelevanceScreen(onBackClick = { nav.popBackStack() })
        }
        composable("student_home") {
            StudentHomeScreen(
                onWeeklyEvaluation = { nav.navigate("weekly_evaluation") },
                onTasksClick = { nav.navigate("tasks") },
                onNotifications = { nav.navigate("notifications/student") },
                onTabSelected = { selectTab("student", it) },
                onAddTask = { nav.navigate("task_form") }
            )
        }
        composable("notifications/{role}") { entry ->
            val role = entry.arguments?.getString("role") ?: "student"
            NotificationsScreen(role, onBack = { nav.popBackStack() }, onTabSelected = { selectTab(role, it) })
        }
        composable("chat/{role}") { entry ->
            val role = entry.arguments?.getString("role") ?: "student"
            ChatScreen(
                conversations = demoConversations(role),
                thirdTabLabel = tabLabel(role),
                usePlainBackground = role == "academic",
                onConversationClick = { id -> nav.navigate("conversation/$role/${Uri.encode(id)}") },
                onBottomBarItemSelected = { selectTab(role, it) }
            )
        }
        composable("conversation/{role}/{id}") { entry ->
            val role = entry.arguments?.getString("role") ?: "student"
            val contact = demoConversations(role).firstOrNull {
                it.conversationId == entry.arguments?.getString("id")
            }
            if (contact != null) {
                ConversationScreen(
                    onBack = { nav.popBackStack() },
                    contactName = contact.name,
                    role = when (contact.role) {
                        "Field Supervisor" -> "Field"
                        "Academic Supervisor" -> "Academic"
                        else -> "Student"
                    },
                    conversationInfo = null,
                    messages = when (contact.conversationId) {
                        "student-field-supervisor" -> fieldMessages()
                        "academic-student-sara" -> studentMessages()
                        "field-academic-supervisor" -> academicMessages()
                        else -> listOf(ConversationMessage(
                            text = contact.lastMessage.removePrefix("You: "),
                            time = contact.time,
                            isSent = contact.lastMessage.startsWith("You: ")
                        ))
                    },
                    thirdTabLabel = tabLabel(role),
                    onBottomBarItemSelected = { selectTab(role, it) }
                )
            } else {
                TextButton(onClick = { nav.popBackStack() }) { Text("Back to Chat") }
            }
        }
        composable("profile/{role}") { entry ->
            val role = entry.arguments?.getString("role") ?: "student"
            ProfileScreen(
                person = demoProfile(role),
                onLogout = { nav.navigate("login") {
                    popUpTo("login") { inclusive = true }
                } },
                bottomBar = {
                    Box(Modifier.fillMaxWidth().background(Color(0xFF0C0D45)).navigationBarsPadding()) {
                        AppBottomBar(
                            selectedItem = BottomBarItem.Profile,
                            thirdTabLabel = tabLabel(role),
                            chatUnreadCount = when (role) { "student" -> 2; "field" -> 1; else -> 0 },
                            onItemSelected = { selectTab(role, it) }
                        )
                    }
                }
            )
        }
        composable("my_training") {
            MyTrainingScreen(onTabSelected = { selectTab("student", it) })
        }
        composable("weekly_evaluation") {
            WeeklyEvaluationScreen(onBack = { nav.popBackStack() }, onTabSelected = { selectTab("student", it) })
        }
        composable("academic_weekly_evaluation") {
            AcademicWeeklyEvaluationScreen(onBack = { nav.popBackStack() }, onTabSelected = { selectTab("academic", it) })
        }
        composable("tasks") {
            TasksScreen(
                onBackClick = { nav.popBackStack() },
                onTaskClick = {
                    selectedTask.value = it
                    nav.navigate("task_details")
                },
                onAddTask = { nav.navigate("task_form") }
            )
        }
        composable("task_details") {
            TaskDetailsScreen(
                task = selectedTask.value,
                onBackClick = { nav.popBackStack() },
                onEditClick = { nav.navigate("task_form") }
            )
        }
        composable("task_form") { AddEditTaskScreen(onBackClick = { nav.popBackStack() }) }
    }
}
