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
    private val apiClient: ApiClient,
    private val notificationHelper: NotificationHelper,
    private val volumeController: VolumeController
) : RecognitionListener {

    private var model: Model? = null
    private var speechService: SpeechService? = null
    private var isRecording = false

    companion object {
        const val TAG = "WakeWordDetector"
    }

    init {
        loadModel()
    }

    private fun loadModel() {
        StorageService.unpack(
            context, "model", "model",
            { loadedModel ->
                model = loadedModel
                Log.d(TAG, "Model loaded successfully")
                startListening()
            },
            { exception ->
                Log.e(TAG, "Failed to load model: ${exception.message}")
            }
        )
    }

    fun startListening() {
        if (isRecording) return
        try {
            if (model == null) {
                Log.e(TAG, "Model not loaded yet")
                return
            }
            speechService?.shutdown()
            val recognizer = Recognizer(model, 16000.0f)
            speechService = SpeechService(recognizer, 16000.0f)
            speechService?.startListening(this)
            Log.d(TAG, "Listening started")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start listening: ${e.message}")
        }
    }

    override fun onPartialResult(hypothesis: String?) {
        val text = extractText(hypothesis)
        if (!isRecording && text.contains("hey bro", ignoreCase = true)) {
            Log.d(TAG, "Wake word detected in partial")
            onWakeWordDetected()
        }
    }

    override fun onResult(hypothesis: String?) {
        Log.d(TAG, "Result: $hypothesis")
        val text = extractText(hypothesis)
        if (!isRecording && text.contains("hey bro", ignoreCase = true)) {
            Log.d(TAG, "Wake word detected in result")
            onWakeWordDetected()
        }
    }

    override fun onFinalResult(hypothesis: String?) {
        Log.d(TAG, "Final: $hypothesis")
    }

    override fun onError(exception: Exception?) {
        Log.e(TAG, "Error: ${exception?.message}")
        if (!isRecording) startListening()
    }

    override fun onTimeout() {
        Log.d(TAG, "Timeout — restarting")
        if (!isRecording) startListening()
    }

    private fun onWakeWordDetected() {
        if (isRecording) return
        isRecording = true
        Log.d(TAG, "Hey Bro detected! Starting recording...")

        // Show listening notification
        notificationHelper.showListening()

        stopListening()

        // Show recording notification
        notificationHelper.showRecording()

        audioRecorder.startRecording(
            onRecordingFinished = { filePath ->
                Log.d(TAG, "Recording finished: $filePath")

                // Check if it's a volume command first — no backend needed
                val lastTranscript = filePath // placeholder — will be filled after Whisper

                // Show processing notification
                notificationHelper.showProcessing()

                apiClient.sendAudio(
                    filePath = filePath,
                    onSuccess = { response ->
                        Log.d(TAG, "Backend response: $response")

                        // Parse response and check for volume command
                        try {
                            val json = org.json.JSONObject(response)
                            val note = json.optString("note", "")
                            val message = json.optString("message", "Done!")

                            // Check if it's a volume command
                            if (volumeController.handleVolumeCommand(note)) {
                                notificationHelper.showSuccess("Volume adjusted!", note)
                            } else {
                                notificationHelper.showSuccess(message, note)
                            }
                        } catch (e: Exception) {
                            notificationHelper.showSuccess("Done!", response)
                        }

                        isRecording = false
                        startListening()
                    },
                    onError = { error ->
                        Log.e(TAG, "Backend error: $error")
                        notificationHelper.showError(error)
                        isRecording = false
                        startListening()
                    }
                )
            },
            onError = { error ->
                Log.e(TAG, "Recording error: $error")
                notificationHelper.showError("Recording failed: $error")
                isRecording = false
                startListening()
            }
        )
    }

    fun stopListening() {
        speechService?.stop()
        speechService?.shutdown()
        speechService = null
        Log.d(TAG, "Listening stopped")
    }

    fun release() {
        stopListening()
        model?.close()
        model = null
        Log.d(TAG, "Resources released")
    }

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
            ""
        }
    }
}