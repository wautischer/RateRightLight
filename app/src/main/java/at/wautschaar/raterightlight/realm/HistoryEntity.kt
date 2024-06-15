package at.wautschaar.raterightlight.realm

import android.annotation.SuppressLint
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.time.LocalDateTime

open class HistoryEntity : RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var contentType: String = ""
    var contentId: String = ""
    @Ignore
    @SuppressLint("NewApi")
    var timestamp: LocalDateTime = LocalDateTime.now()
}
