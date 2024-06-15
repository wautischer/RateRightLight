package at.wautschaar.raterightlight.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.wautschaar.raterightlight.model.Book
import at.wautschaar.raterightlight.model.Movie
import at.wautschaar.raterightlight.model.TV
import at.wautschaar.raterightlight.network.APIBook
import at.wautschaar.raterightlight.network.APIMDB
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SearchFilter {
    BOOKS, MOVIES, TV_SHOWS
}
class SearchViewModel : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _searchFilter = MutableStateFlow(SearchFilter.BOOKS)
    val searchFilter = _searchFilter.asStateFlow()

    private val _bookSearchResults = MutableStateFlow<List<Book>>(emptyList())
    val bookSearchResults = _bookSearchResults.asStateFlow()

    private val _movieSearchResults = MutableStateFlow<List<Movie>>(emptyList())
    val movieSearchResults = _movieSearchResults.asStateFlow()

    private val _tvSearchResults = MutableStateFlow<List<TV>>(emptyList())
    val tvSearchResults = _tvSearchResults.asStateFlow()

    private val _bookData = MutableStateFlow<List<Book>>(emptyList())
    private val _movieData = MutableStateFlow<List<Movie>>(emptyList())
    private val _tvData = MutableStateFlow<List<TV>>(emptyList())

    val bookData = searchText
        .debounce(200L)
        .onEach { _isSearching.value = true }
        .combine(_bookData) { text, item ->
            if (text.isBlank()) {
                item
            } else {
                delay(100L)
                item.filter { it.title?.contains(text, ignoreCase = true) == true }
            }
        }
        .onEach { _isSearching.value = false }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(500),
            _bookData.value
        )

    val movieData = searchText
        .debounce(200L)
        .onEach { _isSearching.value = true }
        .combine(_movieData) { text, item ->
            if (text.isBlank()) {
                item
            } else {
                delay(100L)
                item.filter { it.title?.contains(text, ignoreCase = true) == true }
            }
        }
        .onEach { _isSearching.value = false }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(500),
            _movieData.value
        )

    val tvData = searchText
        .debounce(200L)
        .onEach { _isSearching.value = true }
        .combine(_tvData) { text, item ->
            if (text.isBlank()) {
                item
            } else {
                delay(100L)
                item.filter { it.original_name?.contains(text, ignoreCase = true) == true }
            }
        }
        .onEach { _isSearching.value = false }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(500),
            _tvData.value
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
        _bookSearchResults.value = emptyList()
        _bookData.value = emptyList()
        _movieSearchResults.value = emptyList()
        _movieData.value = emptyList()
        _tvSearchResults.value = emptyList()
        _tvData.value = emptyList()
    }

    fun onSearchFilterChange(filter: SearchFilter) {
        _searchFilter.value = filter
        Log.d("Filter", "Search filter changed to: ${_searchFilter.value}")
    }

    fun fetchBooks(query: String) {
        Log.d("fetchBooks", "fetchBooks with query: $query and filter: ${_searchFilter.value}")
        viewModelScope.launch {
            try {
                if (_searchFilter.value == SearchFilter.BOOKS) {
                    val response = APIBook.retrofitService.getBooks(query)
                    val newData = response.items.map { bookItem ->
                        Book(
                            id = bookItem.id,
                            title = bookItem.volumeInfo.title,
                            authors = bookItem.volumeInfo.authors,
                            publishedDate = bookItem.volumeInfo.publishedDate,
                            description = bookItem.volumeInfo.description,
                            pageCount = bookItem.volumeInfo.pageCount,
                            categories = bookItem.volumeInfo.categories,
                            language = bookItem.volumeInfo.language,
                            imageUrl = bookItem.volumeInfo.imageLinks?.thumbnail ?: ""
                        )
                    }
                    _bookSearchResults.value = newData
                    _bookData.value = newData
                    Log.d("fetchBooks", "Books fetched: $response")
                } else {
                    _bookSearchResults.value = emptyList()
                    _bookData.value = emptyList()
                }
            } catch (e: Exception) {
                Log.d("fetchBooks", "Something went wrong: ${e.message}")
            }
        }
    }


    fun fetchMovies(query: String) {
        Log.d("fetchMovies", "fetchMovies")
        viewModelScope.launch {
            try {
                if (_searchFilter.value == SearchFilter.MOVIES) {
                    val response = APIMDB.retrofitService.getMovie(query)
                    val newData = response.results.map { movieItem ->
                        Movie(
                            id = movieItem.id,
                            title = movieItem.title,
                            original_language = movieItem.original_language,
                            overview = movieItem.overview,
                            poster_path = movieItem.poster_path,
                            release_date = movieItem.release_date
                        )
                    }
                    _movieSearchResults.value = newData
                    _movieData.value = newData
                    Log.d("fetchMovies", "Movies fetched: $response")
                } else {
                    _movieSearchResults.value = emptyList()
                    _movieData.value = emptyList()
                }
            } catch (e: Exception) {
                Log.d("fetchMovies", "Something went wrong: ${e.message}")
            }
        }
    }


    fun fetchTVShows(query: String) {
        Log.d("fetchTVShows", "fetchTVShows")
        viewModelScope.launch {
            try {
                if (_searchFilter.value == SearchFilter.TV_SHOWS) {
                    val response = APIMDB.retrofitService.getTV(query)
                    val newData = response.results.map { tvItem ->
                        TV(
                            id = tvItem.id,
                            original_language = tvItem.original_language,
                            original_name = tvItem.original_name,
                            overview = tvItem.overview,
                            poster_path = tvItem.poster_path,
                            first_air_date = tvItem.first_air_date
                        )
                    }
                    _tvSearchResults.value = newData
                    _tvData.value = newData
                    Log.d("fetchTVShows", "TV shows fetched: $response")
                } else {
                    _tvSearchResults.value = emptyList()
                    _tvData.value = emptyList()
                }
            } catch (e: Exception) {
                Log.d("fetchTVShows", "Something went wrong: ${e.message}")
            }
        }
    }
}
