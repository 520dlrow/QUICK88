package com.mc88.quick88

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class RecorderActivity : AppCompatActivity() {

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var recordFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences("quick88_prefs", MODE_PRIVATE)
        if (prefs.getInt("theme_index", 0) == 1) setTheme(R.style.Theme_Quick88_Dark) else setTheme(R.style.Theme_Quick88)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recorder)

        val btnRecord = findViewById<Button>(R.id.btnRecord)
        val btnStop = findViewById<Button>(R.id.btnStop)
        val btnPlay = findViewById<Button>(R.id.btnPlayRec)
        val tvStatus = findViewById<TextView>(R.id.tvRecStatus)

        recordFile = File(externalCacheDir, "quick88_record.3gp")

        btnRecord.setOnClickListener {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    mediaRecorder = MediaRecorder(this)
                } else {
                    @Suppress("DEPRECATION")
                    mediaRecorder = MediaRecorder()
                }
                mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                mediaRecorder?.setOutputFile(recordFile?.absolutePath)
                mediaRecorder?.prepare()
                mediaRecorder?.start()
                tvStatus.text = "Recording..."
            } catch (e: Exception) {
                Toast.makeText(this, "Error starting recorder", Toast.LENGTH_SHORT).show()
            }
        }

        btnStop.setOnClickListener {
            try {
                mediaRecorder?.stop()
                mediaRecorder?.release()
                mediaRecorder = null
                tvStatus.text = "Stopped"
            } catch (e: Exception) {
                Toast.makeText(this, "Error stopping", Toast.LENGTH_SHORT).show()
            }
        }

        btnPlay.setOnClickListener {
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(recordFile?.absolutePath)
                    prepare()
                    start()
                }
                tvStatus.text = "Playing..."
            } catch (e: Exception) {
                Toast.makeText(this, "No recording found", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnBackRec).setOnClickListener { finish() }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaRecorder?.release()
        mediaPlayer?.release()
    }
}
