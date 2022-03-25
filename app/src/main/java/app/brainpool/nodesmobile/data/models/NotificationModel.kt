package app.brainpool.nodesmobile.data.models

import com.google.gson.annotations.SerializedName


data class AppNotification(
    @SerializedName("propertyId")
    val propertyId: String="",
    @SerializedName("mapFileName")
    val mapFileName: String="",
    @SerializedName("mapId")
    val mapId: String="",
    @SerializedName("mapFileUrl")
    val mapFileUrl: String="",
    @SerializedName("channelId")
    val channelId: String=""
)
