package at.wautschaar.raterightlight.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.wautschaar.raterightlight.model.Movie
import at.wautschaar.raterightlight.network.APIMDB
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {
    private val _movie = MutableStateFlow<Movie?>(null)
    val movie: StateFlow<Movie?> = _movie

    fun getMovieByID(movieId: String) {
        viewModelScope.launch {
            try {
                val movieResponse = APIMDB.retrofitService.getMovieByID(movieId)
                val movie = Movie(
                        id = movieResponse.id,
                        original_language = movieResponse.original_language,
                        title = movieResponse.title,
                        overview = movieResponse.overview,
                        poster_path = movieResponse.poster_path,
                        release_date = movieResponse.release_date
                    )
                _movie.value = movie
            } catch (e: Exception) {
                println("Fehler beim Film Fetch! Viewmodel: ${e.message}")
            }
        }
    }
}