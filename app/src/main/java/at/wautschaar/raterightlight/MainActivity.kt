package at.wautschaar.raterightlight;

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import at.wautschaar.raterightlight.ui.theme.RateRightLightTheme

object Destinations {
    const val MY_LIST_ROUTE = "MyList"
    const val HOME_ROUTE = "Home"
    const val SETTINGS_ROUTE = "Settings"
}

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val onItemClick: () -> Unit
)

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val navController = rememberNavController()
            NavHost(navController, startDestination = Destinations.HOME_ROUTE) {
                composable(Destinations.MY_LIST_ROUTE) {
                    MyList()
                }
                composable(Destinations.HOME_ROUTE) {
                    Home()
                }
                composable(Destinations.SETTINGS_ROUTE) {
                    Settings()
                }
            }

            RateRightLightTheme {
                val items = listOf(
                    BottomNavigationItem(
                        title = "MyList",
                        selectedIcon = Icons.Filled.List,
                        unselectedIcon = Icons.Outlined.List,
                        onItemClick = {
                            navController.navigate(Destinations.MY_LIST_ROUTE)
                        }
                    ),
                    BottomNavigationItem(
                        title = "Home",
                        selectedIcon = Icons.Filled.Home,
                        unselectedIcon = Icons.Outlined.Home,
                        onItemClick = {
                            navController.navigate(Destinations.HOME_ROUTE)
                        }
                    ),
                    BottomNavigationItem(
                        title = "Settings",
                        selectedIcon = Icons.Filled.Settings,
                        unselectedIcon = Icons.Outlined.Settings,
                        onItemClick = {
                            navController.navigate(Destinations.SETTINGS_ROUTE)
                        }
                    )
                )
                var selectedItemIndex: Int by rememberSaveable {
                    mutableIntStateOf(1)
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                items.forEachIndexed { index, item ->
                                    NavigationBarItem(
                                        selected = selectedItemIndex == index,
                                        onClick = {
                                            selectedItemIndex = index
                                            when (index) {
                                                0 -> navController.navigate(Destinations.MY_LIST_ROUTE)
                                                1 -> navController.navigate(Destinations.HOME_ROUTE)
                                                2 -> navController.navigate(Destinations.SETTINGS_ROUTE)
                                            }
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
                    ) { innerPadding ->
                        Surface(modifier = Modifier.fillMaxSize()) {
                            when (selectedItemIndex) {
                                0 -> MyList()
                                1 -> Home()
                                2 -> Settings()
                            }
                        }
                    }

                    var bookListByName by remember {
                        mutableStateOf<List<Book>>(emptyList())
                    }
                    LaunchedEffect(Unit) {
                        val bookResponse = API.retrofitService.getBooks("Berserk")
                        bookListByName = bookResponse.items.map { bookItem ->
                            Book(
                                id = bookItem.id,
                                title = bookItem.volumeInfo.title,
                                authors = bookItem.volumeInfo.authors,
                                description = bookItem.volumeInfo.description,
                                publishedDate = bookItem.volumeInfo.publishedDate,
                                pageCount = bookItem.volumeInfo.pageCount,
                                categories = bookItem.volumeInfo.categories,
                                language = bookItem.volumeInfo.language,
                                imageUrl = bookItem.volumeInfo.imageLinks.toString()
                            )
                        }
                    }
                    BookList(books = bookListByName)
                }
            }
        }
    }
}

@Composable
fun BookCard (book: Book, modifier: Modifier = Modifier) {
    Card(modifier = Modifier) {
        Column {
            Text(text = book.title)
        }
    }
fun Home() {
    Log.d("Home", "Home composable loaded")
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Home")
        }
    }
}

@Composable
fun BookList (books: List<Book>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = Modifier) {
        items(books) {tempBook ->
            BookCard(book = tempBook, modifier = Modifier)
        }
fun MyList() {
    Log.d("MyList", "MyList composable loaded")
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "MyList")
        }
    }
}

@Composable
fun Settings() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Settings")
        }
    }
}


@Composable
fun Detail() {

}

@Composable
fun RecentlyWatched() {

}

@Composable
fun RecommendedForYou() {

}

@Composable
fun News() {

}
