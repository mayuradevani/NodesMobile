package app.brainpool.nodesmobile.data.models

import com.google.gson.annotations.SerializedName


data class AppNotification(
    @SerializedName("propertyId")
    val propertyId: String,
    @SerializedName("mapFileName")
    val mapFileName: String,
    @SerializedName("mapId")
    val mapId: String,
//    @SerializedName("fileUrl")
//    val mapId: String,
    @SerializedName("channelId")
    val applicationid: String
)
