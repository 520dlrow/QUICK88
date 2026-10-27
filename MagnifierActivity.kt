package com.mc88.quick88

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat

class MagnifierActivity : AppCompatActivity() {

    private lateinit var viewFinder: PreviewView
    private var camera: Camera? = null

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) startCamera() else finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences("quick88_prefs", MODE_PRIVATE)
        if (prefs.getInt("theme_index", 0) == 1) setTheme(R.style.Theme_Quick88_Dark) else setTheme(R.style.Theme_Quick88)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_magnifier)

        viewFinder = findViewById(R.id.viewFinderMag)
        val btnZoomIn = findViewById<Button>(R.id.btnZoomInMag)
        val btnZoomOut = findViewById<Button>(R.id.btnZoomOutMag)
        val btnExit = findViewById<Button>(R.id.btnExitMag)

        btnExit.setOnClickListener { finish() }

        btnZoomIn.setOnClickListener {
            camera?.cameraControl?.setZoomRatio((camera?.cameraInfo?.zoomState?.value?.zoomRatio ?: 1f) + 0.5f)
        }

        btnZoomOut.setOnClickListener {
            val currentZoom = camera?.cameraInfo?.zoomState?.value?.zoomRatio ?: 1f
            if (currentZoom > 1f) {
                camera?.cameraControl?.setZoomRatio(currentZoom - 0.5f)
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }
}
