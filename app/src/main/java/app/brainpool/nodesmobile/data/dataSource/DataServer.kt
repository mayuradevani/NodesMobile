package dubai.business.womencouncil.data.dataSource

import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.model.HomeListItem

open class DataServer private constructor() {
    companion object {
        fun getHomeData(): MutableList<HomeListItem> {
            val list = ArrayList<HomeListItem>()
            list.add(HomeListItem("Temprature", R.drawable.ic_map, false))
            list.add(HomeListItem("Wind", R.drawable.ic_settings, false))
            list.add(HomeListItem("Cloud Cover", R.drawable.ic_notifications, false))
            list.add(HomeListItem("Visibility", R.drawable.ic_notes, false))
            list.add(HomeListItem("Humidity", R.drawable.ic_task, false))
            list.add(HomeListItem("Temprature", R.drawable.ic_map, false))
            list.add(HomeListItem("Wind", R.drawable.ic_settings, false))
            list.add(HomeListItem("Cloud Cover", R.drawable.ic_notifications, false))
            list.add(HomeListItem("Visibility", R.drawable.ic_notes, false))
            list.add(HomeListItem("Humidity", R.drawable.ic_task, false))
            return list
        }
    }
}