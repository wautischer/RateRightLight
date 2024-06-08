package at.wautschaar.raterightlight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.wautschaar.raterightlight.model.Book
import at.wautschaar.raterightlight.network.APIBook
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookViewModel : ViewModel() {
    private val _book = MutableStateFlow<Book?>(null)
    val book: StateFlow<Book?> = _book

    fun getBookByID(bookId: String) {
        viewModelScope.launch {
            try {
                val bookResponse = APIBook.retrofitService.getBookByID(bookId)
                val mappedBooks = bookResponse.items.map { bookItem ->
                    Book(
                        id = bookItem.id,
                        title = bookItem.volumeInfo.title,
                        authors = bookItem.volumeInfo.authors,
                        publishedDate = bookItem.volumeInfo.publishedDate,
                        description = bookItem.volumeInfo.description,
                        pageCount = bookItem.volumeInfo.pageCount,
                        categories = bookItem.volumeInfo.categories,
                        language = bookItem.volumeInfo.language,
                        imageUrl = bookItem.volumeInfo.imageLinks?.thumbnail
                    )
                }
                _book.value = mappedBooks.first()
            } catch (e: Exception) {
                println("Fehler beim Buch Fetch! Viewmodel: ${e.message}")
            }
        }
    }
}
