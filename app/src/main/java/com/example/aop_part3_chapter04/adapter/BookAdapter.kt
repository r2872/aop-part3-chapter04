package com.example.aop_part3_chapter04.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aop_part3_chapter04.databinding.ItembookBinding
import com.example.aop_part3_chapter04.model.Book

class BookAdapter(private val itemClickedListener: (Book) -> Unit) :
    ListAdapter<Book, BookAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItembookBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bookModel: Book) {
            binding.titleTxt.text = bookModel.title
            binding.descriptionTxt.text = bookModel.description
            binding.root.setOnClickListener {
                itemClickedListener(bookModel)
            }

            Glide.with(binding.coverImageView.context)
                .load(bookModel.coverSmallUrl)
                .into(binding.coverImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItembookBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}