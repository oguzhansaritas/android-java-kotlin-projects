package com.baristuzemen.firebasecloudstorageexample

import android.app.Activity
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*



fun getFilename(contentResolver: ContentResolver, uploadUri: Uri) : String
{
    val returnCursor = contentResolver.query(uploadUri, null, null, null, null)
    val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    returnCursor.moveToFirst()
    return returnCursor.getString(nameIndex)
}

class MainActivity : AppCompatActivity() {



    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var progressDialog: ProgressDialog

    private var downloadUrl: Uri? = null
    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        layoutStorage.visibility = View.VISIBLE

        // Click listeners
        buttonCamera.setOnClickListener(this)
        buttonDownload.setOnClickListener(this)

        progressDialog = ProgressDialog(this)
        progressDialog.isIndeterminate = true

        // Local broadcast receiver
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.d(TAG, "onReceive:$intent")
                hideProgressDialog()

                when (intent.action) {
                    MyDownloadService.DOWNLOAD_COMPLETED -> {

                        // Get number of bytes downloaded
                        val numBytes = intent.getLongExtra(MyDownloadService.EXTRA_BYTES_DOWNLOADED, 0)

                        // Alert success
                        showMessageDialog(getString(R.string.success), String.format(
                            Locale.getDefault(),
                            "%d bytes downloaded from %s",
                            numBytes,
                            intent.getStringExtra(MyDownloadService.EXTRA_DOWNLOAD_PATH)))
                    }
                    MyDownloadService.DOWNLOAD_ERROR ->
                        // Alert failure
                        showMessageDialog("Error", String.format(Locale.getDefault(),
                            "Failed to download from %s",
                            intent.getStringExtra(MyDownloadService.EXTRA_DOWNLOAD_PATH)))

                    MyUploadService.UPLOAD_COMPLETED, MyUploadService.UPLOAD_ERROR -> onUploadResultIntent(intent)
                }
            }
        }

        // Restore instance state
        savedInstanceState?.let {
            fileUri = it.getParcelable(KEY_FILE_URI)
            downloadUrl = it.getParcelable(KEY_DOWNLOAD_URL)
        }
    }

    public override fun onStart() {
        super.onStart()

        updateUI()

        // Register receiver for uploads and downloads
        val manager = LocalBroadcastManager.getInstance(this)
        manager.registerReceiver(broadcastReceiver, MyDownloadService.intentFilter)
        manager.registerReceiver(broadcastReceiver, MyUploadService.intentFilter)
    }

    public override fun onStop() {
        super.onStop()

        // Unregister download receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }

    public override fun onSaveInstanceState(out: Bundle) {
        super.onSaveInstanceState(out)
        out.putParcelable(KEY_FILE_URI, fileUri)
        out.putParcelable(KEY_DOWNLOAD_URL, downloadUrl)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG,"onActivityResult:$resultCode:$resultCode:$data")
        if (requestCode == RC_TAKE_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                fileUri = data?.data

                if (fileUri != null) {
                    uploadFromUri(fileUri!!)
                } else {
                    Log.w(TAG, "File URI is null")
                }
            } else {
                Toast.makeText(this, "Taking picture failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadFromUri(uploadUri: Uri) {
        Log.d(TAG, "uploadFromUri:src: $uploadUri")

        // Save the File URI
        fileUri = uploadUri

        // Clear the last download, if any
        updateUI()
        downloadUrl = null

        // Start MyUploadService to upload the file, so that the file is uploaded
        // even if this Activity is killed or put in the background
        startService(Intent(this, MyUploadService::class.java)
            .putExtra(MyUploadService.EXTRA_FILE_URI, uploadUri)
            .setAction(MyUploadService.ACTION_UPLOAD))

        // Show loading spinner
        showProgressDialog(getString(R.string.progress_uploading))
    }

    private fun beginDownload() {
        fileUri?.let {
            // Get path
            val path = "photos/" + getFilename(contentResolver, it)

            // Kick off MyDownloadService to download the file
            val intent = Intent(this, MyDownloadService::class.java)
                .putExtra(MyDownloadService.EXTRA_DOWNLOAD_PATH, path)
                .setAction(MyDownloadService.ACTION_DOWNLOAD)
            startService(intent)

            // Show loading spinner
            showProgressDialog(getString(R.string.progress_downloading))
        }
    }

    private fun launchCamera() {
        Log.d(TAG, "launchCamera")

        // Pick an image from storage
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        startActivityForResult(intent, RC_TAKE_PICTURE)
    }

    private fun onUploadResultIntent(intent: Intent) {

        // Got a new intent from MyUploadService with a success or failure
        downloadUrl = intent.getParcelableExtra(MyUploadService.EXTRA_DOWNLOAD_URL)
        fileUri = intent.getParcelableExtra(MyUploadService.EXTRA_FILE_URI)

        updateUI()
    }

    private fun updateUI() {
        // Download URL and Download button
        if (downloadUrl != null) {
            pictureDownloadUri.text = downloadUrl.toString()
            layoutDownload.visibility = View.VISIBLE
        } else {
            pictureDownloadUri.text = null
            layoutDownload.visibility = View.GONE
        }
    }

    private fun showMessageDialog(title: String, message: String) {
        val ad = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .create()
        ad.show()
    }

    private fun showProgressDialog(caption: String) {
        progressDialog.setMessage(caption)
        progressDialog.show()
    }

    private fun hideProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    override fun onClick(v: View) {
        val i = v.id
        when (i) {
            R.id.buttonCamera -> launchCamera()
            R.id.buttonDownload -> beginDownload()
        }
    }

    companion object {

        private const val TAG = "Storage#MainActivity"

        private const val RC_TAKE_PICTURE = 101

        private const val KEY_FILE_URI = "key_file_uri"
        private const val KEY_DOWNLOAD_URL = "key_download_url"
    }
}