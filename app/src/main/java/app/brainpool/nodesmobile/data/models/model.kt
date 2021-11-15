package dubai.business.womencouncil.data.models

import com.google.gson.annotations.SerializedName

open class BaseRs {
    @SerializedName("status")
    val status: String = ""

    @SerializedName("message")
    val message: String = ""

    @SerializedName("error")
    val error: String = ""
}

public data class UserRS(
    @SerializedName("city")
    var channel: String = "",
    @SerializedName("name")
    var city: String = "",
) : BaseRs() {

}

data class LoginRS(
    @SerializedName("user")
    val userRS: UserRS,
    @SerializedName("auth_key")
    val authKey: String
) : BaseRs()