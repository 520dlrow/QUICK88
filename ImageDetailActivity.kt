package com.mc88.quick88

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ImageDetailActivity : AppCompatActivity() {

    private var currentIndex = 0
    private var imageUris: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences("quick88_prefs", MODE_PRIVATE)
        if (prefs.getInt("theme_index", 0) == 1) setTheme(R.style.Theme_Quick88_Dark) else setTheme(R.style.Theme_Quick88)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_detail)

        val ivImage = findViewById<ImageView>(R.id.ivFullImage)
        val btnClose = findViewById<Button>(R.id.btnCloseImage)
        val btnPrev = findViewById<Button>(R.id.btnPrevImage)
        val btnNext = findViewById<Button>(R.id.btnNextImage)

        imageUris = intent.getStringArrayListExtra("imageUris") ?: arrayListOf()
        currentIndex = intent.getIntExtra("position", 0)

        fun updateImage() {
            if (imageUris.isNotEmpty() && currentIndex in imageUris.indices) {
                ivImage.setImageURI(Uri.parse(imageUris[currentIndex]))
            }
        }

        updateImage()

        btnClose.setOnClickListener { finish() }

        btnPrev.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                updateImage()
            }
        }

        btnNext.setOnClickListener {
            if (currentIndex < imageUris.size - 1) {
                currentIndex++
                updateImage()
            }
        }
    }
}
