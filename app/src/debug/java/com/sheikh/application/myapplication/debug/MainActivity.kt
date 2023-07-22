package com.sheikh.application.myapplication.debug

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.arthenica.mobileffmpeg.FFmpeg
import com.arthenica.mobileffmpeg.FFmpegExecution
import com.sheikh.application.myapplication.debug.ui.DownloaderApp
import com.sheikh.application.myapplication.debug.ui.theme.YoutubeDownloaderTheme
import java.io.File

private const val TAG = "MainActivity"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YoutubeDownloaderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DownloaderApp()
                }
            }
        }
    }
}