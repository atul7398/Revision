package com.app.rivisio.utils

import java.io.File

object FileUtils {

    const val fileSizeThreshold = 5

    fun calculateFileSize(filepath: String): Float {
        val file = File(filepath);
        val fileSizeInBytes = file.length()
        val fileSizeInKB: Float = fileSizeInBytes / 1024F
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        return fileSizeInKB / 1024F
    }

}