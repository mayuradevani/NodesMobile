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
            var file = getTileFile(x, reversedY, zoom)
            if (file != null) {
                `in` = FileInputStream(file)
                buffer = ByteArrayOutputStream()
                var nRead: Int
                val data = ByteArray(BUFFER_SIZE)
                while (`in`.read(data, 0, BUFFER_SIZE).also { nRead = it } != -1) {
                    buffer.write(data, 0, nRead)
                }
                buffer.flush()
                buffer.toByteArray()
            } else
                return null
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
//        return if (hasTile(x, y, zoom)) {
        val image = readTileImage(x, y, zoom)
        return if (image == null) TileProvider.NO_TILE else Tile(TILE_WIDTH, TILE_HEIGHT, image)
//        } else {
//            TileProvider.NO_TILE
//        }
    }

//    private fun hasTile(x: Int, y: Int, zoom: Int): Boolean {
//        val b: Rect? = TILE_ZOOMS.get(zoom)
//        return if (b == null) false else b.left <= x && x <= b.right && b.top <= y && y <= b.bottom
//    }

//    private val TILE_ZOOMS: SparseArray<Rect?> = object : SparseArray<Rect?>() {
//        init {
//            put(8, Rect(135, 180, 135, 181))
//            put(9, Rect(270, 361, 271, 363))
//            put(10, Rect(541, 723, 543, 726))
//            put(11, Rect(1082, 1447, 1086, 1452))
//            put(12, Rect(2165, 2894, 2172, 2905))
//            put(13, Rect(4330, 5789, 4345, 5810))
//            put(14, Rect(8661, 11578, 8691, 11621))
//        }
//    }

    companion object {
        private const val TILE_WIDTH = 256
        private const val TILE_HEIGHT = 256
        private const val BUFFER_SIZE = 16 * 1024
    }
}