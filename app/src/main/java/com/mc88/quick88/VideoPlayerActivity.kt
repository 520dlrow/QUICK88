package com.mc88.quick88

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class VideoPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        val videoView = findViewById<VideoView>(R.id.videoView)
        val btnClose = findViewById<Button>(R.id.btnCloseVideo)

        val videoUriString = intent.getStringExtra("videoUri")

        if (videoUriString != null) {
            try {
                val uri = Uri.parse(videoUriString)
                val mediaController = MediaController(this)
                mediaController.setAnchorView(videoView)
                videoView.setMediaController(mediaController)
                videoView.setVideoURI(uri)
                videoView.setOnPreparedListener { it.isLooping = false }
                videoView.start()
            } catch (e: Exception) {
                Toast.makeText(this, R.string.video_error, Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            finish()
        }

        btnClose.setOnClickListener { finish() }
    }
}
