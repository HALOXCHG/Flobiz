package com.flobiz.challenge.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flobiz.challenge.db.DataRepository
import com.flobiz.challenge.models.Questions

class MainActivityViewModel(private val repository: DataRepository) : ViewModel() {

    var list = MutableLiveData<List<Questions>>()
    var page: Int = 1

    init {
        networkCall(page = page)
    }

    fun networkCall(
        order: String = "desc",
        sort: String = "activity",
        site: String = "stackoverflow",
        page: Int = 1,
        pagesize: Int = 30,
        tagged: String = "",
    ) = repository.fetchDataFromNetwork(order, sort, site, page, pagesize, tagged)

    fun getAll(): LiveData<List<Questions>> = repository.getList()

    fun insertData(list: List<Questions>) = repository.insertInDb(list)

    fun clearData() = repository.clearDatabase()

    override fun onCleared() {
        super.onCleared()
        repository.limitCache()
    }
}

class MainActivityViewModelFactory(private val repository: DataRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MainActivityViewModel(repository) as T
    }
}