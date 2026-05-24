package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiClient
import com.example.data.database.ChatMessageEntity
import com.example.data.database.QuizResultEntity
import com.example.data.database.StudyDatabase
import com.example.data.database.StudyNoteEntity
import com.example.data.repository.StudyRepository
import com.example.data.model.Question
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudyViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "StudyViewModel"
    private val repository: StudyRepository

    // Screen navigation
    private val _activeScreen = MutableStateFlow("Dashboard") // Dashboard, Chat, Quiz, Notes, History
    val activeScreen: StateFlow<String> = _activeScreen.asStateFlow()

    // Mode state: "Entrance" (CG PRE-D.EL.ED) or "Course" (CG D.EL.ED COLLEGE)
    private val _mode = MutableStateFlow("Entrance")
    val mode: StateFlow<String> = _mode.asStateFlow()

    // Reactive Chat Messages for CURRENT Mode observed directly from DB Flow
    val chatMessages: StateFlow<List<ChatMessageEntity>>

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // Quiz states
    private val _quizQuestions = MutableStateFlow<List<Question>>(emptyList())
    val quizQuestions: StateFlow<List<Question>> = _quizQuestions.asStateFlow()

    private val _isQuizLoading = MutableStateFlow(false)
    val isQuizLoading: StateFlow<Boolean> = _isQuizLoading.asStateFlow()

    private val _quizError = MutableStateFlow<String?>(null)
    val quizError: StateFlow<String?> = _quizError.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    private val _userAnswers = MutableStateFlow<List<String>>(emptyList())
    val userAnswers: StateFlow<List<String>> = _userAnswers.asStateFlow()

    private val _quizCompleted = MutableStateFlow(false)
    val quizCompleted: StateFlow<Boolean> = _quizCompleted.asStateFlow()

    private val _selectedQuizSubject = MutableStateFlow("")
    val selectedQuizSubject: StateFlow<String> = _selectedQuizSubject.asStateFlow()

    // Database statistics and note list
    val quizHistory: StateFlow<List<QuizResultEntity>>
    val studyNotes: StateFlow<List<StudyNoteEntity>>

    init {
        val database = StudyDatabase.getDatabase(application)
        repository = StudyRepository(database.studyDao())

        // Reactively load chats corresponding to the chosen study mode
        chatMessages = _mode.flatMapLatest { currentMode ->
            repository.getChatMessagesByMode(currentMode)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Reactively observe quiz archives and customized notes
        quizHistory = repository.allQuizResults.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        studyNotes = repository.allStudyNotes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun navigateTo(screen: String) {
        _activeScreen.value = screen
    }

    fun setMode(newMode: String) {
        if (_mode.value != newMode) {
            _mode.value = newMode
            // Stop active quiz if mode changes
            resetQuiz()
        }
    }

    /**
     * Send study message inside active chat mode. Handles DB storage and API call.
     */
    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val currentMode = _mode.value
        viewModelScope.launch {
            // 1. Insert user message to database
            val userMsg = ChatMessageEntity(mode = currentMode, role = "user", content = text)
            repository.insertChatMessage(userMsg)

            _isChatLoading.value = true

            try {
                // 2. Format history for API (last 6 messages to keep context short yet useful)
                val currentMessages = chatMessages.value
                val historyList = currentMessages.takeLast(6).map { Pair(it.role, it.content) }

                // 3. Make the API Call to Gemini
                val aiResponse = GeminiClient.getStudyResponse(currentMode, historyList, text)

                // 4. Insert AI response to database
                val modelMsg = ChatMessageEntity(mode = currentMode, role = "model", content = aiResponse)
                repository.insertChatMessage(modelMsg)
            } catch (e: Exception) {
                Log.e(TAG, "Chat sending error", e)
                val errMsg = ChatMessageEntity(
                    mode = currentMode,
                    role = "model",
                    content = "Maafi chahte hain Deepak, internet problem ki wajah se response nahi mil paya. Kripya bad me try karein."
                )
                repository.insertChatMessage(errMsg)
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    /**
     * Clear all chat conversations for the selected applet mode.
     */
    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearChatMessagesByMode(_mode.value)
        }
    }

    /**
     * Clear all saved quiz results.
     */
    fun clearQuizHistory() {
        viewModelScope.launch {
            repository.clearAllQuizResults()
        }
    }

    /**
     * Triggers study quiz generation through Gemini REST API.
     */
    fun generateStudyQuiz(subject: String) {
        if (subject.isBlank()) return
        resetQuiz()
        _selectedQuizSubject.value = subject
        _isQuizLoading.value = true
        _quizError.value = null
        _activeScreen.value = "Quiz"

        viewModelScope.launch {
            try {
                val rawQuizJson = GeminiClient.generateQuizJson(_mode.value, subject)
                if (rawQuizJson == "ERROR_API_KEY_MISSING") {
                    _quizError.value = "Gemini API key is missing. Please add it via Secrets panel."
                    _isQuizLoading.value = false
                    return@launch
                } else if (rawQuizJson.startsWith("ERROR_HTTP_")) {
                    _quizError.value = "Server returned error during Quiz generation. Please check your internet connection."
                    _isQuizLoading.value = false
                    return@launch
                } else if (rawQuizJson.startsWith("ERROR_EXCEPTION_")) {
                    _quizError.value = "AI model error: ${rawQuizJson.substringAfter("ERROR_EXCEPTION_")}"
                    _isQuizLoading.value = false
                    return@launch
                }

                val questions = Question.fromJsonString(rawQuizJson)
                if (questions.isEmpty()) {
                    _quizError.value = "Quiz JSON parse fail ho gaya layout change ke karan. Dobara 'Generate Quiz' button dabaayein!"
                } else {
                    _quizQuestions.value = questions
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error generating quiz", e)
                _quizError.value = "Quiz download problem: ${e.localizedMessage ?: "Check network connection"}"
            } finally {
                _isQuizLoading.value = false
            }
        }
    }

    /**
     * Save an official answer selection and update progress.
     */
    fun submitQuizAnswer(answer: String) {
        val currentQuestions = _quizQuestions.value
        val currentIndex = _currentQuestionIndex.value
        if (currentQuestions.isEmpty() || currentIndex >= currentQuestions.size) return

        val correctAnswer = currentQuestions[currentIndex].correct_answer
        val isCorrect = answer.trim().lowercase() == correctAnswer.trim().lowercase()

        val updatedAnswers = _userAnswers.value.toMutableList()
        updatedAnswers.add(answer)
        _userAnswers.value = updatedAnswers

        if (isCorrect) {
            _quizScore.value = _quizScore.value + 1
        }

        if (currentIndex + 1 >= currentQuestions.size) {
            // Quiz finished! Complete stats and store inside database
            _quizCompleted.value = true
            viewModelScope.launch {
                val finalScore = _quizScore.value
                val total = currentQuestions.size
                val quizRecord = QuizResultEntity(
                    mode = if (_mode.value == "Entrance") "Entrance" else "Course",
                    subject = _selectedQuizSubject.value,
                    score = finalScore,
                    totalQuestions = total
                )
                repository.insertQuizResult(quizRecord)
            }
        } else {
            // Forward to next index
            _currentQuestionIndex.value = currentIndex + 1
        }
    }

    fun restartQuiz() {
        val subject = _selectedQuizSubject.value
        if (subject.isNotEmpty()) {
            generateStudyQuiz(subject)
        }
    }

    fun resetQuiz() {
        _quizQuestions.value = emptyList()
        _currentQuestionIndex.value = 0
        _quizScore.value = 0
        _userAnswers.value = emptyList()
        _quizCompleted.value = false
        _quizError.value = null
    }

    /**
     * Bookmark high-quality custom questions/answers as offline study notes.
     */
    fun addStudyNote(subject: String, question: String, answer: String) {
        if (question.isBlank() || answer.isBlank()) return
        viewModelScope.launch {
            repository.insertStudyNote(
                StudyNoteEntity(
                    mode = _mode.value,
                    subject = subject,
                    question = question,
                    answer = answer
                )
            )
        }
    }

    fun deleteStudyNote(id: Int) {
        viewModelScope.launch {
            repository.deleteStudyNoteById(id)
        }
    }
}
