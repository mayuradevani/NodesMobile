package app.brainpool.nodesmobile.view.ui.map.adapter

import android.widget.ImageView
import android.widget.TextView
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.data.models.Images
import app.brainpool.nodesmobile.util.gone
import app.brainpool.nodesmobile.util.visible
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class ImagesAdapter(sampleData: MutableList<Images>, var isDeleteVisible: Boolean) :
    BaseQuickAdapter<Images, BaseViewHolder>(R.layout.item_image, sampleData) {

    override fun convert(helper: BaseViewHolder, item: Images) {
        try {
            if (isDeleteVisible)
                helper.getView<TextView>(R.id.iv_delete).visible()
            else
                helper.getView<TextView>(R.id.iv_delete).gone()
            val imageView = helper.getView<ImageView>(R.id.grid_image)
            if (item.isOnline) {
                val strImageURL = item.url
                if (!strImageURL.isEmpty() && !strImageURL.equals("null", ignoreCase = true)) {
                    imageView.visible()
                    Glide.with(context)
                        .load(strImageURL)
                        .into(imageView)
                } else {
                    imageView.setImageDrawable(null)
                    imageView.gone()
                }
            } else {
                imageView.setImageURI(item.uri)
            }
            helper.getView<ImageView>(R.id.iv_delete).setOnClickListener {
                remove(item)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
