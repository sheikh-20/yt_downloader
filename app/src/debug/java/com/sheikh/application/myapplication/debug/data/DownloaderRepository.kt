package com.sheikh.application.myapplication.debug.data

import com.chaquo.python.PyObject
import com.chaquo.python.Python

interface DownloaderRepository {
    suspend fun getTitle(videoUrl: String): String
    suspend fun getThumbnail(videoUrl: String): String
    suspend fun getVideoStreams(videoUrl: String): String
    suspend fun getAudioStreams(videoUrl: String): String
    suspend fun getFilePath(videoUrl: String): String
    suspend fun videoDownload(videoUrl: String, iTag: Int)
    suspend fun audioDownload(videoUrl: String, iTag: Int)
    suspend fun mergeAudioVideo(videoUrl: String)
}

class DefaultDownloaderRepository(private val python: Python): DownloaderRepository {

    private val moduleName = "YoutubeDownloaderDebug"

    override suspend fun getTitle(videoUrl: String): String {
        return python.getModule(moduleName).callAttr("video_title", videoUrl).toString()
    }

    override suspend fun getThumbnail(videoUrl: String): String {
        return python.getModule(moduleName).callAttr("video_thumbnail", videoUrl).toString()
    }

    override suspend fun getVideoStreams(videoUrl: String): String {
        return python.getModule(moduleName).callAttr("video_streams", videoUrl).toString()
    }

    override suspend fun getAudioStreams(videoUrl: String): String {
        return python.getModule(moduleName).callAttr("audio_streams", videoUrl).toString()
    }

    override suspend fun getFilePath(videoUrl: String): String {
        return python.getModule(moduleName).callAttr("file_path", videoUrl).toString()
    }

    override suspend fun videoDownload(videoUrl: String, iTag: Int) {
        python.getModule(moduleName).callAttr("video_download", videoUrl, iTag)
    }

    override suspend fun audioDownload(videoUrl: String, iTag: Int) {
        python.getModule(moduleName).callAttr("audio_download", videoUrl, iTag)
    }

    override suspend fun mergeAudioVideo(videoUrl: String) {
        python.getModule(moduleName).callAttr("audio_video_merge", videoUrl)
    }
}