package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    // Configure client with generous 60s timeouts as recommended for Gemini AI models
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val mediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * Sends a chat prompt or study question to Gemini API with correct system context for Deepak's active mode.
     */
    suspend fun getStudyResponse(
        mode: String, // "Entrance" or "Course"
        history: List<Pair<String, String>>, // List of role to content pairs
        userPrompt: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "API Key not configured. Please add your GEMINI_API_KEY in the Secrets panel."
        }

        try {
            val systemInstructionText = getSystemInstructions(mode)

            // Construct Gemini request JSON body
            val requestJson = JSONObject()

            // contents list
            val contentsArray = JSONArray()

            // Prepend conversation history if present
            for (turn in history) {
                val turnObj = JSONObject()
                turnObj.put("role", if (turn.first == "user") "user" else "model")
                val partsArray = JSONArray()
                val partObj = JSONObject()
                partObj.put("text", turn.second)
                partsArray.put(partObj)
                turnObj.put("parts", partsArray)
                contentsArray.put(turnObj)
            }

            // Append current user prompt
            val currentUserTurn = JSONObject()
            currentUserTurn.put("role", "user")
            val currentParts = JSONArray()
            val currentPartObj = JSONObject()
            currentPartObj.put("text", userPrompt)
            currentParts.put(currentPartObj)
            currentUserTurn.put("parts", currentParts)
            contentsArray.put(currentUserTurn)

            requestJson.put("contents", contentsArray)

            // System instructions
            val systemInstObj = JSONObject()
            val systemParts = JSONArray()
            val systemPartObj = JSONObject()
            systemPartObj.put("text", systemInstructionText)
            systemParts.put(systemPartObj)
            systemInstObj.put("parts", systemParts)
            requestJson.put("systemInstruction", systemInstObj)

            // Configuration
            val genConfig = JSONObject()
            genConfig.put("temperature", 0.7)
            requestJson.put("generationConfig", genConfig)

            val requestBodyString = requestJson.toString()
            Log.d(TAG, "RequestBody: $requestBodyString")

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBodyString.toRequestBody(mediaType))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorString = response.body?.string() ?: ""
                    Log.e(TAG, "API call failed: status ${response.code}, body: $errorString")
                    return@withContext "API returned error: Code ${response.code}. Please ensure your API key is valid."
                }

                val responseString = response.body?.string() ?: ""
                Log.d(TAG, "ResponseBody: $responseString")

                val resObj = JSONObject(responseString)
                val candidates = resObj.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val contentObj = firstCandidate.optJSONObject("content")
                    if (contentObj != null) {
                        val partsArr = contentObj.optJSONArray("parts")
                        if (partsArr != null && partsArr.length() > 0) {
                            return@withContext partsArr.getJSONObject(0).optString("text", "Empty response text")
                        }
                    }
                }
                return@withContext "Main Padho App model se koi jawab nahi mila. Dobara koshish karein."
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during API call", e)
            return@withContext "Internet connection error ya internal technical glitch: ${e.localizedMessage ?: "Unknown error"}. Kripya check karein."
        }
    }

    /**
     * Generates a 5 MCQ quiz based on the subject and mode.
     */
    suspend fun generateQuizJson(
        mode: String,
        subject: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "ERROR_API_KEY_MISSING"
        }

        try {
            val systemInstructionText = getSystemInstructions(mode) + "\n\nCRITICAL: ALWAYS respond with a raw JSON array of 5 unique MCQs without any Markdown or surrounding text formatting."

            val prompt = """
                Generate a list of 5 premium, distinct multiple choice questions (MCQs) to test Deepak's understanding.
                Selected Mode: ${if (mode == "Entrance") "CG PRE-D.EL.ED ENTRANCE MODE" else "CG D.EL.ED COLLEGE COURSE MODE"}
                Selected Subject: $subject

                The output MUST be a valid JSON array of objects representing the MCQs. Every object MUST have the following structure:
                {
                  "question": "Clear, premium, challenging question in friendly Hinglish focusing on $subject",
                  "options": ["Option A string", "Option B string", "Option C string", "Option D string"],
                  "correct_answer": "The exact string representation of the correct answer, which must be match exactly one of the options",
                  "hint": "A short, helpful hint written in clear Hinglish to guide Deepak",
                  "explanation": "A complete, friendly explanation of why this selection is correct, written in clear Hinglish with bullet points"
                }

                Important Rules:
                - Output exactly 5 MCQs as a flat array.
                - Make options diverse and plausible.
                - Do not use any introductory, conversational, or concluding text. Just raw, structured JSON.
            """.trimIndent()

            val requestJson = JSONObject()
            val contentsArray = JSONArray()
            val userTurn = JSONObject()
            userTurn.put("role", "user")
            val partsArray = JSONArray()
            val partObj = JSONObject()
            partObj.put("text", prompt)
            partsArray.put(partObj)
            userTurn.put("parts", partsArray)
            contentsArray.put(userTurn)
            requestJson.put("contents", contentsArray)

            // System instructions
            val systemInstObj = JSONObject()
            val systemParts = JSONArray()
            val systemPartObj = JSONObject()
            systemPartObj.put("text", systemInstructionText)
            systemParts.put(systemPartObj)
            systemInstObj.put("parts", systemParts)
            requestJson.put("systemInstruction", systemInstObj)

            // Configuration (request JSON format explicitly)
            val genConfig = JSONObject()
            genConfig.put("temperature", 0.9)
            genConfig.put("responseMimeType", "application/json")
            requestJson.put("generationConfig", genConfig)

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestJson.toString().toRequestBody(mediaType))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorString = response.body?.string() ?: ""
                    Log.e(TAG, "Quiz generation failure: code ${response.code}, body: $errorString")
                    return@withContext "ERROR_HTTP_${response.code}"
                }

                val responseString = response.body?.string() ?: ""
                val resObj = JSONObject(responseString)
                val candidates = resObj.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val contentObj = firstCandidate.optJSONObject("content")
                    if (contentObj != null) {
                        val partsArr = contentObj.optJSONArray("parts")
                        if (partsArr != null && partsArr.length() > 0) {
                            val text = partsArr.getJSONObject(0).optString("text", "")
                            return@withContext text
                        }
                    }
                }
                return@withContext "ERROR_EMPTY_RESPONSE"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Quiz exception", e)
            return@withContext "ERROR_EXCEPTION_${e.localizedMessage ?: "Unknown"}"
        }
    }

    private fun getSystemInstructions(mode: String): String {
        return """
            You are the core AI brain of "Padho App (Made by Deepak)", a premium personal study-companion app designed to help Deepak study effectively.
            Deepak is currently studying in: ${if (mode == "Entrance") "CG PRE-D.EL.ED ENTRANCE MODE" else "CG D.EL.ED COLLEGE COURSE MODE"}

            You MUST strictly enforce these instructions and bounds. There are no exceptions!

            ==================================================
            MODE 1: CG PRE-D.EL.ED ENTRANCE MODE
            ==================================================
            - Goal: Help Deepak study and pass the Entrance exam to gain admission into the D.El.Ed course.
            - Subjects allowed:
              1. General Knowledge (CG GK / Chhattisgarh GK & India GK)
              2. Mental Ability (Reasoning, general series, coding-decoding, patterns etc.)
              3. Teaching Aptitude (Shikshan Abhiruchi, primary kid handling, classroom situations)
              4. General Hindi Grammar (Vyakaran, vilom, paryayvachi, sandhi, samas)
              5. General English Grammar (Tense, voice, preposition, synonyms, antonyms)
            - strict restriction: If Deepak asks any core D.El.Ed college syllabus course questions here (like Child Development/Bal Vikas, SCERT curriculum, lesson planning, specific college year theories, pedagogy), refuse politely and guide him to switch the app to "Course Mode".

            ==================================================
            MODE 2: CG D.EL.ED COLLEGE COURSE MODE
            ==================================================
            - Goal: Help Deepak study for his 1st and 2nd year college curriculum exams.
            - Subjects allowed:
              1. Child Development (Bal Vikas - Piaget, Vygotsky, Kohlberg theories, emotional development)
              2. Pedagogy (Shikshan Shastra - how children think, learning paradigms)
              3. Lesson Planning (Path Yojna - black-board plan, microteaching, SCERT lesson templates)
              4. SCERT Textbook curriculum (D.El.Ed college syllabus, student evaluation)
            - strict restriction: If Deepak asks general knowledge, general reasoning/mental ability, history of India, standard school English/Hindi grammar questions here, refuse politely and suggest switching to "Entrance Mode".

            ==================================================
            GENERAL RULES FOR BOTH MODES (CRITICAL):
            ==================================================
            1. Language: ALWAYS reply in a friendly, clear, helpful, motivating Hinglish language (a natural blend of Hindi and English written in Roman script). Use standard, easily readable Indian terminology (e.g., "padhai", "exam", "syllabus", "tayyari").
            2. Formatting: ALWAYS format your responses with structured bullet points, clear spacing, and clean headers. Avoid huge unreadable walls of text to make studying enjoyable.
            3. Guardrail: If Deepak asks anything outside these exam domains (e.g., Bollywood movies, actors, pop culture, general coding, app development, cooking recipes, general news, gaming, commercial products), you MUST reply STRICTLY with the exact following wording:
               "Main Padho App hoon, aur main sirf CG D.El.Ed Entrance aur Course se jude sawal hal kar sakta hoon. Kripya apni padhai par focus karein!"
               Do not alter or translate this response.

            Remember: Keep Deepak motivated, focused, and provide premium-quality educational knowledge to help him shine in his CG D.El.Ed journey!
        """.trimIndent()
    }
}
