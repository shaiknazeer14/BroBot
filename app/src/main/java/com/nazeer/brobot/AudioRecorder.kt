package com.nazeer.brobot

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.File
import java.io.IOException

class AudioRecorder(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var outputFilePath: String = ""
    private val handler = Handler(Looper.getMainLooper())
    private var isRecording = false

    companion object {
        const val TAG = "AudioRecorder"
        const val RECORD_DURATION_MS = 5000L
    }

    fun startRecording(
        onRecordingFinished: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (isRecording) {
            Log.d(TAG, "Already recording — skipping")
            return
        }

        try {
            val outputFile = File(context.filesDir, "command.mp4")
            outputFilePath = outputFile.absolutePath

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            mediaRecorder!!.apply {
                setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(16000)
                setOutputFile(outputFilePath)
                prepare()
                start()
            }

            isRecording = true
            Log.d(TAG, "Recording started → $outputFilePath")

            // Auto stop after 5 seconds
            handler.postDelayed({
                stopRecording(onRecordingFinished, onError)
            }, RECORD_DURATION_MS)

        } catch (e: Exception) {
            Log.e(TAG, "Failed to start recording: ${e.message}")
            isRecording = false
            onError("Failed to start recording: ${e.message}")
        }
    }

    private fun stopRecording(
        onRecordingFinished: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (!isRecording) {
            Log.d(TAG, "Not recording — skipping stop")
            return
        }

        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            Log.d(TAG, "Recording stopped → file saved: $outputFilePath")
            onRecordingFinished(outputFilePath)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop recording: ${e.message}")
            isRecording = false
            onError("Failed to stop recording: ${e.message}")
        }
    }

    fun release() {
        handler.removeCallbacksAndMessages(null)
        mediaRecorder?.release()
        mediaRecorder = null
        isRecording = false
        Log.d(TAG, "AudioRecorder released")
    }
}