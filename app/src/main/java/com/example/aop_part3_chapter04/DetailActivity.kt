package com.example.aop_part3_chapter04

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.aop_part3_chapter04.databinding.ActivityDetailBinding
import com.example.aop_part3_chapter04.model.Book
import com.example.aop_part3_chapter04.model.Review

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var db: AppDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = getAppDatabase(this)

        val model = intent.getParcelableExtra<Book>("bookModel")

        binding.titleTxt.text = model?.title.orEmpty()
        binding.descriptionTxt.text = model?.description.orEmpty()

        Glide.with(this)
            .load(model?.coverSmallUrl.orEmpty())
            .into(binding.coverImg)

        Thread {
            val review = db.reviewDao().getOneReview(model?.id?.toInt() ?: 0) ?: return@Thread
            runOnUiThread {
                binding.reviewEdt.setText(review.review.orEmpty())
            }
        }.start()

        binding.saveBtn.setOnClickListener {
            Thread {
                db.reviewDao()
                    .saveReview(Review(model?.id?.toInt() ?: 0, binding.reviewEdt.text.toString()))
            }.start()
        }


    }
}