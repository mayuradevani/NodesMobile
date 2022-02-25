package app.brainpool.nodesmobile.view.custom

interface LoadingView {
    fun onStartLoading()
    fun onStopLoading(success: Boolean, message: String = "")
    fun onInit()

}