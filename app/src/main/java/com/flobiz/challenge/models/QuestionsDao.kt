package com.flobiz.challenge.models

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flobiz.challenge.models.Questions

@Dao
interface QuestionsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuestions(list: List<Questions>)

    @Query("SELECT * FROM questions_list")
    fun getAllQuestions(): LiveData<List<Questions>>

    @Query("DELETE FROM questions_list")
    fun deleteAllQuestions()

    @Query("DELETE FROM questions_list where id NOT IN (SELECT id from questions_list ORDER BY id DESC LIMIT 50)")
    fun limitCache()
}