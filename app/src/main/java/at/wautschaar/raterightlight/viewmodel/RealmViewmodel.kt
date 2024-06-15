package at.wautschaar.raterightlight.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.wautschaar.raterightlight.network.APIBook
import at.wautschaar.raterightlight.network.APIMDB
import at.wautschaar.raterightlight.realm.HistoryEntity
import at.wautschaar.raterightlight.realm.MyApp
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class RealmViewmodel : ViewModel() {

    private val realm = MyApp.realm

    val histories = realm
        .query<HistoryEntity>()
        .asFlow()
        .map { results ->
            results.list.toList()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    init {

    }

    @SuppressLint("NewApi")
    fun insertHistory(cType: String, cID: String) {
        viewModelScope.launch {
            try {
                val cTitle: String
                val cInfo: String
                val cImg: String

                when (cType) {
                    "book" -> {
                        val bookInfo = APIBook.retrofitService.getBookByID(cID).volumeInfo
                        cTitle = bookInfo.title
                        cInfo = bookInfo.authors?.getOrNull(0) ?: ""
                        cImg = bookInfo.imageLinks?.smallThumbnail.toString()
                    }
                    "movie" -> {
                        val movie = APIMDB.retrofitService.getMovieByID(cID)
                        cTitle = movie.title.toString()
                        cInfo = movie.release_date.toString()
                        cImg = movie.poster_path.toString()
                    }
                    "tv" -> {
                        val tv = APIMDB.retrofitService.getTvByID(cID)
                        cTitle = tv.original_name.toString()
                        cInfo = tv.first_air_date.toString()
                        cImg = tv.poster_path.toString()
                    }
                    else -> {
                        throw IllegalArgumentException("Unsupported content type: $cType")
                    }
                }

                realm.write {
                    val history = HistoryEntity().apply {
                        contentId = cID
                        contentTitle = cTitle
                        contentInfo = cInfo
                        contentTye = cType
                        contentImg = cImg
                        if (timestamp == null) {
                            timestamp = LocalDateTime.now()
                        }
                    }
                    copyToRealm(history, updatePolicy = UpdatePolicy.ALL)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteHistory(historyToDel: HistoryEntity) {
        viewModelScope.launch {
            realm.write {
                val h = historyToDel ?: return@write
                val h_latest = findLatest(h) ?: return@write
                delete(h_latest)
            }
        }
    }

    /* //region Test
    private fun testRealm() {
        viewModelScope.launch {
            realm.write {
                val history = HistoryEntity().apply {
                    contentType = "Book"
                    contentId = "slfnfih3"
                }
                val history2 = HistoryEntity().apply {
                    contentType = "Movie"
                    contentId = "saf32qs"
                }
                copyToRealm(history, updatePolicy = UpdatePolicy.ALL)
                copyToRealm(history2, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }
     */ //endregion
}
