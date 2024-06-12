@file:OptIn(ExperimentalMaterial3Api::class)

package at.wautschaar.raterightlight

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import at.wautschaar.raterightlight.model.Book
import at.wautschaar.raterightlight.model.Movie
import at.wautschaar.raterightlight.model.TV
import at.wautschaar.raterightlight.network.APIBook
import at.wautschaar.raterightlight.network.APIMDB
import at.wautschaar.raterightlight.ui.theme.RateRightLightTheme
import at.wautschaar.raterightlight.viewmodel.BookViewModel
import at.wautschaar.raterightlight.viewmodel.MovieViewModel
import at.wautschaar.raterightlight.viewmodel.SearchViewModel
import at.wautschaar.raterightlight.viewmodel.TvViewModel
import coil.compose.rememberAsyncImagePainter

private const val IMAGE_URL = "https://image.tmdb.org/t/p/original/"
private var trendingMovieList = emptyList<Movie>()
private var trendingTVList = emptyList<TV>()
private var trendingBookList = emptyList<Book>()

object Destinations {
    const val MY_LIST_ROUTE = "MyList"
    const val HOME_ROUTE = "Home"
    const val SETTINGS_ROUTE = "Settings"
    const val TRENDING_ROUTE = "TrendingPage"
    const val DETAILED_BOOK_VIEW = "DetailedBookView"
    const val DETAILED_TV_VIEW = "DetailedTvView"
    const val DETAILED_MOVIE_VIEW = "DetailedMovieView"
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

            var temptrendingMovieList by remember { mutableStateOf<List<Movie>>(emptyList()) }
            var temptrendingTVList by remember { mutableStateOf<List<TV>>(emptyList()) }
            var temptrendingBookList by remember { mutableStateOf<List<Book>>(emptyList()) }
            LaunchedEffect(Unit) {
                val trendingMovieResponse = APIMDB.retrofitService.getTrendingMovie()
                val trendingTVResponse = APIMDB.retrofitService.getTrendingTV()
                val trendingBookResponse = APIBook.retrofitService.getBooks("stephen&king")

                temptrendingMovieList = trendingMovieResponse.results.map { Movie ->
                    Movie(
                        id = Movie.id,
                        original_language = Movie.original_language,
                        title = Movie.title,
                        overview = Movie.overview,
                        poster_path = Movie.poster_path,
                        release_date = Movie.release_date
                    )
                }

                temptrendingTVList = trendingTVResponse.results.map { TV ->
                    TV(
                        id = TV.id,
                        original_language = TV.original_language,
                        original_name = TV.original_name,
                        overview = TV.overview,
                        poster_path = TV.poster_path,
                        first_air_date = TV.first_air_date
                    )
                }

                temptrendingBookList = trendingBookResponse.items.map { Book ->
                    Book(
                        id = Book.id,
                        title = Book.volumeInfo.title,
                        authors = Book.volumeInfo.authors,
                        publishedDate = Book.volumeInfo.publishedDate,
                        description = Book.volumeInfo.description,
                        pageCount = Book.volumeInfo.pageCount,
                        categories = Book.volumeInfo.categories,
                        language = Book.volumeInfo.language,
                        imageUrl = Book.volumeInfo.imageLinks?.thumbnail
                    )
                }

                trendingMovieList = temptrendingMovieList
                trendingTVList = temptrendingTVList
                trendingBookList = temptrendingBookList
            }

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
                                /*
                                BottomNavigationItem(
                                    title = "Settings",
                                    selectedIcon = Icons.Filled.Settings,
                                    unselectedIcon = Icons.Outlined.Settings,
                                    onItemClick = {
                                        navController.navigate(Destinations.SETTINGS_ROUTE)
                                    }
                                ),
                                 */
                                BottomNavigationItem(
                                    title = "Trending",
                                    selectedIcon = Icons.Filled.Notifications,
                                    unselectedIcon = Icons.Outlined.Notifications,
                                    onItemClick = {
                                        navController.navigate(Destinations.TRENDING_ROUTE)
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
                            composable(Destinations.MY_LIST_ROUTE) { MyList(navController) }
                            composable(Destinations.HOME_ROUTE) { Home(navController) }
                            //composable(Destinations.SETTINGS_ROUTE) { Settings() }
                            composable(Destinations.TRENDING_ROUTE) { TrendingPage(navController) }
                            composable("${Destinations.DETAILED_BOOK_VIEW}/{bookId}") { backStackEntry ->
                                val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                                DetailedBookView(bookId, navController)
                            }
                            composable("${Destinations.DETAILED_TV_VIEW}/{tvId}") { backStackEntry ->
                                val tvId = backStackEntry.arguments?.getString("tvId") ?: ""
                                DetailedTvView(tvId = tvId, navController = navController)
                            }
                            composable("${Destinations.DETAILED_MOVIE_VIEW}/{movieId}") { backStackEntry ->
                                val movieId = backStackEntry.arguments?.getString("movieId") ?: ""
                                DetailedMovieView(movieId = movieId, navController = navController)
                            }
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
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Searchbar(navController)

            Spacer(modifier = Modifier.height(16.dp))

            //MovieList(movies = trendingMovieList, navController = navController)
        }
    }
}

