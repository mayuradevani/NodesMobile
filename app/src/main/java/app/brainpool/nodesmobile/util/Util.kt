package app.brainpool.nodesmobile.util

import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

fun saveImage(image: Bitmap, imageFileName: String): String? {
    try {
        val imageFile = getFile(imageFileName)
        var savedImagePath = imageFile?.absolutePath
        if (imageFile != null) {
            try {
                val fOut: OutputStream = FileOutputStream(imageFile)
                image.compress(Bitmap.CompressFormat.PNG, 100, fOut)
                fOut.close()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        return savedImagePath
    } catch (e: Exception) {
        e.printStackTrace()
        return ""
    }
}

fun getFile(imageFileName: String): File? {
    try {
        val imageFileDir = imageFileName.substring(0, imageFileName.lastIndexOf("/") + 1)
        val fName = imageFileName.substring(imageFileName.lastIndexOf("/") + 1)
        val pathFolder =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString() + "/.NodesMobile/" + imageFileDir
        val storageDir = File(pathFolder)
        var success = true
        if (!storageDir.exists()) {
            success = storageDir.mkdirs()
        }
        if (success) {
            return File(storageDir, fName)
        } else
            return null
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

