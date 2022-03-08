package app.brainpool.nodesmobile.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.util.GlobalVar.TAG
import app.brainpool.nodesmobile.view.state.ViewState
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.apollographql.apollo.exception.ApolloException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.tapadoo.alerter.Alerter
import com.tapadoo.alerter.OnHideAlertListener
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.inVisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

suspend fun <T> Task<T>.await(): T? = suspendCoroutine { continuetion ->
    this.addOnCompleteListener {
        if (it.isSuccessful) {
            continuetion.resume(it.result)
        } else {
            continuetion.resume(null)
        }

    }
}

fun setNightModeOnOff(context: Context, s: String) {
    if (s == context.getString(R.string.on)) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    } else if (s == context.getString(R.string.off)) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}

inline fun <reified T : Activity> Context.navigate() {
    startActivity(Intent(this, T::class.java))
}

fun setupTheme(context: Context, theme: String): Context {
    var context: Context = context
    val res: Resources = context.getResources()
    var mode: Int = res.getConfiguration().uiMode
    when (theme) {
        context.getString(R.string.on) -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            mode = Configuration.UI_MODE_NIGHT_YES
        }
        context.getString(R.string.off) -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            mode = Configuration.UI_MODE_NIGHT_NO
        }
        else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
    val config = Configuration(res.getConfiguration())
    config.uiMode = mode
    if (Build.VERSION.SDK_INT >= 17) {
        context = context.createConfigurationContext(config)
    } else {
        res.updateConfiguration(config, res.getDisplayMetrics())
    }
    return context
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

fun Fragment.showAlert(message: String, title: String? = null, callback: (() -> Unit)? = null) {
    val alert = Alerter.create(requireActivity())
        .setText(message)
        .setBackgroundColorRes(R.color.grey_dark)
        .setTitle(title.orEmpty())
        .enableInfiniteDuration(false)
        .enableSwipeToDismiss()

    if (callback != null) {
        alert.setOnHideListener(OnHideAlertListener { callback.invoke() })
    }

    alert.show()

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