package com.example.nutritionapp.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.nutritionapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit  // Add this import

class OtherFragment : Fragment(R.layout.fragment_other) {
    private lateinit var inputEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var responseTextView: TextView
    private lateinit var calorieSummaryTextView: TextView

    // Replace this with your actual API key
    private val OPENAI_API_KEY = "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"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputEditText = view.findViewById(R.id.input_edit_text)
        submitButton = view.findViewById(R.id.submit_button)
        responseTextView = view.findViewById(R.id.response_text_view)
        calorieSummaryTextView = view.findViewById(R.id.calories_summary_text)

        updateCalorieSummary()

        submitButton.setOnClickListener {
            val userInput = inputEditText.text.toString()
            if (userInput.isNotEmpty()) {
                makeApiCall(userInput)
                submitButton.isEnabled = false
            }
        }
    }

    private fun updateCalorieSummary() {
        val goal = 2500
        val food = 0
        val exercise = 0
        val remaining = goal - food + exercise

        calorieSummaryTextView.text = getString(
            R.string.calorie_summary,
            goal,
            food,
            exercise,
            remaining
        )
    }

    private fun makeApiCall(prompt: String) {
        // Updated OkHttpClient configuration
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val jsonObject = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
            put("temperature", 0.7)
        }

        val requestBody = jsonObject.toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $OPENAI_API_KEY")
            .header("Content-Type", "application/json")
            .post(requestBody)
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()

                    withContext(Dispatchers.Main) {
                        if (!response.isSuccessful || responseBody == null) {
                            responseTextView.setText(
                                getString(R.string.error_api_response, response.code)
                            )
                            return@withContext
                        }

                        try {
                            val jsonResponse = JSONObject(responseBody)
                            if (jsonResponse.has("error")) {
                                val errorMessage = jsonResponse.getJSONObject("error")
                                    .getString("message")
                                responseTextView.setText(
                                    getString(R.string.error_api_message, errorMessage)
                                )
                                return@withContext
                            }

                            val generatedText = jsonResponse
                                .getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content")

                            responseTextView.setText(generatedText)
                        } catch (e: Exception) {
                            responseTextView.setText(
                                getString(R.string.error_parsing, e.message)
                            )
                        }
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    responseTextView.setText(
                        getString(R.string.error_network, e.message)
                    )
                }
            } finally {
                withContext(Dispatchers.Main) {
                    submitButton.isEnabled = true
                }
            }
        }
    }
}