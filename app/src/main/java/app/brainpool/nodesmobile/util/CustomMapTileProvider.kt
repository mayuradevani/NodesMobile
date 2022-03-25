package app.brainpool.nodesmobile.util

import android.content.Context
import android.os.Environment
import android.util.Log
import app.brainpool.nodesmobile.util.GlobalVar.TAG
import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class CustomMapTileProvider(context: Context, mapTileFolder: String) : TileProvider {
    var context = context
    var reversedY = 0
    val mapTileFolder = mapTileFolder
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
            e.printStackTrace()
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
                    .toString() + "/.NodesMobile/" + mapTileFolder
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