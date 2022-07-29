package com.flobiz.challenge.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.flobiz.challenge.R
import com.flobiz.challenge.databinding.ItemFakeAdCardBinding
import com.flobiz.challenge.databinding.ItemQuestionCardBinding
import com.flobiz.challenge.models.Questions
import java.text.SimpleDateFormat
import java.util.*

class MainAdapter(private val list: List<Any>, private val listener: OnClickResponse) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_AD = 0
    private val TYPE_QUESTION = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            TYPE_QUESTION -> ItemViewHolder(ItemQuestionCardBinding.inflate(LayoutInflater.from(
                parent.context), parent, false))
            TYPE_AD -> AdViewHolder(ItemFakeAdCardBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false))
            else -> AdViewHolder(ItemFakeAdCardBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false))
//            else -> ItemViewHolder(ItemQuestionCardBinding.inflate(LayoutInflater.from(
//                parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_QUESTION -> ItemViewHolder(ItemQuestionCardBinding.bind(holder.itemView)).setContent(
                list[position] as Questions)
            TYPE_AD -> {
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
//        return TYPE_QUESTION
        return if (position % 4 == 0)
            TYPE_AD
        else
            TYPE_QUESTION
    }

    inner class ItemViewHolder(binding: ItemQuestionCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val myBinding = binding

        @SuppressLint("SetTextI18n")
        fun setContent(questions: Questions) {
            myBinding.apply {
                questionCardView.setOnClickListener {
                    listener.onClick(myBinding, questions.link)
                }
                userQuestionTitle.text = questions.title
                userName.text = questions.owner.display_name
                questions.creation_date
                postDate.text = "Posted On: ${getDate(questions.creation_date)}"

                Glide.with(myBinding.root.context)
                    .load(questions.owner.profile_image)
                    .centerInside()
                    .fitCenter()
                    .circleCrop()
                    .error(R.drawable.ic_baseline_image_24)
                    .into(imageViewUserImage)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getDate(millis: String): String {
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = (millis.toLong() * 1000)
        return simpleDateFormat.format(calendar.time)
    }

    interface OnClickResponse {
        fun onClick(binding: ItemQuestionCardBinding, uri: String)
    }

    inner class AdViewHolder(binding: ItemFakeAdCardBinding) : RecyclerView.ViewHolder(binding.root)
}