@Composable
fun MyList(navController: NavController) {
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

@OptIn(ExperimentalMaterial3Api::class)
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
            val imageUrl1 = "https://books.google.com/books/content?id="
            val imageUrl2 = "&printsec=frontcover&img=1&zoom=2&edge=curl&source=gbs_api"
            val painter = rememberAsyncImagePainter(model = imageUrl1 + book.id + imageUrl2)
            Image(
                painter = painter,
                contentDescription = book.title,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop
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
}

@Composable
fun TrendingPage(navController: NavController) {
    val context = LocalContext.current

    var selectedButton by rememberSaveable { mutableStateOf(loadSelectedButton(context)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { selectedButton = "Bücher" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedButton == "Bücher") Color.Black else Color.LightGray
                    ),
                    modifier = Modifier.width(120.dp)
                ) {
                    Text(
                        text = "Bücher",
                        color = if (selectedButton == "Bücher") Color.White else Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { selectedButton = "Filme"; },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedButton == "Filme") Color.Black else Color.LightGray
                    ),
                    modifier = Modifier.width(120.dp)
                ) {
                    Text(
                        text = "Filme",
                        color = if (selectedButton == "Filme") Color.White else Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { selectedButton = "Serien"; },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedButton == "Serien") Color.Black else Color.LightGray
                    ),
                    modifier = Modifier.width(120.dp)
                ) {
                    Text(
                        text = "Serien",
                        color = if (selectedButton == "Serien") Color.White else Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedButton) {
                "Bücher" -> {
                    BookList(books = trendingBookList, navController = navController)
                }

                "Filme" -> {
                    MovieList(movies = trendingMovieList, navController = navController)
                }

                "Serien" -> {
                    TVList(tvs = trendingTVList, navController = navController)
                }
            }
        }
    }
    LaunchedEffect(selectedButton) {
        saveSelectedButton(context, selectedButton)
    }
}

private const val PREFS_NAME = "com.example.myapp.PREFS_NAME"
private const val SELECTED_BUTTON_KEY = "SELECTED_BUTTON_KEY"

private fun loadSelectedButton(context: Context): String {
    val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return sharedPreferences.getString(SELECTED_BUTTON_KEY, "Bücher") ?: "Bücher"
}

private fun saveSelectedButton(context: Context, selectedButton: String) {
    val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    sharedPreferences.edit {
        putString(SELECTED_BUTTON_KEY, selectedButton)
        apply()
    }
}

