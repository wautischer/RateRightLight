package at.wautschaar.raterightlight;

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import at.wautschaar.raterightlight.ui.theme.RateRightLightTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RateRightLightTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp()
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
fun HomePage(navController: NavHostController){
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
fun DetailPage(navController: NavHostController){
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
fun MyListPage(navController: NavHostController){
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
fun RecentlyWatchedPage(navController: NavHostController){

}

@Composable
fun RecommendedForYouPage(navController: NavHostController){

}

@Composable
fun NewsPage(navController: NavHostController){

}

@Composable
fun SettingsPage(navController: NavHostController){

}
