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

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books

    fun getBookByID(bookId: String) {
        viewModelScope.launch {
            try {
                val bookResponse = APIBook.retrofitService.getBookByID(bookId)
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
                _book.value = book
            } catch (e: Exception) {
                println("Fehler beim Buch Fetch! Viewmodel: ${e.message}")
            }
        }
    }
}
