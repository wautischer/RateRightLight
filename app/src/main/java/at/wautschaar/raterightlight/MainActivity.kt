@file:OptIn(ExperimentalMaterial3Api::class)

package at.wautschaar.raterightlight;

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import at.wautschaar.raterightlight.model.Book
import at.wautschaar.raterightlight.network.API
import at.wautschaar.raterightlight.ui.theme.RateRightLightTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter

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
                }
            }
        }
    }
}

@Composable
fun BookCard(book: Book, modifier: Modifier = Modifier) {
    Card(modifier = Modifier) {
        Column {
            Text(text = book.title)
        }
    }
}

@Composable
fun BookList(books: List<Book>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = Modifier) {
        items(books) { tempBook ->
            BookCard(book = tempBook, modifier = Modifier)
        }
    }
}

@Composable
fun Searchbar() {
    val viewModel = viewModel<SearchViewModel>()
    val searchText by viewModel.searchText.collectAsState()
    val data by viewModel.data.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
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
                    .heightIn(min = 56.dp)
                    .padding(start = 16.dp, end = 8.dp),
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
                }

            )

            if(isSearching) {
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
                                .padding(vertical = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Home() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Searchbar()

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
            var bookListByName by remember {
                mutableStateOf<List<Book>>(emptyList())
            }
            LaunchedEffect(Unit) {
                val bookResponse = API.retrofitService.getBooks("berserk")
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
