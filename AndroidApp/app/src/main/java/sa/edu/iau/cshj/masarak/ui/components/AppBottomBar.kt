package sa.edu.iau.cshj.masarak.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val LocalChatUnreadCount = staticCompositionLocalOf<Int?> { null }

enum class BottomBarItem(val label: String) {
    Home("Home"),
    Chat("Chat"),
    MyTraining("My Training"),
    Profile("Profile")
}

@Composable
fun AppBottomBar(
    selectedItem: BottomBarItem? = null,
    onItemSelected: (BottomBarItem) -> Unit = {}
) {
    AppBottomBar(
        selectedItem = selectedItem,
        thirdTabLabel = BottomBarItem.MyTraining.label,
        onItemSelected = onItemSelected
    )
}

@Composable
fun AppBottomBar(
    selectedItem: BottomBarItem? = null,
    thirdTabLabel: String,
    onItemSelected: (BottomBarItem) -> Unit = {}
) {
    AppBottomBar(
        selectedItem = selectedItem,
        thirdTabLabel = thirdTabLabel,
        chatUnreadCount = 0,
        onItemSelected = onItemSelected
    )
}

@Composable
fun AppBottomBar(
    selectedItem: BottomBarItem? = null,
    thirdTabLabel: String,
    chatUnreadCount: Int,
    onItemSelected: (BottomBarItem) -> Unit = {}
) {
    val unreadCount = LocalChatUnreadCount.current ?: chatUnreadCount
    val deepNavy = Color(0xFF0C0D45)
    val pearlAqua = Color(0xFF75CBD1)

    NavigationBar(
        modifier = Modifier.height(76.dp),
        containerColor = deepNavy,
        contentColor = Color.White,
        tonalElevation = 0.dp
    ) {
        BottomBarItem.values().forEach { item ->
            val displayLabel = if (item == BottomBarItem.MyTraining) {
                thirdTabLabel
            } else {
                item.label
            }

            val icon = when (item) {
                BottomBarItem.Home -> Icons.Filled.Home

                BottomBarItem.Chat -> Icons.Filled.ChatBubbleOutline

                BottomBarItem.MyTraining -> when (thirdTabLabel) {
                    "Trainees" -> Icons.Outlined.Groups
                    "Students" -> Icons.Outlined.School
                    else -> Icons.Filled.WorkOutline
                }

                BottomBarItem.Profile -> Icons.Filled.PersonOutline
            }

            NavigationBarItem(
                selected = selectedItem == item,
                onClick = { onItemSelected(item) },
                icon = {
                    if (item == BottomBarItem.Chat && unreadCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = Color(0xFFC62828),
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = if (unreadCount > 99) {
                                            "99+"
                                        } else {
                                            unreadCount.toString()
                                        }
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = displayLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = deepNavy,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color.White,
                    unselectedTextColor = Color.White,
                    indicatorColor = pearlAqua
                )
            )
        }
    }
}