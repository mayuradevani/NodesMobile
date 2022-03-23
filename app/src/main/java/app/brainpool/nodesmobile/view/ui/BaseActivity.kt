package app.brainpool.nodesmobile.view.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.util.*
import app.brainpool.nodesmobile.view.state.ViewState
import com.google.firebase.messaging.FirebaseMessaging
import com.pixplicity.easyprefs.library.Prefs
import com.tapadoo.alerter.Alerter
import kotlinx.coroutines.launch

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base2)
    }

    var isLogout = false

    private fun handleError(state: ViewState<*>) {
        Alerter.hide()
        state.message?.let { showAlert(it) }
    }

    fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T?) -> Unit) =
        liveData.observe(this, androidx.lifecycle.Observer(body))

    fun <T : Any, L : LiveData<ViewState<T>>> LifecycleOwner.observeViewState(
        liveData: L,
        loader: View? = null,
        body: (T?) -> Unit
    ) {
        this.observe(liveData) {
            when (it) {
                is ViewState.Loading -> {
                    loader?.visible()
                }
                is ViewState.Error -> {
                    loader?.gone()
                    if (it.message?.contains("HTTP 401") == true) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            lifecycleScope.launch { logOut() }
                        }, 3000)
                    }
                    handleError(it)
                }
                is ViewState.Success -> {
                    body.invoke(it.value)
                    loader?.gone()
                }
            }
        }
    }

    private suspend fun logOut() {
        try {
            if (!isLogout) {
                isLogout = true
                FirebaseMessaging.getInstance().deleteToken().await()
                Prefs.clear()
                navigateClearStack<Splash>()
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}