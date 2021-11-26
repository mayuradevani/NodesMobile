package app.brainpool.nodesmobile.util

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.view.state.ViewState
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.apollographql.apollo.exception.ApolloException
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.inVisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
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
            }
            is ViewState.Success -> {
                body.invoke(it.value)
                loader?.gone()
            }
        }
    }
}


fun <T> ViewModel.doInBackground(
    liveData: MutableLiveData<ViewState<T>>,
    customError: String? = null,
    request: suspend () -> T
) {
    viewModelScope.launch {
        liveData.postValue(ViewState.Loading())
        try {
            val response = request.invoke()
            liveData.postValue(ViewState.Success(response))
        } catch (e: ApolloException) {
            Log.d("ApolloException", "Failure", e)
            liveData.postValue(ViewState.Error(e.message))
        }
    }
}

fun ImageView.loadImage(filePath: String, placeHolder: Int? = null) {

    val requestBuilder = Glide.with(this).load(filePath)
    if (placeHolder != null)
        requestBuilder.placeholder(placeHolder)
    else {
        requestBuilder.placeholder(R.drawable.place_holder)
    }
    if (placeHolder != null)
        requestBuilder.error(placeHolder)
    else {
        requestBuilder.error(R.drawable.place_holder)
    }
    requestBuilder.into(this)
}

fun Fragment.materialDialog(message: String, title: String = "") {
    MaterialDialog(requireContext()).cornerRadius(16f)
        .show {
            lifecycleOwner(this@materialDialog)
            if (title.isNotBlank()) {
                title(text = title)
            }
            message(text = message)
        }
}

fun Fragment.materialDialog(
    message: String, title: String = "",
    positiveText: String, positiveClickListener: DialogCallback
): MaterialDialog? {
    this.let {
        return MaterialDialog(requireContext()).cornerRadius(16f)
            .show {
                lifecycleOwner(this@materialDialog)
                if (title.isNotBlank()) {
                    title(text = title)
                }
                message(text = message)
                positiveButton(text = positiveText, click = positiveClickListener)
                noAutoDismiss()
            }

    }
    return null
}

fun Fragment.materialDialog(
    message: String, title: String = "",
    positiveText: String, positiveClickListener: DialogCallback,
    negativeText: String, negativeClickListener: DialogCallback
): MaterialDialog? {
    this.let {
        return MaterialDialog(requireContext()).cornerRadius(16f)
            .show {
                lifecycleOwner(this@materialDialog)
                if (title.isNotBlank()) {
                    title(text = title)
                }
                message(text = message)
                negativeButton(text = negativeText, click = negativeClickListener)
                positiveButton(text = positiveText, click = positiveClickListener)
                noAutoDismiss()
            }

    }
    return null
}

//android:fontFamily="sans-serif" // roboto regular
//android:fontFamily="sans-serif-light" // roboto light
//android:fontFamily="sans-serif-condensed" // roboto condensed
//android:fontFamily="sans-serif-thin" // roboto thin (android 4.2)
//android:fontFamily="sans-serif-medium"
