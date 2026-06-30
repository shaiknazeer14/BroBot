package com.nazeer.brobot
//needed to access assets, files, system services
import android.content.Context
//Lets us print debug messages to Android Studio's Logcat so we can see what's happening while testing
import android.util.Log
import org.vosk.Model
// typeConverts raw audio into text using a Model
import org.vosk.Recognizer
//Defines 5 methods any "listener" must implement to receive recognition results
import org.vosk.android.RecognitionListener
// Manages the live microphone stream and feeds it into a Recognizer
import org.vosk.android.SpeechService
import org.vosk.android.StorageService

class WakeWordDetector(private val context: Context) : RecognitionListener {

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
            },
            { exception ->
                Log.e("WakeWordDetector", "Failed to load model: ${exception.message}")
            }
        )
    }

}