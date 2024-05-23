@file:OptIn(ExperimentalMaterial3Api::class)

package at.wautschaar.raterightlight;

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import at.wautschaar.raterightlight.model.Book
import at.wautschaar.raterightlight.ui.theme.RateRightLightTheme
import coil.compose.AsyncImage

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
            var selectedItemIndex by rememberSaveable { mutableIntStateOf(1) }

            RateRightLightTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        bottomBar = {
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
                            NavigationBar {
                                items.forEachIndexed { index, item ->
                                    NavigationBarItem(
                                        selected = selectedItemIndex == index,
                                        onClick = {
                                            selectedItemIndex = index
                                            item.onItemClick()
                                        },
                                        label = { Text(text = item.title) },
                                        icon = {
                                            Icon(
                                                imageVector = if (index == selectedItemIndex) {
                                                    item.selectedIcon
                                                } else item.unselectedIcon,
                                                contentDescription = item.title
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Destinations.HOME_ROUTE,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(Destinations.MY_LIST_ROUTE) { MyList() }
                            composable(Destinations.HOME_ROUTE) { Home(navController) }
                            composable(Destinations.SETTINGS_ROUTE) { Settings() }
                            composable("searchResult/{query}") { backStackEntry ->
                                val query = backStackEntry.arguments?.getString("query") ?: ""
                                SearchResultPage(query = query, navController = navController)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Home(navController: NavController) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Searchbar(navController)

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Home",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
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
fun Searchbar(navController: NavController) {
    val viewModel = viewModel<SearchViewModel>()
    val searchText by viewModel.searchText.collectAsState()
    val data by viewModel.data.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(Color.White)
    ) {
        Column {
            TextField(
                value = searchText,
                onValueChange = { newSearchText ->
                    viewModel.onSearchTextChange(newSearchText)
                    viewModel.fetchBooks(newSearchText)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(25.dp)),
                placeholder = {
                    Text(
                        text = "Search",
                        color = Color.Gray,
                        style = TextStyle(fontStyle = FontStyle.Italic)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchTextChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = Color.Gray
                            )
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    viewModel.fetchBooks(searchText)
                    navController.navigate("searchResult/${searchText}")
                })
            )

            if (isSearching) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    items(data) { item ->
                        Text(
                            text = "${item.title}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SearchResultPage(
    query: String,
    navController: NavController,
    viewModel: SearchViewModel = viewModel()
) {
    Log.d("SearchResult", "SearchResult composable loaded")
    LaunchedEffect(query) {
        viewModel.fetchBooks(query)
    }

    val searchResults by viewModel.searchResults.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$query") },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                }
            )
        },
        content = {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Spacer(modifier = Modifier.height(60.dp))
                }
                items(searchResults) { book ->
                    BookItem(book = book)
                }
            }
        }
    )
}



@Composable
fun BookItem(book: Book) {
    book.imageUrl?.let { Log.d("imageURL", it) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            AsyncImage(
                model = book.imageUrl,
                contentDescription = book.title,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.ic_launcher_foreground)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(text = book.title, style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = book.authors?.joinToString() ?: "unkown",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun Settings() {
    Log.d("Settings", "Settings composable loaded")
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

