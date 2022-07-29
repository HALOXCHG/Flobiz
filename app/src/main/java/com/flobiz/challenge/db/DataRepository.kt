package com.flobiz.challenge.db

import androidx.lifecycle.LiveData
import com.flobiz.challenge.api.StackExchangeApi
import com.flobiz.challenge.models.StackExchangeResponse
import com.flobiz.challenge.models.Questions
import com.flobiz.challenge.models.QuestionsDao
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DataRepository(
    private val questionsDao: QuestionsDao,
    private val retrofit: StackExchangeApi,
) {

    fun getList(): LiveData<List<Questions>> = questionsDao.getAllQuestions()

    fun insertInDb(list: List<Questions>) = questionsDao.insertQuestions(list)

    fun limitCache() = questionsDao.limitCache()

    fun clearDatabase() = questionsDao.deleteAllQuestions()

    fun fetchDataFromNetwork(
        order: String = "desc",
        sort: String = "activity",
        site: String = "stackoverflow",
        page: Int = 1,
        pagesize: Int = 30,
        tagged: String = "",
    ) {
        val call = retrofit.getQuestions(order, sort, site, page, pagesize, tagged)
        call.enqueue(object : Callback<StackExchangeResponse> {
            override fun onResponse(
                call: Call<StackExchangeResponse>,
                response: Response<StackExchangeResponse>,
            ) {
                if (response.isSuccessful) {
                    insertInDb(response.body()?.items!!.toList())
                }
            }

            override fun onFailure(call: Call<StackExchangeResponse>, t: Throwable) {
                getList()
            }
        })
    }
}