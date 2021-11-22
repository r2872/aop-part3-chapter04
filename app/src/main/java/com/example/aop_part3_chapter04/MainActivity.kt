package com.example.aop_part3_chapter04

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.example.aop_part3_chapter04.adapter.BookAdapter
import com.example.aop_part3_chapter04.adapter.HistoryAdapter
import com.example.aop_part3_chapter04.api.BookService
import com.example.aop_part3_chapter04.databinding.ActivityMainBinding
import com.example.aop_part3_chapter04.model.BestSellerDto
import com.example.aop_part3_chapter04.model.History
import com.example.aop_part3_chapter04.model.SearchBookDto
import com.example.aop_part3_chapter04.web.ServerAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: BookAdapter
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var retrofit: Retrofit
    private lateinit var bookService: BookService
    private lateinit var db: AppDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()
        initHistoryRecyclerView()

        db = getAppDatabase(this)

        retrofit = ServerAPI.getRetrofit(this)

        bookService = retrofit.create(BookService::class.java)

        bookService.getBestSellerBooks(getString(R.string.interParkAPIKey))
            .enqueue(object : Callback<BestSellerDto> {

                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<BestSellerDto>,
                    response: Response<BestSellerDto>
                ) {

                    if (!response.isSuccessful) {
                        Log.e(TAG, "실패")
                        return
                    }
                    response.body()?.let {
                        Log.d(TAG, it.toString())
                        it.books.forEach { book ->
                            Log.d(TAG, book.toString())
                        }
                        adapter.submitList(it.books)
                    }

                    adapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<BestSellerDto>, t: Throwable) {

                    Log.e(TAG, t.toString())
                }
            })

        binding.searchEdt.addTextChangedListener {
            if (binding.searchEdt.text.isNotEmpty()) {
                binding.deleteTextImg.visibility = View.VISIBLE
            } else {
                binding.deleteTextImg.visibility = View.GONE
            }
        }

        binding.searchBtn.setOnClickListener {
            searchBook(binding.searchEdt.text.toString())
        }

        deleteText()
    }

    private fun deleteText() {
        binding.deleteTextImg.setOnClickListener {
            binding.searchEdt.text.clear()
        }
    }

    private fun initRecyclerView() {
        adapter = BookAdapter(itemClickedListener = {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("bookModel", it)
            startActivity(intent)
        })
        binding.bookRecyclerView.adapter = adapter
    }

    private fun initHistoryRecyclerView() {
        historyAdapter = HistoryAdapter(historyDeleteClickedListener = {
            deleteSearchKeyword(it)
        }, historyKeywordSearchClickedListener = {
            binding.searchEdt.setText(it)
            searchBook(it)
        })
        binding.historyRecyclerView.adapter = historyAdapter
        initSearchEdt()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initSearchEdt() {
        binding.searchEdt.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {
                searchBook(binding.searchEdt.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        binding.searchEdt.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                showHistoryView()
            }
            return@setOnTouchListener false
        }
    }

    private fun searchBook(inputText: String) {

        if (binding.searchEdt.text.isEmpty()) {
            Toast.makeText(this, "검색어를 입력 해 주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        bookService.getBookByName(getString(R.string.interParkAPIKey), inputText)
            .enqueue(object : Callback<SearchBookDto> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<SearchBookDto>,
                    response: Response<SearchBookDto>
                ) {

                    saveSearchKeyword(inputText)

                    if (!response.isSuccessful) {
                        Log.e(TAG, "실패")
                        return
                    }
                    response.body()?.let {
                        if (it.books.isEmpty()) {
                            Toast.makeText(this@MainActivity, "검색 결과가 없습니다.", Toast.LENGTH_SHORT)
                                .show()
                            return@let
                        }
                        it.books.forEach { book ->
                            Log.d(TAG, book.toString())
                        }
                        adapter.submitList(it.books)
                    }
                    hideHistoryView()
                    adapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<SearchBookDto>, t: Throwable) {
                    hideHistoryView()
                    Log.e(TAG, t.toString())
                }
            })
    }

    private fun showHistoryView() {
        Thread {
            val keywords = db.historyDao().getAll().reversed()
            keywords.forEach {
                Log.d(TAG, it.toString())
            }

            runOnUiThread {
                binding.historyRecyclerView.isVisible = true
                historyAdapter.submitList(keywords.orEmpty())
            }
        }.start()
    }

    private fun hideHistoryView() {
        binding.historyRecyclerView.isVisible = false
    }

    private fun saveSearchKeyword(inputText: String) {
        Thread {
            val inputHistory = History(null, inputText)
            val keywords = db.historyDao().getAll()
            for (i in keywords.indices) {
                if (keywords[i].keyword == inputHistory.keyword) {
                    return@Thread
                }
            }
            db.historyDao().insertHistory(inputHistory)
        }.start()
    }

    private fun deleteSearchKeyword(keyword: String) {
        Thread {
            db.historyDao().delete(keyword)
            showHistoryView()
        }.start()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}