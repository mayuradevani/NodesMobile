package com.alcophony.app.ui.core

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import app.brainpool.nodesmobile.util.*
import app.brainpool.nodesmobile.view.state.ViewState
import app.brainpool.nodesmobile.view.ui.Splash
import app.brainpool.nodesmobile.view.ui.map.adapter.ImagesAdapter
import com.google.firebase.messaging.FirebaseMessaging
import com.pixplicity.easyprefs.library.Prefs
import com.tapadoo.alerter.Alerter
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.launch

abstract class BaseFragment(@LayoutRes layout: Int) : Fragment(layout) {

    var isLogout = false
    val compositeDisposable = CompositeDisposable()
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
                            viewLifecycleOwner.lifecycleScope.launch { logOut() }
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
                if (isAdded && isVisible) {
                    activity?.navigateClearStack<Splash>()
                    activity?.finish()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
        super.onPause()
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}