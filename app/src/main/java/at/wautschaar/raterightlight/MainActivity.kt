@file:OptIn(ExperimentalMaterial3Api::class)

package at.wautschaar.raterightlight;

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
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
import at.wautschaar.raterightlight.model.Movie
import at.wautschaar.raterightlight.model.TV
import at.wautschaar.raterightlight.network.APIBook
import at.wautschaar.raterightlight.network.APIMDB
import at.wautschaar.raterightlight.ui.theme.RateRightLightTheme
import coil.compose.AsyncImage
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
    const val TEST_ROUTE = "Test"
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
                        imageUrl = Book.volumeInfo.imageLinks.thumbnail
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
                                BottomNavigationItem(
                                    title = "Settings",
                                    selectedIcon = Icons.Filled.Settings,
                                    unselectedIcon = Icons.Outlined.Settings,
                                    onItemClick = {
                                        navController.navigate(Destinations.SETTINGS_ROUTE)
                                    }
                                ),
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
                            composable(Destinations.MY_LIST_ROUTE) { MyList() }
                            composable(Destinations.HOME_ROUTE) { Home(navController) }
                            composable(Destinations.SETTINGS_ROUTE) { Settings() }
                            composable(Destinations.TRENDING_ROUTE) { TrendingPage() }
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
fun TrendingPage() {
    var selectedButton by remember { mutableStateOf("Bücher") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
                    Text(text = "Bücher", color = if (selectedButton == "Bücher") Color.White else Color.Black)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { selectedButton = "Filme" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedButton == "Filme") Color.Black else Color.LightGray
                    ),
                    modifier = Modifier.width(120.dp)
                ) {
                    Text(text = "Filme", color = if (selectedButton == "Filme") Color.White else Color.Black)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { selectedButton = "Serien" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedButton == "Serien") Color.Black else Color.LightGray
                    ),
                    modifier = Modifier.width(120.dp)
                ) {
                    Text(text = "Serien", color = if (selectedButton == "Serien") Color.White else Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedButton) {
                "Bücher" -> {
                    BookList(books = trendingBookList)
                }
                "Filme" -> {
                    MovieList(movies = trendingMovieList)
                }
                "Serien" -> {
                    TVList(tvs = trendingTVList)
                }
            }
        }
    }
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
    }
    //MovieList(movies = movieList)
    //TVList(tvs = tvList)
    //MovieList(movies = trendingList)
    //TVList(tvs = trendingTVList)
}

@Composable
fun MovieList(movies: List<Movie>, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.padding(8.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(movies.size) { index ->
            MovieCard(movie = movies[index], modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun MovieCard(movie: Movie, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        shape = RoundedCornerShape(10.dp)
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
            Text(text = movie.title.toString(), modifier = Modifier.padding(4.dp), Color.White)
        }
    }
}

@Composable
fun TVList(tvs: List<TV>, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.padding(8.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tvs.size) { index ->
            TVCard(tv = tvs[index], modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun TVCard(tv: TV, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        shape = RoundedCornerShape(10.dp)
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
            Text(text = tv.original_name.toString(), modifier = Modifier.padding(4.dp), Color.White)
        }
    }
}

@Composable
fun BookList(books: List<Book>, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.padding(8.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(books.size) { index ->
            BookCard(book = books[index], modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun BookCard(book: Book, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            val image_url1 = "https://books.google.com/books/content?id="
            val image_url2 = "&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
            val painter = rememberAsyncImagePainter(model = image_url1 + book.id + image_url2)
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
            Text(text = book.title, modifier = Modifier.padding(4.dp), Color.White)
        }
    }
}
//endregion
