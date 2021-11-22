package com.example.aop_part3_chapter04.web

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServerAPI {

    companion object {

        private val HOST_URL = "https://book.interpark.com"

        //        Retrofit 형태의 변수가 => OKHttpClient 처럼 실제 호출 담당.
//        레트로핏 객체는 -> 하나만 만들어두고 -> 여러 화면에서 고유해서 사용.
//        객체를 하나로 유지하자. => SingleTon 패턴 사용.
        private var retrofit: Retrofit? = null

        fun getRetrofit(context: Context): Retrofit {

            if (retrofit == null) {

                retrofit = Retrofit.Builder()
                    .baseUrl(HOST_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }

            return retrofit!!
        }
    }
}