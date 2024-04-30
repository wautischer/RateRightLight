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

    NavHost(navController, startDestination = "Screen1") {
        composable("Screen1") {
            Screen1(navController)
        }
        composable("Screen2") {
            Screen2(navController)
        }
    }
}

@Composable
fun Screen1(navController: NavHostController) {
    Surface {
        Column {
            Text(text = "Screen 1")
            Button(
                onClick = { navController.navigate("Screen2") },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Go to Screen 2")
            }
        }
    }
}

@Composable
fun Screen2(navController: NavHostController) {
    Surface {
        Column {
            Text(text = "Screen 2")
            Button(
                onClick = { navController.navigate("Screen1") },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Go to Screen 1")
            }
        }
    }
}
