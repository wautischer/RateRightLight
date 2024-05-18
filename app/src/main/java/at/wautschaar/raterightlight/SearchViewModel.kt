package at.wautschaar.raterightlight

import android.util.Log
import at.wautschaar.raterightlight.model.Book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.wautschaar.raterightlight.network.API
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel: ViewModel(){
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _data = MutableStateFlow<List<Book>>(emptyList())

    val data = searchText
        .debounce(500L)
        .onEach { _isSearching.value = true }
        .combine(_data) { text, item ->
            if (text.isBlank()) {
                item
            } else {
                delay(250L)
                item.filter { it.doesMatchSearchQuery(text) }
            }
        }
        .onEach { _isSearching.value = false }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(1000),
            _data.value
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun fetchBooks(query: String) {
        viewModelScope.launch {
            try {
                val response = API.retrofitService.getBooks(query)
                _data.value = response.items.map { bookItem ->
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
                Log.d("fetchBooks", "Books fetched: $response")
            } catch (e: Exception) {
                Log.d("Search", "Something went wrong: ${e.message}")
            }
        }
    }
}