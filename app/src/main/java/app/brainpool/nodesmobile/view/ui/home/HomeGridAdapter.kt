package app.brainpool.nodesmobile.view.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.model.HomeListItem

class HomeGridAdapter(
    private val context: Context,
    private val sampleData: List<HomeListItem>
) :
    BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    override fun getCount(): Int {
        return sampleData.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View? {
        var convertView = convertView
        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.item_home, null)
        }
        imageView = convertView!!.findViewById(R.id.ivIcon)
        textView = convertView.findViewById(R.id.tvTitle)
        imageView.setImageResource(sampleData.get(position).icon)
        textView.text = sampleData.get(position).title
        return convertView
    }
}