package com.nazeer.brobot

import android.content.Context
import android.util.Log
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.StorageService

class WakeWordDetector(
    private val context: Context,
    private val audioRecorder: AudioRecorder,
    private val apiClient: ApiClient
) : RecognitionListener {

    private var model: Model? = null
    private var speechService: SpeechService? = null

    // Prevent multiple recordings
    private var isRecording = false

    init {
        loadModel()
    }

    private fun loadModel() {
        StorageService.unpack(
            context,
            "model",
            "model",
            { loadedModel ->
                model = loadedModel
                Log.d("WakeWordDetector", "Model loaded successfully")
                startListening()
            },
            { exception ->
                Log.e(
                    "WakeWordDetector",
                    "Failed to load model: ${exception.message}"
                )
            }
        )
    }

    fun startListening() {
        if (isRecording) return

        try {
            if (model == null) {
                Log.e("WakeWordDetector", "Model not loaded yet.")
                return
            }

            speechService?.shutdown()

            val recognizer = Recognizer(model, 16000.0f)
            speechService = SpeechService(recognizer, 16000.0f)

            speechService?.startListening(this)

            Log.d("WakeWordDetector", "Listening started")

        } catch (e: Exception) {
            Log.e(
                "WakeWordDetector",
                "Failed to start listening: ${e.message}"
            )
        }
    }

    override fun onPartialResult(hypothesis: String?) {
        Log.d("WakeWordDetector", "Partial: $hypothesis")

        val text = extractText(hypothesis)

        if (!isRecording && text.contains("hey bro", ignoreCase = true)) {
            Log.d("WakeWordDetector", "Wake word detected (Partial)")
            onWakeWordDetected()
        }
    }

    override fun onResult(hypothesis: String?) {
        Log.d("WakeWordDetector", "Result: $hypothesis")

        val text = extractText(hypothesis)

        if (!isRecording && text.contains("hey bro", ignoreCase = true)) {
            Log.d("WakeWordDetector", "Wake word detected (Final)")
            onWakeWordDetected()
        }
    }

    override fun onFinalResult(hypothesis: String?) {
        Log.d("WakeWordDetector", "Final Result: $hypothesis")
    }

    override fun onError(exception: Exception?) {
        Log.e(
            "WakeWordDetector",
            "Recognition Error: ${exception?.message}"
        )

        if (!isRecording) {
            startListening()
        }
    }

    override fun onTimeout() {
        Log.d("WakeWordDetector", "Recognition Timeout")

        if (!isRecording) {
            startListening()
        }
    }

    /**
     * Called when wake word is detected.
     */
    private fun onWakeWordDetected() {

        if (isRecording) return

        isRecording = true

        Log.d("WakeWordDetector", "Hey Bro detected!")

        // Stop Vosk so microphone becomes free
        stopListening()

        // Start recording user's query
        audioRecorder.startRecording(

            onRecordingFinished = { filePath ->

                Log.d(
                    "WakeWordDetector",
                    "Recording finished: $filePath"
                )

                apiClient.sendAudio(

                    filePath = filePath,

                    onSuccess = { response ->

                        Log.d(
                            "WakeWordDetector",
                            "Backend Response: $response"
                        )

                        isRecording = false
                        startListening()
                    },

                    onError = { error ->

                        Log.e(
                            "WakeWordDetector",
                            "Backend Error: $error"
                        )

                        isRecording = false
                        startListening()
                    }
                )
            },

            onError = { error ->

                Log.e(
                    "WakeWordDetector",
                    "Recording Error: $error"
                )

                isRecording = false
                startListening()
            }
        )
    }

    fun stopListening() {

        speechService?.stop()
        speechService?.shutdown()
        speechService = null

        Log.d("WakeWordDetector", "Listening stopped")
    }

    /**
     * Release resources.
     * Call from Activity/Service onDestroy().
     */
    fun release() {

        stopListening()

        model?.close()
        model = null

        Log.d("WakeWordDetector", "Resources released")
    }

    /**
     * Extract recognized text from Vosk JSON.
     */
    private fun extractText(result: String?): String {

        if (result.isNullOrEmpty()) return ""

        return try {

            val json = JSONObject(result)

            when {
                json.has("partial") -> json.getString("partial")
                json.has("text") -> json.getString("text")
                else -> ""
            }

        } catch (e: Exception) {

            Log.e(
                "WakeWordDetector",
                "JSON Parse Error: ${e.message}"
            )

            ""
        }
    }
}