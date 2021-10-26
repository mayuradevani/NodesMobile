package app.brainpool.nodesmobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.example.LoginMutation
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        launch {
            callApi()
        }
    }

    private suspend fun callApi() {
        val apolloClient = ApolloClient.builder()
            .serverUrl("http://34.126.80.190/graph-private")
            .build()

        val response =
            try {
                apolloClient.mutate(LoginMutation(email = "test@gmail.com")).await()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        val login = response?.data
        if (login == null || response.hasErrors()) {
            Log.v("TAG", "Error:" + response?.errors?.get(0)?.message)
        } else {
            Log.v("TAG", "Success")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    var job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
}