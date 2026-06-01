package com.monkeydluffy.luffyassistant

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var btnMicrophone: Button
    private lateinit var tvOutput: TextView
    private val SPEECH_REQUEST_CODE = 100
    private val PERMISSION_REQUEST_CODE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnMicrophone = findViewById(R.id.btn_microphone)
        tvOutput = findViewById(R.id.tv_output)

        // Request permissions
        if (!hasRecordAudioPermission()) {
            requestRecordAudioPermission()
        }

        btnMicrophone.setOnClickListener {
            startVoiceRecognition()
        }
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "বলুন আপনার কমান্ড...")

        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(this, "Voice Recognition উপলব্ধ নয়", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!results.isNullOrEmpty()) {
                val spokenText = results[0]
                tvOutput.text = "আপনি বলেছেন: $spokenText"
                processVoiceCommand(spokenText)
            }
        }
    }

    private fun processVoiceCommand(command: String) {
        when {
            command.contains("সালাম", ignoreCase = true) ||
            command.contains("হাই", ignoreCase = true) -> {
                Toast.makeText(this, "আপনার উপর সালাম!", Toast.LENGTH_SHORT).show()
            }
            command.contains("সময়", ignoreCase = true) -> {
                val time = java.text.SimpleDateFormat("HH:mm", Locale.getDefault())
                    .format(java.util.Date())
                Toast.makeText(this, "সময়: $time", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "কমান্ড বুঝতে পারলাম না", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hasRecordAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestRecordAudioPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "অনুমতি দেওয়া হয়েছে", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "অনুমতি প্রয়োজন", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
