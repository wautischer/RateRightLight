package at.wautschaar.raterightlight;

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import at.wautschaar.raterightlight.ui.theme.RateRightLightTheme

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RateRightLightTheme {
                val items = listOf(
                    BottomNavigationItem(
                        title = "MyList",
                        selectedIcon = Icons.Filled.List,
                        unselectedIcon = Icons.Outlined.List
                    ),
                    BottomNavigationItem(
                        title = "Home",
                        selectedIcon = Icons.Filled.Home,
                        unselectedIcon = Icons.Outlined.Home
                    ),
                    BottomNavigationItem(
                        title = "Settings",
                        selectedIcon = Icons.Filled.Settings,
                        unselectedIcon = Icons.Outlined.Settings
                    )
                )
                var selectedItemIndex by rememberSaveable {
                    mutableStateOf(1)
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp()
                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                items.forEachIndexed { index, item ->
                                    NavigationBarItem(
                                        selected = selectedItemIndex == index,
                                        onClick = {
                                            selectedItemIndex = index
                                            //  navController.navigate(item.title)
                                        },
                                        label = {
                                                Text(text = item.title)
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = if (index == selectedItemIndex) {
                                                    item.selectedIcon
                                                } else item.unselectedIcon,
                                                contentDescription = item.title
                                            )

                                        })
                                }
                            }
                        }
                    ) {

                    }
                }
            }
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "HomePage") {
        composable("HomePage") {
            HomePage(navController)
        }
        composable("DetailsPage") {
            DetailPage(navController)
        }
        composable("MyListPage") {
            MyListPage(navController)
        }
    }
}

@Composable
fun HomePage(navController: NavHostController) {
    Surface {
        Column {
            Text(text = "HomePage")
            Button(
                onClick = { navController.navigate("DetailsPage") },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("View Details")
            }

            Button(
                onClick = { navController.navigate("MyListPage") },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("My List")
            }
        }
    }

}

@Composable
fun DetailPage(navController: NavHostController) {
    Surface {
        Column {
            Text(text = "Details")
            Button(
                onClick = { navController.navigate("HomePage") },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Home")
            }
        }
    }

}

@Composable
fun MyListPage(navController: NavHostController) {
    Surface {
        Column {
            Text(text = "My List")
            Button(
                onClick = { navController.navigate("HomePage") },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Home")
            }
        }
    }
}

@Composable
fun RecentlyWatchedPage(navController: NavHostController) {

}

@Composable
fun RecommendedForYouPage(navController: NavHostController) {

}

@Composable
fun NewsPage(navController: NavHostController) {

}

@Composable
fun SettingsPage(navController: NavHostController) {

}
