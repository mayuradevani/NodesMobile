package app.brainpool.nodesmobile.networking

import android.content.Context
import android.os.Looper
import android.util.Log
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.util.GlobalVar
import com.apollographql.apollo.ApolloClient
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

class NodesMobileApi {
    fun getApolloClient(context: Context): ApolloClient {
        check(Looper.myLooper() == Looper.getMainLooper()) {
            "Only in main thread can get the apolloClient instance"
        }
        return ApolloClient.builder()
            .serverUrl("http://34.149.134.85/graph-private")//34.126.80.190
            .okHttpClient(
                OkHttpClient.Builder()
                    .addInterceptor(AuthorizationInterceptor(context))
                    .build()
            )
            .build()
    }
}

private class AuthorizationInterceptor(val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", Prefs.getString(PrefsKey.AUTH_KEY))
            .build()
        Log.d(GlobalVar.TAG, "Call:$request")
        return chain.proceed(request)
    }
}