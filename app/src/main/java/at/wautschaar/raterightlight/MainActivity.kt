package at.wautschaar.raterightlight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import at.wautschaar.raterightlight.model.Book
import at.wautschaar.raterightlight.network.API
import at.wautschaar.raterightlight.ui.theme.RateRightLightTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RateRightLightTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var bookListByName by remember {
                        mutableStateOf<List<Book>>(emptyList())
                    }
                    LaunchedEffect(Unit) {
                        val bookResponse = API.retrofitService.getBooks("berserk-max-")
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
}

@Composable
fun BookList (books: List<Book>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = Modifier) {
        items(books) {tempBook ->
            BookCard(book = tempBook, modifier = Modifier)
        }
    }
}