package com.hwichance.android.mindblooming.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment.getExternalStorageDirectory
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.custom_views.FlexibleLayout
import java.io.File
import java.io.FileOutputStream

class PictureUtils {
    companion object {
        private const val APP_NAME = "mind_blooming"
        private const val APP_PATH = "/Mind Blooming"

        fun getBitmapImage(layout: FlexibleLayout): Bitmap {
            return Bitmap.createBitmap(layout.width, layout.height, Bitmap.Config.ARGB_8888).apply {
                Canvas(this).apply {
                    this.drawColor(Color.WHITE)
                    layout.setFullView()
                    layout.draw(this)
                    layout.restoreView()
                }
            }
        }

        fun saveBitmapAsImageBeforeQ(context: Context, bitmap: Bitmap) {
            val fileName = DateTimeUtils.convertDateToFileName(APP_NAME, System.currentTimeMillis())
            val rootPath = getExternalStorageDirectory().absolutePath
            val dirPath = "${rootPath}/${DIRECTORY_PICTURES}/${APP_PATH}"
            val filePath = "${dirPath}/${fileName}"

            with(File(dirPath)) {
                if (!this.exists()) {
                    this.mkdir()
                }
            }

            try {
                with(File(filePath)) {
                    createNewFile()
                    FileOutputStream(this).let { fos ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                        fos.close()
                    }
                    context.sendBroadcast(
                        Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.fromFile(this)
                        )
                    )
                }

                Toast.makeText(context, R.string.saved_as_image, Toast.LENGTH_SHORT).show()
            } catch (e: java.lang.Exception) {
                Log.e("SAVE_AS_FILE", "error=${e.localizedMessage}")
            }
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        fun saveBitmapAsImageAfterQ(context: Context, resolver: ContentResolver, bitmap: Bitmap) {
            val fileName = DateTimeUtils.convertDateToFileName(APP_NAME, System.currentTimeMillis())
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/*")
                put(MediaStore.Images.Media.RELATIVE_PATH, DIRECTORY_PICTURES + APP_PATH)
                put(MediaStore.Images.Media.IS_PENDING, 1)

            }

            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            try {
                if (uri != null) {
                    val descriptor = resolver.openFileDescriptor(uri, "w")
                    if (descriptor != null) {
                        FileOutputStream(descriptor.fileDescriptor).let { fos ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                            fos.close()
                        }
                    }

                    values.clear()
                    values.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(uri, values, null, null)

                    Toast.makeText(context, R.string.saved_as_image, Toast.LENGTH_SHORT).show()
                }
            } catch (e: java.lang.Exception) {
                Log.e("SAVE_AS_FILE", "error=${e.localizedMessage}")
            }
        }
    }
}