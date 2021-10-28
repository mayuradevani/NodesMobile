package app.brainpool.nodesmobile.networking

import android.os.Looper
import com.apollographql.apollo.ApolloClient
import okhttp3.OkHttpClient

class NodesMobileApi {
    fun getApolloClient(): ApolloClient {
        check(Looper.myLooper() == Looper.getMainLooper()) {
            "Only in main thread can get the apolloClient instance"
        }
        val okHttpClient = OkHttpClient.Builder().build()
        return ApolloClient.builder()
            .serverUrl("http://34.126.80.190/graph-private")
            .okHttpClient(okHttpClient)
            .build()
    }
}