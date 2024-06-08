package at.wautschaar.raterightlight

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.wautschaar.raterightlight.model.Book
import at.wautschaar.raterightlight.network.APIBook
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel: ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Book>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _data = MutableStateFlow<List<Book>>(emptyList())

    val data = searchText
        .debounce(200L)
        .onEach { _isSearching.value = true }
        .combine(_data) { text, item ->
            if (text.isBlank()) {
                item
            } else {
                delay(100L)
                item.filter { it.doesMatchSearchQuery(text) }
            }
        }
        .onEach { _isSearching.value = false }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(500),
            _data.value
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
        _searchResults.value = emptyList()
        _data.value = emptyList()
    }


    fun fetchBooks(query: String) {
        Log.d("fetchBooks", "fetchBooks")
        viewModelScope.launch {
            try {
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
                _searchResults.value = newData
                _data.value = newData
                Log.d("fetchBooks", "Books fetched: $response")
            } catch (e: Exception) {
                Log.d("Search", "Something went wrong: ${e.message}")
            }
        }
    }
}