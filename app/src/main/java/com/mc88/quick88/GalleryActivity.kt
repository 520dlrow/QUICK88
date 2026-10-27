package com.mc88.quick88

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GalleryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView
    private val mediaList = mutableListOf<MediaItem>()
    private lateinit var adapter: GalleryAdapter

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions.values.all { it }) {
            loadMedia()
        } else {
            Toast.makeText(this, "Permission required", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences("quick88_prefs", MODE_PRIVATE)
        if (prefs.getInt("theme_index", 0) == 1) setTheme(R.style.Theme_Quick88_Dark) else setTheme(R.style.Theme_Quick88)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        recyclerView = findViewById(R.id.recyclerViewGallery)
        tvEmpty = findViewById(R.id.tvEmptyGallery)
        val btnBack = findViewById<Button>(R.id.btnBackGallery)

        recyclerView.layoutManager = GridLayoutManager(this, 3)
        
        adapter = GalleryAdapter(mediaList) { position ->
            val item = mediaList[position]
            if (item.isVideo) {
                val intent = Intent(this, VideoPlayerActivity::class.java)
                intent.putExtra("videoUri", item.uri.toString())
                startActivity(intent)
            } else {
                val intent = Intent(this, ImageDetailActivity::class.java)
                val stringUris = ArrayList(mediaList.filter { !it.isVideo }.map { it.uri.toString() })
                intent.putStringArrayListExtra("imageUris", stringUris)
                
                val imagePosition = mediaList.take(position).count { !it.isVideo }
                intent.putExtra("position", imagePosition)
                startActivity(intent)
            }
        }
        
        recyclerView.adapter = adapter
        btnBack.setOnClickListener { finish() }

        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (permissions.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }) {
            loadMedia()
        } else {
            requestPermissionLauncher.launch(permissions)
        }
    }

    private fun loadMedia() {
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.MEDIA_TYPE
        )
        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )
        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

        val cursor = contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            projection, selection, selectionArgs, sortOrder
        )

        mediaList.clear()
        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val typeColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val type = it.getInt(typeColumn)
                val contentUri = ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), id)
                val isVideo = type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
                mediaList.add(MediaItem(contentUri, isVideo))
            }
        }

        adapter.notifyDataSetChanged()

        if (mediaList.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}
