package com.mc88.quick88

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.ContactsContract
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {

    private var isFlashOn = false
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String
    private lateinit var vibrator: Vibrator
    private lateinit var toneGenerator: ToneGenerator
    private lateinit var tvBattery: TextView

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val batteryPct = (level * 100 / scale.toFloat()).toInt()
            tvBattery.text = "🔋 $batteryPct%"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences("quick88_prefs", MODE_PRIVATE)
        if (prefs.getInt("theme_index", 0) == 1) setTheme(R.style.Theme_Quick88_Dark) else setTheme(R.style.Theme_Quick88)
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvBattery = findViewById(R.id.tvBatteryTop)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

        try {
            cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            cameraId = cameraManager.cameraIdList[0]
        } catch (e: Exception) {}

        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        val btnTheme = findViewById<ImageButton>(R.id.btnTheme)
        btnTheme.setOnClickListener {
            playFeedback()
            val current = prefs.getInt("theme_index", 0)
            prefs.edit().putInt("theme_index", if (current == 0) 1 else 0).apply()
            recreate()
        }

        findViewById<CardView>(R.id.cardFlashlight).setOnClickListener {
            playFeedback()
            try {
                isFlashOn = !isFlashOn
                cameraManager.setTorchMode(cameraId, isFlashOn)
            } catch (e: Exception) {
                Toast.makeText(this, "Flashlight error", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<CardView>(R.id.cardCamera).setOnClickListener {
            playFeedback()
            startActivity(Intent(this, CameraActivity::class.java))
        }

        findViewById<CardView>(R.id.cardVideo).setOnClickListener {
            playFeedback()
            val intent = Intent(MediaStore.INTENT_ACTION_VIDEO_CAMERA)
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Video camera not found", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<CardView>(R.id.cardContacts).setOnClickListener {
            playFeedback()
            val intent = Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI)
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Contacts app not found", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<CardView>(R.id.cardGallery).setOnClickListener {
            playFeedback()
            startActivity(Intent(this, GalleryActivity::class.java))
        }

        findViewById<CardView>(R.id.cardCalculator).setOnClickListener {
            playFeedback()
            startActivity(Intent(this, CalculatorActivity::class.java))
        }

        findViewById<CardView>(R.id.cardMagnifier).setOnClickListener {
            playFeedback()
            startActivity(Intent(this, MagnifierActivity::class.java))
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(batteryReceiver)
    }
}
