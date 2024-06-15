package at.wautschaar.raterightlight.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.wautschaar.raterightlight.realm.HistoryEntity
import at.wautschaar.raterightlight.realm.MyApp
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
        testRealm()
    }

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
}