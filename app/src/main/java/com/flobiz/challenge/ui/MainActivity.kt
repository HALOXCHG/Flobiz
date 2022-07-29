package com.flobiz.challenge.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flobiz.challenge.*
import com.flobiz.challenge.databinding.ActivityMainBinding
import com.flobiz.challenge.databinding.ItemQuestionCardBinding
import com.flobiz.challenge.models.Questions
import com.google.android.material.bottomsheet.BottomSheetDialog


class MainActivity : AppCompatActivity(), MainAdapter.OnClickResponse {
    lateinit var binding: ActivityMainBinding

    //Initializing viewModel
    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModelFactory((application as BaseApplication).repository)
    }

    var list: ArrayList<Any> = ArrayList<Any>()
    var tagsList: ArrayList<String> = ArrayList<String>()
    var filterData = FilterData()
    var not_loading = true

    lateinit var adapter: MainAdapter
    lateinit var layoutManager: LinearLayoutManager

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView = binding.recyclerViewMain
        val progressBar = binding.progressBar
        val retryButton = binding.retryButton
        val errorMessage = binding.errorMessage
        val noInternetMessage = binding.noInternetMessage

        adapter = MainAdapter(list, this)
        layoutManager = LinearLayoutManager(this)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        progressBar.visibility = View.VISIBLE
        errorMessage.visibility = View.VISIBLE

        viewModel.getAll().observe(this, {
            progressBar.visibility = View.GONE
            errorMessage.visibility = View.GONE
            if (it.isEmpty()) {
                noInternetMessage.visibility = View.VISIBLE
                retryButton.visibility = View.VISIBLE
            } else {
                noInternetMessage.visibility = View.GONE
                retryButton.visibility = View.GONE
            }

            //Fetch Tags for Filters
            fetchTags(it)

            //Inserts fake advertisements/banners
            insertAds(filterData(it))
            adapter.notifyDataSetChanged()
            not_loading = true
        })

        binding.retryButton.setOnClickListener {
            if (checkForInternet(this)) {
                viewModel.networkCall(page = viewModel.page)
            } else {
                Toast.makeText(this, "Check you internet connection", Toast.LENGTH_SHORT).show()
            }
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (not_loading && layoutManager.findLastCompletelyVisibleItemPosition() >= list.size - 10) {
                    not_loading = false
                    viewModel.networkCall(page = ++viewModel.page)
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    @SuppressLint("ResourceType")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_filter -> {
                val dialog = BottomSheetDialog(this)
                dialog.dismissWithAnimation = true

                val view = layoutInflater.inflate(R.layout.filter_bottom_sheet, null)
                val btnClear = view.findViewById<Button>(R.id.clear_filter)
                val radioGroup = view.findViewById<RadioGroup>(R.id.radio_group_filters)

                var rId = 1
                tagsList.forEach {
                    val radioButton = RadioButton(this)
                    radioButton.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
                    radioButton.setPadding(20, 20, 20, 20)
                    radioButton.textSize = 16f
                    radioButton.text = it
                    radioButton.id = rId++
                    radioGroup.addView(radioButton)
                }

                radioGroup.check(filterData.id)

                radioGroup.setOnCheckedChangeListener { rd, checkedId ->
                    for (i in 0 until rd.childCount) {
                        val radioButton = rd.getChildAt(i) as RadioButton
                        if (radioButton.id == checkedId) {
                            filterData.id = radioButton.id
                            filterData.tag = radioButton.text.toString()

                            dialog.dismiss()
                            viewModel.page = 1
                            viewModel.clearData()
                            viewModel.networkCall(tagged = filterData.tag, page = viewModel.page)
                        }
                    }
                }


                btnClear.setOnClickListener {
                    filterData = FilterData()
                    dialog.dismiss()
                    viewModel.page = 1
                    viewModel.clearData()
                    viewModel.networkCall(tagged = filterData.tag, page = viewModel.page)
                }

                dialog.setCancelable(true)
                dialog.setContentView(view)
                dialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fetchTags(list: List<Questions>) {
        list.forEach { question: Questions ->
            tagsList.addAll(question.tags)
        }
        tagsList = ArrayList(tagsList.distinct())
    }

    private fun filterData(list: List<Questions>): List<Questions> {
        val arList = ArrayList<Questions>()
        return if (filterData.tag.isEmpty()) {
            list
        } else {
            list.forEach {
                if (it.tags.contains(filterData.tag))
                    arList.add(it)
            }
            arList.toList()
        }
    }

    private fun insertAds(it: List<Questions>) {
        list.clear()
        var i: Int = 0
        it.forEach { question: Questions ->
            if (i % 4 == 0) {
                list.add(i, "Insert fake ad")
            } else {
                list.add(i, question)
            }
            i++
        }
    }

    private fun checkForInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false

            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    override fun onClick(binding: ItemQuestionCardBinding, uri: String) {
        when (checkForInternet(this)) {
            true -> binding.root.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
            false -> Toast.makeText(this, "You are offline. Try again later", Toast.LENGTH_SHORT)
                .show()
        }
    }

    inner class FilterData(var id: Int = -1, var tag: String = "") {
    }
}


