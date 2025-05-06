package com.baristuzemen.firebasecloudstorageexample


import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MyDownloadService : MyBaseTaskService() {

    private lateinit var storageRef: StorageReference

    override fun onCreate() {
        super.onCreate()

        // Initialize Storage
        storageRef = FirebaseStorage.getInstance().reference
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand:$intent:$startId")

        if (ACTION_DOWNLOAD == intent.action) {
            // Get the path to download from the intent
            val downloadPath = intent.getStringExtra(EXTRA_DOWNLOAD_PATH)
            downloadFromPath(downloadPath)
        }

        return Service.START_REDELIVER_INTENT
    }

    private fun downloadFromPath(downloadPath: String) {

        // Mark task started, taskStarted
        Log.d(TAG, "downloadFromPath:$downloadPath")

        // Mark task started
        taskStarted()
        var totalBytesRead: Long = 0

        // Download and get total bytes
        storageRef.child(downloadPath).getStream { _, inputStream ->
            val buffer = ByteArray(1024)
            var size: Int = inputStream.read(buffer)

            while (size != -1) {
                size = inputStream.read(buffer)
                totalBytesRead += size
            }

            // Close the stream at the end of the Task
            inputStream.close()
        }.addOnSuccessListener { _ ->
            Log.d(TAG, "download:SUCCESS")

            // Send success broadcast with number of bytes downloaded
            broadcastDownloadFinished(downloadPath, totalBytesRead)

            // Mark task completed
            taskCompleted()
        }.addOnFailureListener { exception ->
            Log.w(TAG, "download:FAILURE", exception)

            // Send failure broadcast
            broadcastDownloadFinished(downloadPath, -1)

            // Mark task completed
            taskCompleted()
        }

        // Send success or failure broadcast with number of bytes downloaded (broadcastDownloadFinished .....

        // Mark task completed
    }

    /**
     * Broadcast finished download (success or failure).
     * @return true if a running receiver received the broadcast.
     */
    private fun broadcastDownloadFinished(downloadPath: String, bytesDownloaded: Long): Boolean {
        val success = bytesDownloaded != -1L
        val action = if (success) DOWNLOAD_COMPLETED else DOWNLOAD_ERROR

        val broadcast = Intent(action)
            .putExtra(EXTRA_DOWNLOAD_PATH, downloadPath)
            .putExtra(EXTRA_BYTES_DOWNLOADED, bytesDownloaded)
        return LocalBroadcastManager.getInstance(applicationContext)
            .sendBroadcast(broadcast)
    }

    companion object {

        private const val TAG = "Storage#DownloadService"

        /** Actions  */
        const val ACTION_DOWNLOAD = "action_download"
        const val DOWNLOAD_COMPLETED = "download_completed"
        const val DOWNLOAD_ERROR = "download_error"

        /** Extras  */
        const val EXTRA_DOWNLOAD_PATH = "extra_download_path"
        const val EXTRA_BYTES_DOWNLOADED = "extra_bytes_downloaded"

        val intentFilter: IntentFilter
            get() {
                val filter = IntentFilter()
                filter.addAction(DOWNLOAD_COMPLETED)
                filter.addAction(DOWNLOAD_ERROR)

                return filter
            }
    }
}