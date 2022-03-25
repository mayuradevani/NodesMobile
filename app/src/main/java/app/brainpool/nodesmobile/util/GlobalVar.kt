package app.brainpool.nodesmobile.util

object GlobalVar {
    val WORKER_TAG: String= DownloadImageWorker::class.java.simpleName
    const val TAG = "NODES_TAG"
//    const val EXTRA_FILE_NAME = "mapFileName"
//    const val PROPERTY_ID= "propertyId"
    const val REQUEST_CHECK_SETTINGS = 2
//    const val MAP_TILES_SERVER = "https://storage.googleapis.com/brainpool/user-files/brainpoollicense/maptiles"
    const val DEV_URL="http://34.149.134.85/graph-private"//34.126.80.190
    const val QA_URL="http://34.149.178.205/graph-private"
    const val PRODUCTION_URL="http://34.149.101.219/graph-private"
}