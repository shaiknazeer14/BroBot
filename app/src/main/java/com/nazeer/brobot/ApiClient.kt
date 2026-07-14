package com.nazeer.brobot
import android.util.Log
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import okhttp3.Callback
import java.util.concurrent.TimeUnit

class ApiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    companion object {
        const val TAG = "ApiClient"
        const val BASE_URL = "http://10.0.2.2:8000"
    }

    fun sendAudio(
        filePath: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val audioFile = File(filePath)

        if (!audioFile.exists()) {
            Log.e(TAG, "Audio file not found: $filePath")
            onError("Audio file not found")
            return
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "audio",
                audioFile.name,
                audioFile.asRequestBody("audio/mp4".toMediaType())
            )
            .build()

        val request = Request.Builder()
            .url("$BASE_URL/process-audio")
            .post(requestBody)
            .build()

        Log.d(TAG, "Sending audio to backend: $filePath")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Request failed: ${e.message}")
                onError("Network error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d(TAG, "Response: $responseBody")

                if (response.isSuccessful) {
                    onSuccess(responseBody ?: "No response")
                } else {
                    onError("Server error: ${response.code}")
                }
            }
        })
    }
}