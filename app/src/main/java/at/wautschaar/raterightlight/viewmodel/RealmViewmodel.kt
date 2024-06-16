package at.wautschaar.raterightlight.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.wautschaar.raterightlight.realm.HistoryEntity
import at.wautschaar.raterightlight.realm.ItemEntity
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
        //testRealm()
    }

    fun insertHistory(cType: String, cID: String) {
        viewModelScope.launch {
            realm.write {
                val history = HistoryEntity().apply {
                    contentType = cType
                    contentId = cID
                }
                copyToRealm(history, updatePolicy = UpdatePolicy.ALL)
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

    val myList = realm
        .query<ItemEntity>()
        .asFlow()
        .map { results ->
            results.list.toList()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    fun isItemInList(contentId: String, contentType: String): Boolean {
        return myList.value.any { it.contentId == contentId && it.contentType == contentType }
    }

    fun insertItem(cType: String, cID: String) {
        viewModelScope.launch {
            realm.write {
                val item = ItemEntity().apply {
                    contentType = cType
                    contentId = cID
                }
                copyToRealm(item, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }

    fun deleteItem(itemToDel: ItemEntity) {
        viewModelScope.launch {
            realm.write {
                val i = itemToDel ?: return@write
                val i_latest = findLatest(i) ?: return@write
                delete(i_latest)
            }
        }
    }

}
