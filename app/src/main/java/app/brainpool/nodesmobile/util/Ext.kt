package app.brainpool.nodesmobile.util

import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import app.brainpool.nodesmobile.R
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.bumptech.glide.Glide

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.inVisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun FragmentManager.switch(containerId: Int, newFrag: Fragment, tag: String) {

    var current = findFragmentByTag(tag)
    beginTransaction()
        .apply {

            //Hide the current fragment
            primaryNavigationFragment?.let { hide(it) }

            //Check if current fragment exists in fragmentManager
            if (current == null) {
                current = newFrag
                add(containerId, current!!, tag)
            } else {
                show(current!!)
            }
        }
        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        .setPrimaryNavigationFragment(current)
        .setReorderingAllowed(true)
        .commitNowAllowingStateLoss()
}

fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T?) -> Unit) =
    liveData.observe(this, androidx.lifecycle.Observer(body))

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
