package app.brainpool.nodesmobile.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.util.GlobalVar.TAG
import app.brainpool.nodesmobile.view.state.ViewState
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.apollographql.apollo.exception.ApolloException
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.io.File

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

inline fun <reified T : Activity> Context.navigate() {
    startActivity(Intent(this, T::class.java))
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
            Log.d(TAG, "Response:${response.toString()}")
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

fun AppCompatActivity.materialDialog(
    message: String, title: String = "",
    positiveText: String, positiveClickListener: DialogCallback
): MaterialDialog? {
    this.let {
        return MaterialDialog(this).cornerRadius(16f)
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


inline fun <reified T : Activity> Context.navigateWithExtra(extra: Any) {
    if (extra is String) {
        startActivity(Intent(this, T::class.java).putExtra("EXTRA", extra))

    } else {
        startActivity(Intent(this, T::class.java).putExtra("EXTRA", extra.toJson() as String))
    }
}

inline fun <reified T : Activity> Fragment.navigateWithExtra(intent: Intent, extra: Any) {
    if (extra is String) {
        startActivity(intent.putExtra("EXTRA", extra))

    } else {
        startActivity(intent.putExtra("EXTRA", extra.toJson() as String))
    }
}

inline fun <reified T : Parcelable, reified K : Activity> Context.navigateWithParcebleList(
    extra: ArrayList<T>
) {
    startActivity(Intent(this, K::class.java).putParcelableArrayListExtra("EXTRA", extra))

}

inline fun <reified T : Parcelable> Fragment.navigateWithParcebleList(
    kClass: Class<out AppCompatActivity>,
    extra: ArrayList<T>
) {
    startActivity(
        Intent(requireContext(), kClass).putParcelableArrayListExtra(
            "EXTRA_PAR_LIST",
            extra
        )
    )

}

inline fun <reified T : Parcelable> Fragment.getIntenseParableList(
    kClass: Class<out AppCompatActivity>,
    extra: ArrayList<T>
) = Intent(requireContext(), kClass).putParcelableArrayListExtra("EXTRA_PAR_LIST", extra)


inline fun <reified T : Parcelable> Activity.getParcebleListExtra(): ArrayList<T>? {
    return try {
        intent.extras?.getParcelableArrayList<T>("EXTRA_PAR_LIST") as ArrayList<T>
    } catch (e: Exception) {
        null
    }
}


inline fun <reified T> Activity.getExtra(): T? {
    return try {
        intent.extras?.getString("EXTRA")?.fromJson<T>()
    } catch (e: Exception) {
        null
    }
}

inline fun <reified T> String.getList() =
    Gson().fromJson<List<T>>(this, object : TypeToken<List<T>>() {}.type)


fun Activity.getExtra(): String? {
    return try {
        intent.extras?.getString("EXTRA")
    } catch (e: Exception) {
        null
    }
}

fun Activity.getIntExtra(): Int? {
    return try {
        intent.extras?.getInt("EXTRA", -1)
    } catch (e: Exception) {
        null
    }
}


inline fun <reified T> Context.navigateClearStack() {
    startActivity(Intent(this, T::class.java)
        .apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
    if (this is Activity) {
        finish()
    }

}

fun Any.toJson() = Gson().toJson(this)
inline fun <reified T> String.fromJson(): T? = Gson().fromJson(this, T::class.java);

fun getAllImageFilesInAllFolder(mapDir: File): Any {
    var c = 0
    if (mapDir.list() != null)
        for (name in mapDir.list()) {
            val subDir = File(mapDir.path + "/$name")
            if (subDir.isDirectory) {
                for (name in subDir.list()) {
                    val subDir2 = File(subDir.path + "/$name")
                    if (subDir2.isDirectory) {
                        for (name in subDir2.list()) {
                            c++
                        }
                    }
                }
            }
        }
    return c
}

fun Activity.hasPermission(permission: String): Boolean {

    return ActivityCompat.checkSelfPermission(this, permission) ==
            PackageManager.PERMISSION_GRANTED
}

fun View.showSnackbar(
    view: View,
    msg: String,
    length: Int,
    actionMessage: CharSequence?,
    action: (View) -> Unit
) {
    val snackbar = Snackbar.make(view, msg, length)
    if (actionMessage != null) {
        snackbar.setAction(actionMessage) {
            action(this)
        }.show()
    } else {
        snackbar.show()
    }
}
