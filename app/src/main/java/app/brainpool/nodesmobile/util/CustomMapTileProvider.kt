package app.brainpool.nodesmobile.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.util.GlobalVar.TAG
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileProvider
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class CustomMapTileProvider(context: Context) : TileProvider {
    var context = context
    var reversedY = 0
    private fun readTileImage(x: Int, y: Int, zoom: Int): ByteArray? {
        var `in`: FileInputStream? = null
        var buffer: ByteArrayOutputStream? = null
        return try {
            reversedY = (1 shl zoom) - y - 1
            `in` = FileInputStream(getTileFile(x, reversedY, zoom))
            buffer = ByteArrayOutputStream()
            var nRead: Int
            val data = ByteArray(BUFFER_SIZE)
            while (`in`.read(data, 0, BUFFER_SIZE).also { nRead = it } != -1) {
                buffer.write(data, 0, nRead)
            }
            buffer.flush()
            buffer.toByteArray()
        } catch (e: IOException) {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    var imageFileName =
                        Prefs.getString(PrefsKey.MAP_TILE_FOLDER) + "/" + zoom + "/" + x + "/" + reversedY + ".png"
                    var s2 =
                        GlobalVar.MAP_TILES_SERVER + "/" + imageFileName

                    Glide.with(context)
                        .asBitmap()
                        .listener(object : RequestListener<Bitmap> {
                            override fun onResourceReady(
                                resource: Bitmap?,
                                model: Any?,
                                target: Target<Bitmap>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                resource?.let {

                                    var imageFileDir = imageFileName.substring(
                                        0,
                                        imageFileName.lastIndexOf("/") + 1
                                    )
                                    var fName =
                                        imageFileName.substring(imageFileName.lastIndexOf("/") + 1)
                                    var pathFolder =
                                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                            .toString() + "/.NodesMobile/" + imageFileDir
                                    val storageDir = File(pathFolder)
                                    var success = true
                                    if (!storageDir.exists()) {
                                        success = storageDir.mkdirs()
                                    }
                                    if (success) {
                                        val imageFile = File(storageDir, fName)
                                        if (!imageFile.exists()) {
                                            Log.v(TAG, "Saving Image: $s2")
                                            saveImage(it, imageFileName)
                                        }
                                    }

                                }
                                return false
                            }

                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Bitmap>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }
                        })
                        .load(s2)
                        .placeholder(android.R.drawable.progress_indeterminate_horizontal) // need placeholder to avoid issue like glide annotations
                        .error(android.R.drawable.stat_notify_error) // need error to avoid issue like glide annotations
                        .submit()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            null
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            Log.v(TAG, "OUM: " + e.message)
            null
        } finally {
            if (`in` != null) try {
                `in`.close()
            } catch (ignored: Exception) {
            }
            if (buffer != null) try {
                buffer.close()
            } catch (ignored: Exception) {
            }
        }
    }

    private fun getTileFile(x: Int, y: Int, zoom: Int): File? {
        var file: File? = null
        try {
            val sdcard = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString() + "/.NodesMobile/" + Prefs.getString(PrefsKey.MAP_TILE_FOLDER)
            )
            val tileFile = "/$zoom/$x/$y.png"
            file = File(sdcard, tileFile)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.v(TAG, "Tile not found: " + e.message)
        }
        return file
    }

    override fun getTile(x: Int, y: Int, zoom: Int): Tile? {
        val image = readTileImage(x, y, zoom)
        return if (image == null) null else Tile(TILE_WIDTH, TILE_HEIGHT, image)
    }

    companion object {
        private const val TILE_WIDTH = 256
        private const val TILE_HEIGHT = 256
        private const val BUFFER_SIZE = 16 * 1024
    }
}