
package sa.edu.iau.cshj.masarak.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    val deepNavy = Color(0xFF0C0D45)
    val pearlAqua = Color(0xFF75CBD1)

    NavigationBar(
        modifier = Modifier.height(76.dp),
        containerColor = deepNavy,
        contentColor = Color.White,
        tonalElevation = 0.dp
    ) {
        BottomBarItem.values().forEach { item ->

            NavigationBarItem(
                selected = selectedItem == item,
                onClick = { onItemSelected(item) },

                icon = {
                    when (item) {
                        BottomBarItem.Home -> {
                            Icon(
                                Icons.Filled.Home,
                                contentDescription = item.label,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        BottomBarItem.Chat -> {
                            Icon(
                                Icons.Filled.ChatBubbleOutline,
                                contentDescription = item.label,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        BottomBarItem.MyTraining -> {
                            Icon(
                                Icons.Filled.WorkOutline,
                                contentDescription = item.label,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        BottomBarItem.Profile -> {
                            Icon(
                                Icons.Filled.PersonOutline,
                                contentDescription = item.label,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                },

                label = {
                    Text(
                        text = item.label,
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
