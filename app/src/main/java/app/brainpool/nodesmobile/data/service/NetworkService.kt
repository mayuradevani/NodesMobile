package dubai.business.womencouncil.data.service

import dubai.business.womencouncil.data.models.LoginRS
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/*
Retrofit interface
 */

interface NetworkService {
    @FormUrlEncoded
    @POST("process_login")
    suspend fun login(
        @Field("email") username: String,
    ): LoginRS
}

