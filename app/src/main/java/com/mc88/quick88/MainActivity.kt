package com.mc88.quick88

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.ContactsContract
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {

    private var isFlashOn = false
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String
    private lateinit var vibrator: Vibrator
    private lateinit var toneGenerator: ToneGenerator

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences("quick88_prefs", MODE_PRIVATE)
        when (prefs.getInt("theme_index", 0)) {
            1 -> setTheme(R.style.Theme_Quick88_Dark)
            else -> setTheme(R.style.Theme_Quick88)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

        try {
            cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            cameraId = cameraManager.cameraIdList[0]
        } catch (e: Exception) {}

        val btnTheme = findViewById<ImageButton>(R.id.btnTheme)
        val cardFlashlight = findViewById<CardView>(R.id.cardFlashlight)
        val cardCamera = findViewById<CardView>(R.id.cardCamera)
        val cardContacts = findViewById<CardView>(R.id.cardContacts)
        val cardGallery = findViewById<CardView>(R.id.cardGallery)

        btnTheme.setOnClickListener {
            playFeedback()
            val current = prefs.getInt("theme_index", 0)
            val next = if (current == 0) 1 else 0
            prefs.edit().putInt("theme_index", next).apply()
            recreate()
        }

        cardFlashlight.setOnClickListener {
            playFeedback()
            try {
                isFlashOn = !isFlashOn
                cameraManager.setTorchMode(cameraId, isFlashOn)
            } catch (e: Exception) {
                Toast.makeText(this, "Flashlight not available", Toast.LENGTH_SHORT).show()
            }
        }

        cardCamera.setOnClickListener {
            playFeedback()
            startActivity(Intent(this, CameraActivity::class.java))
        }

        cardContacts.setOnClickListener {
            playFeedback()
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Contacts not available", Toast.LENGTH_SHORT).show()
            }
        }

        cardGallery.setOnClickListener {
            playFeedback()
            startActivity(Intent(this, GalleryActivity::class.java))
        }
    }

    private fun playFeedback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(50)
        }
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 50)
    }
}
