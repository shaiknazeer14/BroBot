package com.nazeer.brobot

import android.content.Context
import android.util.Log
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.StorageService

class WakeWordDetector(private val context: Context,private val audioRecorder: AudioRecorder) : RecognitionListener {

    private var model: Model? = null
    private var speechService: SpeechService? = null

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
                Log.e("WakeWordDetector", "Failed to load model: ${exception.message}")
            }
        )
    }

    fun startListening() {
        try {
            if (model == null) {
                Log.e("WakeWordDetector", "Model is not loaded yet!")
                return
            }

            val recognizer = Recognizer(model, 16000.0f)
            speechService = SpeechService(recognizer, 16000.0f)
            speechService?.startListening(this)

            Log.d("WakeWordDetector", "Listening started")

        } catch (e: Exception) {
            Log.e("WakeWordDetector", "Failed to start listening: ${e.message}")
        }
    }

    override fun onPartialResult(hypothesis: String?) {
        Log.d("WakeWordDetector", "Partial result: $hypothesis")
    }

    override fun onResult(hypothesis: String?) {
        Log.d("WakeWordDetector", "Final result: $hypothesis")

        hypothesis?.let {
            if (it.contains("hey bro", ignoreCase = true)) {
                Log.d("WakeWordDetector", "Wake word detected")
                onWakeWordDetected()
            }
        }
    }

    override fun onFinalResult(hypothesis: String?) {
        Log.d("WakeWordDetector", "Final result: $hypothesis")
    }

    override fun onError(exception: Exception?) {
        Log.e("WakeWordDetector", "Error: ${exception?.message}")
    }

    override fun onTimeout() {
        Log.d("WakeWordDetector", "Timeout")
    }

    private fun onWakeWordDetected() {
        Log.d("WakeWordDetector", "Hey Bro detected! Starting recording...")

        // Step 1 — Stop Vosk → free the mic
        stopListening()

        // Step 2 — Start recording
        audioRecorder.startRecording { filePath ->
            // Step 3 — Recording complete → resume Vosk
            Log.d("WakeWordDetector", "Recording complete → file: $filePath")
            startListening()
        }
    }

    fun stopListening() {
        speechService?.stop()
        speechService?.shutdown()
        speechService = null

        Log.d("WakeWordDetector", "Listening stopped")
    }
}