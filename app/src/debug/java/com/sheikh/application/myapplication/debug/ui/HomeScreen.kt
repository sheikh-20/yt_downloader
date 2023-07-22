package com.sheikh.application.myapplication.debug.ui

import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.sheikh.application.myapplication.debug.model.Stream
import com.sheikh.application.myapplication.debug.ui.theme.YoutubeDownloaderTheme
import com.sheikh.application.myapplication.debug.viewmodel.DownloadUiState

private const val TAG = "HomeScreen"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier,
               uiState: DownloadUiState = DownloadUiState.Default,
               onClick: (String) -> Unit = {},
               onDownloadClick: (String, Stream, Stream) -> Unit = { _, _, _ ->},
               onCancelClick: () -> Unit = {}
               ) {

    var value by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    LaunchedEffect(key1 = "") {
        value = Intent(Intent.ACTION_SEND).getStringExtra(Intent.EXTRA_TEXT) ?: ""
        Log.d(TAG, value.toString())
    }

    Column(modifier = modifier
        .fillMaxSize()
        .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

        OutlinedTextField(
            value = value,
            onValueChange = { value = it },
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            )

        when (uiState) {
            is DownloadUiState.Default -> {
                Button(
                    onClick = {
                        Log.d(TAG, "Debug OnClick")
                        onClick(value)
                        focusManager.clearFocus()
                    },
                    modifier = modifier
                        .fillMaxWidth()
                        .wrapContentWidth(align = Alignment.End)
                ) {
                    Text(text = "Click")
                }
            }
            is DownloadUiState.Loading -> {
                Row(modifier = modifier.fillMaxWidth().wrapContentWidth(align = Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                    OutlinedButton(onClick = onCancelClick) {
                        Text(text = "Cancel")
                    }

                    CircularProgressIndicator()
                }
            }
            is DownloadUiState.Complete -> {
                Button(
                    onClick = {
                        Log.d(TAG, "Debug OnClick")
                        onClick(value)
                        focusManager.clearFocus()
                    },
                    modifier = modifier
                        .fillMaxWidth()
                        .wrapContentWidth(align = Alignment.End)
                ) {
                    Text(text = "Click")
                }

                HomeCard(thumbnailUrl = uiState.videoThumbnail,
                    videoTitle = uiState.videoTitle,
                    videoStreams = uiState.videoStreams,
                    audioStream = uiState.audioStreams,
                    videoUrl = value,
                    onDownloadClick = onDownloadClick
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeCard(modifier: Modifier = Modifier,
                     thumbnailUrl: String = "",
                     videoTitle: String = "",
                     videoStreams: List<Stream> = emptyList(),
                     audioStream: Stream? = null,
                     videoUrl: String = "",
                     onDownloadClick: (String, Stream, Stream) -> Unit = { _, _, _ ->}
                     ) {
    Card(elevation = CardDefaults.cardElevation(), shape = RoundedCornerShape(20)) {
        Row(modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Column() {
                Image(
                    painter = rememberAsyncImagePainter(model = thumbnailUrl),
                    contentDescription = null,
                    modifier = modifier
                        .size(width = 150.dp, height = 100.dp)
                        .clip(
                            RoundedCornerShape(20)
                        ),
                    contentScale = ContentScale.Crop
                )

                Text(text = videoTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = modifier.width(150.dp),
                    maxLines = 2
                    )
            }

            LazyColumn(modifier = modifier
                .height(100.dp)
                .weight(1f),) {
                items(videoStreams) {
                    VideoResolutionCard(videoStream = it, videoUrl = videoUrl, audioStream = audioStream, onDownloadClick = onDownloadClick)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VideoResolutionCard(modifier: Modifier = Modifier,
                                videoStream: Stream? = null,
                                audioStream: Stream? = null,
                                videoUrl: String = "",
                                onDownloadClick: (String, Stream, Stream) -> Unit = { _, _, _ ->}
                                ) {
    Card(elevation = CardDefaults.cardElevation(),
        shape = RoundedCornerShape(20)) {
        Row(modifier = modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {

            if (videoStream != null) {
                Text(text = videoStream.resolution.substring(1, videoStream.resolution.length.minus(1)), style = MaterialTheme.typography.titleSmall)
            }

            Spacer(modifier = modifier.weight(1f))

            FloatingActionButton(onClick = {
                videoStream?.let {  video ->
                    audioStream?.let {  audio ->
                        onDownloadClick(videoUrl, video, audio)}
                    } }, modifier.size(30.dp)) {
                Icon(imageVector = Icons.Outlined.Download, contentDescription = null)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenLightThemePreview() {
    YoutubeDownloaderTheme(darkTheme = false) {
        HomeScreen()
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenDarkThemePreview() {
    YoutubeDownloaderTheme(darkTheme = true) {
        HomeScreen()
    }
}