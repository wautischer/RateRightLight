package at.wautschaar.raterightlight.realm

import android.app.Application
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class MyApp : Application() {
    companion object {
        lateinit var realm: Realm
    }

    override fun onCreate() {
        super.onCreate()
        val config = RealmConfiguration.Builder(
            schema = setOf(
                HistoryEntity::class
            )
        )
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()

        realm = Realm.open(config)
    }
}
