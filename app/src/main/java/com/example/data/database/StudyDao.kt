package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {
    // Quiz Results
    @Query("SELECT * FROM quiz_results ORDER BY timestamp DESC")
    fun getAllQuizResults(): Flow<List<QuizResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizResult(result: QuizResultEntity)

    @Query("DELETE FROM quiz_results")
    suspend fun clearAllQuizResults()

    // Study Notes
    @Query("SELECT * FROM study_notes ORDER BY timestamp DESC")
    fun getAllStudyNotes(): Flow<List<StudyNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyNote(note: StudyNoteEntity)

    @Query("DELETE FROM study_notes WHERE id = :id")
    suspend fun deleteStudyNoteById(id: Int)

    // Chat Messages
    @Query("SELECT * FROM chat_messages WHERE mode = :mode ORDER BY timestamp ASC")
    fun getChatMessagesByMode(mode: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE mode = :mode")
    suspend fun clearChatMessagesByMode(mode: String)
}
