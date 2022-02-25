package com.alcophony.app.ui.core

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import app.brainpool.nodesmobile.Splash
import app.brainpool.nodesmobile.util.await
import app.brainpool.nodesmobile.util.gone
import app.brainpool.nodesmobile.util.navigateClearStack
import app.brainpool.nodesmobile.util.showAlert
import app.brainpool.nodesmobile.view.state.ViewState
import com.google.firebase.messaging.FirebaseMessaging
import com.pixplicity.easyprefs.library.Prefs
import com.tapadoo.alerter.Alerter
import kotlinx.coroutines.launch

abstract class BaseFragment(@LayoutRes layout: Int) : Fragment(layout) {
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
                    loader?.gone()
                }
                is ViewState.Error -> {
                    loader?.gone()
                    if (it.message?.contains("HTTP 401") == true) {

                        Handler(Looper.getMainLooper()).postDelayed({
                            viewLifecycleOwner.lifecycleScope.launch { logOut() }
                        }, 3000)

//                        observeViewState(viewModel.logout) { response ->
//                            if (response?.data?.logoutUserData?.success == true) {
//                                try {
//                                    viewLifecycleOwner
//                                        .lifecycleScope
//                                        .launch {
//                                            FirebaseMessaging.getInstance().deleteToken().await()
//                                            Prefs.clear()
//                                            requireActivity().navigateClearStack<Splash>()
//                                            activity?.finish()
//                                        }
//
//                                } catch (e: Exception) {
//                                    e.printStackTrace()
//                                }
//                            }
//                        }
//                        viewModel.logout(requireContext())
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
        if (!isLogout) {
            isLogout = true
            FirebaseMessaging.getInstance().deleteToken().await()
            Prefs.clear()
            if (isAdded && isVisible) {
                activity?.navigateClearStack<Splash>()
                activity?.finish()
            }
        }
    }
}