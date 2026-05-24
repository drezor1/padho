package com.example.data.repository

import com.example.data.database.ChatMessageEntity
import com.example.data.database.QuizResultEntity
import com.example.data.database.StudyDao
import com.example.data.database.StudyNoteEntity
import kotlinx.coroutines.flow.Flow

class StudyRepository(private val studyDao: StudyDao) {

    val allQuizResults: Flow<List<QuizResultEntity>> = studyDao.getAllQuizResults()
    
    val allStudyNotes: Flow<List<StudyNoteEntity>> = studyDao.getAllStudyNotes()

    fun getChatMessagesByMode(mode: String): Flow<List<ChatMessageEntity>> {
        return studyDao.getChatMessagesByMode(mode)
    }

    suspend fun insertQuizResult(result: QuizResultEntity) {
        studyDao.insertQuizResult(result)
    }

    suspend fun clearAllQuizResults() {
        studyDao.clearAllQuizResults()
    }

    suspend fun insertStudyNote(note: StudyNoteEntity) {
        studyDao.insertStudyNote(note)
    }

    suspend fun deleteStudyNoteById(id: Int) {
        studyDao.deleteStudyNoteById(id)
    }

    suspend fun insertChatMessage(message: ChatMessageEntity) {
        studyDao.insertChatMessage(message)
    }

    suspend fun clearChatMessagesByMode(mode: String) {
        studyDao.clearChatMessagesByMode(mode)
    }
}
