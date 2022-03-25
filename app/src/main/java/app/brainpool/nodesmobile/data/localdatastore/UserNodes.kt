package app.brainpool.nodesmobile.data.localdatastore

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UserNodes : RealmObject() {
    @PrimaryKey
    var id: String=""
    var email: String = ""
    var firstname: String? = ""
    var lastname: String? = ""
    var isSuperadmin: Boolean? = false
    var licenseNumberId: String = ""
    var licenseNumberName: String = ""
    var role: String? = ""
    var defPropertyId: String? = ""
    var imei: String? = ""
    var timeInterval: Int? = 5
    var radius: Int? = 1
    override fun toString(): String {
        return firstname.toString()
    }
}