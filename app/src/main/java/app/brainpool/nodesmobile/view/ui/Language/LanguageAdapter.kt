package app.brainpool.nodesmobile.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.brainpool.nodesmobile.LanguageCodeDataQuery
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.databinding.ItemLanguageBinding

class LanguageAdapter :
    ListAdapter<LanguageCodeDataQuery.GetAllLanguageCode, LanguageViewHolder>(CharacterDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val binding: ItemLanguageBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_language,
            parent,
            false
        )
        return LanguageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        holder.binding.language = getItem(position)
    }

}

class LanguageViewHolder(val binding: ItemLanguageBinding) :
    RecyclerView.ViewHolder(binding.root)

class CharacterDiffUtil : DiffUtil.ItemCallback<LanguageCodeDataQuery.GetAllLanguageCode>() {

    override fun areItemsTheSame(
        oldItem: LanguageCodeDataQuery.GetAllLanguageCode,
        newItem: LanguageCodeDataQuery.GetAllLanguageCode
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: LanguageCodeDataQuery.GetAllLanguageCode,
        newItem: LanguageCodeDataQuery.GetAllLanguageCode
    ): Boolean {
        return oldItem == newItem
    }

}