package at.wautschaar.raterightlight.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.wautschaar.raterightlight.model.TV
import at.wautschaar.raterightlight.network.APIMDB
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TvViewModel : ViewModel() {
    private val _tv = MutableStateFlow<TV?>(null)
    val tv: StateFlow<TV?> = _tv

    fun getTvByID(tvId: String) {
        viewModelScope.launch {
            try {
                val tvResponse = APIMDB.retrofitService.getTvByID(tvId)
                val tv = TV(
                    id = tvResponse.id,
                    original_language = tvResponse.original_language,
                    original_name = tvResponse.original_name,
                    overview = tvResponse.overview,
                    poster_path = tvResponse.poster_path,
                    first_air_date = tvResponse.first_air_date
                )
                _tv.value = tv
            } catch (e: Exception) {
                println("Fehler beim Film Fetch! Viewmodel: ${e.message}")
            }
        }
    }
}