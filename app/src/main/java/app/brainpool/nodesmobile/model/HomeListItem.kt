package app.brainpool.nodesmobile.model

data class HomeListItem(var title: String?, var icon: Int, var selected: Boolean) {
    constructor() : this(null, 0, false)
}

