package app.brainpool.nodesmobile.util

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import app.brainpool.nodesmobile.util.GlobalVar.TAG
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


class DownloadImageWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        return try {
            val linkMap = inputData.getString("link")
            saveImage(
                Glide.with(applicationContext)
                    .asBitmap()
                    .load(linkMap)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .submit()
                    .get(), inputData.getString("fName").toString()
            )
            Result.success()
        } catch (throwable: Throwable) {
            Log.e(TAG, "Error Worker:" + throwable.message)
            Result.failure()
        }
    }

//    fun isScheduled(context: Context?): Boolean {
//        // NOT WORKS!
//        val instance = WorkManager.getInstance(context!!)
//        val statuses: ListenableFuture<List<WorkInfo>> = instance
//            .getWorkInfosForUniqueWork(WORKER_TAG)
//        return try {
//            var running = false
//            val workInfoList: List<WorkInfo>
//            workInfoList = statuses.get()
//            for (workInfo in workInfoList) {
//                val state = workInfo.state
//                running = state == WorkInfo.State.RUNNING ||
//                        state == WorkInfo.State.ENQUEUED
//            }
//            running
//        } catch (e: ExecutionException) {
//            e.printStackTrace()
//            false
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//            false
//        }
//    }
}
