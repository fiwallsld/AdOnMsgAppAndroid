package com.example.adonmessengerapp

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import java.io.FileNotFoundException
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    
    private lateinit var videoView: VideoView
    private lateinit var btnCheck: Button
    private lateinit var btnRunGroundService: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))

         val serviceIntent = Intent(this, MyUsageService::class.java)
         startService(serviceIntent)

        videoView = findViewById(R.id.videoView)
        btnCheck = findViewById(R.id.btnCheck)
        btnRunGroundService = findViewById(R.id.btnRunGroundService)

        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        val videoUri: Uri = Uri.parse("https://www.w3schools.com/html/mov_bbb.mp4")

            videoView.setMediaController(mediaController)
            videoView.setVideoURI(videoUri)
            videoView.start()

        btnCheck.setOnClickListener {
            if (checkUsageAccessPermission()) {
                Toast.makeText(this, "Quyền truy cập Usage đã được cấp!", Toast.LENGTH_SHORT).show()
                Log.d("MainActivity", "Quyền Usage Access đã được cấp")
                // startActivity(intentMyUsageService)
            } else {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                startActivity(intent)
            }
        }

        btnRunGroundService.setOnClickListener {
                Toast.makeText(this, "Check my btn!", Toast.LENGTH_SHORT).show()
                Log.d("btnRunGroundService", "Nhan de chay nen ung dung")
                moveTaskToBack(true)
        }
    }

    private fun checkUsageAccessPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }


}
