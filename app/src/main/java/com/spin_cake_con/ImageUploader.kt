package com.spin_cake_con

import java.io.File

//An object for uploading file to Transfer.sh
interface ImageUploader {
    fun upload(file: File, callback: UploadCallback): Boolean

    interface UploadCallback {
        fun onResult(responseStr: String)
    }
}
