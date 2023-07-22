package com.sheikh.application.myapplication.debug.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sheikh.application.myapplication.debug.viewmodel.DownloaderViewModel

@Composable
fun DownloaderApp(viewModel: DownloaderViewModel = viewModel(factory = DownloaderViewModel.Factory)) {

    val uiState by viewModel.downloaderUiState.collectAsState()

    HomeScreen(
        uiState = uiState,
        onClick = viewModel::getVideoInfo,
        onDownloadClick = viewModel::videoDownload,
        onCancelClick = viewModel::cancelWorker
    )
}