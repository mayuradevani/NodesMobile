package app.brainpool.nodesmobile.util

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

fun String.hasSpaces() = find { it == ' ' } != null

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


