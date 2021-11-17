package com.example.aop_part3_chapter04

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.example.aop_part3_chapter04.api.BookService
import com.example.aop_part3_chapter04.databinding.ActivityMainBinding
import com.example.aop_part3_chapter04.model.BestSellerDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://book.interpark.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val bookService = retrofit.create(BookService::class.java)

        bookService.getBestSellerBooks(API_KEY).enqueue(object : Callback<BestSellerDto> {

            override fun onResponse(call: Call<BestSellerDto>, response: Response<BestSellerDto>) {

                if (!response.isSuccessful) {
                    Log.e(TAG, "실패")
                    return
                }
                response.body()?.let {
                    Log.d(TAG, it.toString())
                    it.books.forEach { book ->
                        Log.d(TAG, book.toString())
                    }
                }
            }

            override fun onFailure(call: Call<BestSellerDto>, t: Throwable) {

                Log.e(TAG, t.toString())
            }
        })
    }

    companion object {
        private const val API_KEY =
            "155EA1590C95EA7CD8D1F377DF2CE4C82EF607AB94B83914633F0EC05452776B"
        private const val TAG = "MainActivity"
    }
}