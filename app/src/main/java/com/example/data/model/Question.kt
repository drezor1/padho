package com.example.data.model

import org.json.JSONArray
import org.json.JSONObject

data class Question(
    val question: String,
    val options: List<String>,
    val correct_answer: String,
    val hint: String,
    val explanation: String
) {
    companion object {
        fun fromJsonString(jsonString: String): List<Question> {
            val list = mutableListOf<Question>()
            try {
                // Find first '[' and last ']' to safely bypass markdown wrappers like ```json
                val start = jsonString.indexOf('[')
                val end = jsonString.lastIndexOf(']')
                if (start == -1 || end == -1 || end < start) return emptyList()
                
                val cleanedJson = jsonString.substring(start, end + 1)
                val jsonArray = JSONArray(cleanedJson)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val optArray = obj.getJSONArray("options")
                    val opts = mutableListOf<String>()
                    for (j in 0 until optArray.length()) {
                        opts.add(optArray.getString(j))
                    }
                    list.add(
                        Question(
                            question = obj.getString("question"),
                            options = opts,
                            correct_answer = obj.getString("correct_answer"),
                            hint = obj.optString("hint", "No hint available."),
                            explanation = obj.optString("explanation", "No detailed explanation available.")
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return list
        }
    }
}
