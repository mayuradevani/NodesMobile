package app.brainpool.nodesmobile.view.ui.home.adapter

import android.graphics.Typeface
import android.widget.TextView
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.model.HomeListItem
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class HomeListAdapter(sampleData: MutableList<HomeListItem>) :
    BaseQuickAdapter<HomeListItem, BaseViewHolder>(R.layout.item_home, sampleData) {
    override fun convert(helper: BaseViewHolder, item: HomeListItem) {
        helper.setText(R.id.tvTitle, item.title)
        helper.setImageResource(R.id.ivIcon, item.icon)
        if (item.title == "MAP") {
        }
        helper.setText(R.id.tvTitle, item.title)
        if (item.selected) {
            helper.getView<TextView>(R.id.tvTitle).typeface = Typeface.DEFAULT_BOLD
        } else {
            helper.getView<TextView>(R.id.tvTitle).typeface = Typeface.DEFAULT
        }
    }
}