@Composable
fun MovieList(movies: List<Movie>, modifier: Modifier = Modifier, navController: NavController) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.padding(8.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(movies) { movie ->
            MovieCard(
                movie = movie,
                navController = navController,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun MovieCard(movie: Movie, navController: NavController, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        shape = RoundedCornerShape(10.dp),
        onClick = {
            navController.navigate("${Destinations.DETAILED_MOVIE_VIEW}/${movie.id}")
        }
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            val painter = rememberAsyncImagePainter(model = IMAGE_URL + movie.poster_path)
            Image(
                painter = painter,
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.FillBounds
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (movie.title?.length!! > 15) movie.title.take(15) + "..." else movie.title.toString(),
                modifier = Modifier.padding(4.dp),
                Color.White
            )
            Text(text = " " + movie.release_date, color = Color.LightGray)
        }
    }
}

@Composable
fun TVList(tvs: List<TV>, modifier: Modifier = Modifier, navController: NavController) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.padding(8.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tvs) { tv ->
            TVCard(tv = tv, navController = navController, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun TVCard(tv: TV, navController: NavController, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        shape = RoundedCornerShape(10.dp),
        onClick = {
            navController.navigate("${Destinations.DETAILED_TV_VIEW}/${tv.id}")
        }
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            val painter = rememberAsyncImagePainter(model = IMAGE_URL + tv.poster_path)
            Image(
                painter = painter,
                contentDescription = tv.original_name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.FillBounds
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (tv.original_name?.length!! > 15) tv.original_name.take(15) + "..." else tv.original_name.toString(),
                modifier = Modifier.padding(4.dp),
                Color.White
            )
            Text(text = " " + tv.first_air_date, color = Color.LightGray)
        }
    }
}

@Composable
fun BookList(books: List<Book>, modifier: Modifier = Modifier, navController: NavController) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.padding(8.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(books) { book ->
            BookCard(book = book, navController = navController, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun BookCard(book: Book, navController: NavController, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        shape = RoundedCornerShape(10.dp),
        onClick = {
            navController.navigate("${Destinations.DETAILED_BOOK_VIEW}/${book.id}")
        }
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            val imageUrl1 = "https://books.google.com/books/content?id="
            val imageUrl2 = "&printsec=frontcover&img=1&zoom=2&edge=curl&source=gbs_api"
            val painter = rememberAsyncImagePainter(model = imageUrl1 + book.id + imageUrl2)
            Image(
                painter = painter,
                contentDescription = book.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.FillBounds
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (book.title.length > 15) book.title.take(15) + "..." else book.title,
                modifier = Modifier.padding(4.dp),
                Color.White
            )
            Text(text = " " + book.authors?.get(0).toString(), color = Color.LightGray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DetailedBookView(
    bookId: String,
    navController: NavController,
    viewModel: BookViewModel = viewModel()
) {
    val book by viewModel.book.collectAsState()

    LaunchedEffect(bookId) {
        viewModel.getBookByID(bookId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(book?.title.toString()) },
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp)
                    .padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    val imageUrl1 = "https://books.google.com/books/content?id="
                    val imageUrl2 = "&printsec=frontcover&img=1&zoom=3&edge=curl&source=gbs_api"
                    val painter =
                        rememberAsyncImagePainter(model = imageUrl1 + book?.id + imageUrl2)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(600.dp)
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = book?.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(25.dp)),
                            contentScale = ContentScale.FillBounds
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.padding(top = 10.dp))
                    Text(
                        text = book?.title.toString(),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    Text(text = "Autor: " + (book?.authors?.get(0) ?: "Kein(e) Autor(en) bekannt!"))
                }
                item {
                    Text(text = "Sprache: " + (book?.language ?: "Keine Sprache vorhanden!"))
                }
                item {
                    Text(
                        text = "Seitenanzahl: " + (book?.pageCount?.toString()
                            ?: "Keine Seitenanzahl vorhanden!")
                    )
                }
                item {
                    Text(
                        text = "Veröffentlichungsdatum: " + (book?.publishedDate
                            ?: "Kein Veröffentlichungsdatum vorhanden!")
                    )
                }
                item {
                    Text(
                        text = "Beschreibung",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                    val description = book?.description ?: "Keine Beschreibung vorhanden!"
                    val plainDescription = removeHtmlTags(description)
                    Text(text = plainDescription)
                }
                item {
                    AmazonButton(
                        bookTitle = book?.title.toString(),
                        author = book?.authors?.get(0).toString()
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DetailedTvView(
    tvId: String,
    navController: NavController,
    viewModel: TvViewModel = viewModel()
) {
    val tv by viewModel.tv.collectAsState()

    LaunchedEffect(tvId) {
        viewModel.getTvByID(tvId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tv?.original_name.toString()) },
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp)
                    .padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    val painter = rememberAsyncImagePainter(model = IMAGE_URL + tv?.poster_path)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(600.dp)
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = tv?.original_name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(25.dp)),
                            contentScale = ContentScale.FillBounds
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.padding(top = 10.dp))
                    Text(
                        text = tv?.original_name.toString(),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    Text(text = "Sprache: " + (tv?.original_language ?: "Keine Sprache vorhanden!"))
                }
                item {
                    //TODO: Länge bzw. Folgenanzahl
                }
                item {
                    Text(
                        text = "Veröffentlichungsdatum: " + (tv?.first_air_date
                            ?: "Kein Veröffentlichungsdatum vorhanden!")
                    )
                }
                item {
                    Text(
                        text = "Beschreibung",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                    val description = tv?.overview ?: "Keine Beschreibung vorhanden!"
                    val plainDescription = removeHtmlTags(description)
                    Text(text = plainDescription)
                }
                item {
                    AmazonButton(
                        bookTitle = tv?.original_name.toString(),
                        author = tv?.first_air_date.toString()
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DetailedMovieView(
    movieId: String,
    navController: NavController,
    viewModel: MovieViewModel = viewModel()
) {
    val movie by viewModel.movie.collectAsState()

    LaunchedEffect(movieId) {
        viewModel.getMovieByID(movieId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(movie?.title.toString()) },
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp)
                    .padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    val painter = rememberAsyncImagePainter(model = IMAGE_URL + movie?.poster_path)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(600.dp)
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = movie?.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(25.dp)),
                            contentScale = ContentScale.FillBounds
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.padding(top = 10.dp))
                    Text(
                        text = movie?.title.toString(),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    Text(
                        text = "Sprache: " + (movie?.original_language
                            ?: "Keine Sprache vorhanden!")
                    )
                }
                item {
                    //TODO: Länge bzw. Folgenanzahl
                }
                item {
                    Text(
                        text = "Veröffentlichungsdatum: " + (movie?.release_date
                            ?: "Kein Veröffentlichungsdatum vorhanden!")
                    )
                }
                item {
                    Text(
                        text = "Beschreibung",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                    val description = movie?.overview ?: "Keine Beschreibung vorhanden!"
                    val plainDescription = removeHtmlTags(description)
                    Text(text = plainDescription)
                }
                item {
                    AmazonButton(
                        bookTitle = movie?.title.toString(),
                        author = movie?.original_language.toString()
                    )
                }
            }
        }
    )
}

fun removeHtmlTags(htmlText: String): String {
    return Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY).toString()
}

@Composable
fun AmazonButton(bookTitle: String, author: String) {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { }

    Button(
        onClick = { launchAmazon(bookTitle, author, launcher) },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black
        )
    ) {
        Text("Kauf auf Amazon", color = Color.White)
    }
}

private fun launchAmazon(
    bookTitle: String,
    author: String,
    launcher: ActivityResultLauncher<Intent>
) {
    val searchQuery = "${Uri.encode(bookTitle)}+${Uri.encode(author)}"
    val amazonUrl = "https://www.amazon.com/s?k=$searchQuery"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(amazonUrl))
    launcher.launch(intent)
}

//region Movie/TV Test composable and Movie/TV/Book Card/List
@Composable
fun Test() {
    var movieList by remember {
        mutableStateOf<List<Movie>>(emptyList())
    }
    var tvList by remember {
        mutableStateOf<List<TV>>(emptyList())
    }
    var trendingList by remember {
        mutableStateOf<List<Movie>>(emptyList())
    }
    var trendingTVList by remember {
        mutableStateOf<List<TV>>(emptyList())
    }
    var bookbyidList by remember {
        mutableStateOf<List<Book>>(emptyList())
    }
    LaunchedEffect(Unit) {
        val movieResponse = APIMDB.retrofitService.getMovie("oppenheimer")
        val tvResponse = APIMDB.retrofitService.getTV("the boys")
        val trendingResponse = APIMDB.retrofitService.getTrendingMovie()
        val trendingTVResponse = APIMDB.retrofitService.getTrendingTV()

        movieList = movieResponse.results.map { Movie ->
            Movie(
                id = Movie.id,
                original_language = Movie.original_language,
                title = Movie.title,
                overview = Movie.overview,
                poster_path = Movie.poster_path,
                release_date = Movie.release_date
            )
        }
        tvList = tvResponse.results.map { TV ->
            TV(
                id = TV.id,
                original_language = TV.original_language,
                original_name = TV.original_name,
                overview = TV.overview,
                poster_path = TV.poster_path,
                first_air_date = TV.first_air_date
            )
        }
        trendingList = trendingResponse.results.map { Movie ->
            Movie(
                id = Movie.id,
                original_language = Movie.original_language,
                title = Movie.title,
                overview = Movie.overview,
                poster_path = Movie.poster_path,
                release_date = Movie.release_date
            )
        }
        trendingTVList = trendingTVResponse.results.map { TV ->
            TV(
                id = TV.id,
                original_language = TV.original_language,
                original_name = TV.original_name,
                overview = TV.overview,
                poster_path = TV.poster_path,
                first_air_date = TV.first_air_date
            )
        }

        val bookResponse = APIBook.retrofitService.getBookByID("uehtAAAAQBAJ")
        val book = Book(
            id = bookResponse.id,
            title = bookResponse.volumeInfo.title,
            authors = bookResponse.volumeInfo.authors,
            publishedDate = bookResponse.volumeInfo.publishedDate,
            description = bookResponse.volumeInfo.description,
            pageCount = bookResponse.volumeInfo.pageCount,
            categories = bookResponse.volumeInfo.categories,
            language = bookResponse.volumeInfo.language,
            imageUrl = bookResponse.volumeInfo.imageLinks?.thumbnail
        )
        //bookbyidList = listOf(book)
    }
    //BookList(books = bookbyidList, navController = navController)
    //MovieList(movies = movieList)
    //TVList(tvs = tvList)
    //MovieList(movies = trendingList)
    //TVList(tvs = trendingTVList)
}
//endregion