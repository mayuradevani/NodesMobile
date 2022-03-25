package app.brainpool.nodesmobile.util

import android.graphics.Bitmap
import android.os.Environment
import app.brainpool.nodesmobile.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

fun saveImage(image: Bitmap, imageFileName: String) {
    try {
        val imageFile = getFile(imageFileName)
        if (imageFile != null) {
            try {
                val fOut: OutputStream = FileOutputStream(imageFile)
                image.compress(Bitmap.CompressFormat.PNG, 100, fOut)
                fOut.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        image.recycle()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun getFile(imageFileName: String): File? {
    try {
        val imageFileDir = imageFileName.substring(
            0,
            imageFileName.lastIndexOf("/") + 1
        )//2D3UYW06VNRV6D/22/3859879/
        val fName = imageFileName.substring(imageFileName.lastIndexOf("/") + 1)//1692547.png
        val pathFolder =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString() + "/.NodesMobile/" + imageFileDir
        val storageDir = File(pathFolder)
//        val folder = imageFileDir.substring(0, imageFileDir.indexOf("/"))
//        val count = getAllImageFilesInFolder(
//            File(
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//                    .toString() + "/.NodesMobile/" + folder
//            )
//        )
//        Log.v(TAG, "Downloading in $folder : $count")
//        Log.v(TAG, imageFileName)
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

fun getNavigationIdForNotificationType(id: String): Int {
    return when (id) {
        "new-map-notification" -> R.id.mapFragment
        else -> R.id.mapFragment

    }
}
