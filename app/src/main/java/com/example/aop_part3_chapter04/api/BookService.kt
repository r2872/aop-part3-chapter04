package com.example.aop_part3_chapter04.api

import com.example.aop_part3_chapter04.model.BestSellerDto
import com.example.aop_part3_chapter04.model.SearchBookDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BookService {

    @GET("/api/search.api?output=json")
    fun getBookByName(
        @Query("key") apikey: String,
        @Query("query") keyword: String
    ): Call<SearchBookDto>

    @GET("/api/bestSeller.api?output=json&categoryId=100")
    fun getBestSellerBooks(
        @Query("key") apikey: String
    ): Call<BestSellerDto>
}