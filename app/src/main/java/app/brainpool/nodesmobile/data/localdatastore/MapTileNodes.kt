package app.brainpool.nodesmobile.data.localdatastore

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class MapTileNodes : RealmObject() {
    @PrimaryKey
    var id: Int = 0
    var mapId: String = ""
    lateinit var fName: String
    var link: String? = ""
    var isDownloaded: Boolean? =false
    override fun toString(): String {
        return mapId.toString()
    }
}