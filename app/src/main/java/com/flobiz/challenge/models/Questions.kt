package com.flobiz.challenge.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions_list")
data class Questions(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val question_id: Long,
    val title: String,
    val creation_date: String,
    val link: String,
    val view_count: Int,
    val tags: List<String>,
    val owner: Owner,
)
