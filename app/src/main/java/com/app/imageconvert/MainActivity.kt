package com.app.imageconvert

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {
    var bitmap: Bitmap? = null

    val permissionOfarray = arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.CAMERA
    )
    val REQUEST_CODE = 200
    var isCamera = false
    var converterimage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        camera.setOnClickListener {
//            if (askPermission()) {
//                isCamera = false
//                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                resultLauncher.launch(intent)
//            }
//
//        }
//        gallary.setOnClickListener {
//            if (askPermission()) {
//                isCamera = true
//                val intent =
//                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                resultLauncher.launch(intent)
//            }
//        }
        camera.setOnClickListener {
            isCamera = true
            askPermission()
        }
        gallary.setOnClickListener {
            isCamera = false
            askPermission()
        }
        convert.setOnClickListener {
            if (bitmap != null) {

                var pdfDocument = PdfDocument()
                val pageinfo = PdfDocument.PageInfo.Builder(960, 1280, 1).create()
                val page = pdfDocument.startPage(pageinfo)
                val canvas = page.canvas
                val paint = Paint()
//                  paint.set(Color.parseColor("#FFFFFF"))
                canvas.drawPaint(paint)
                bitmap = Bitmap.createScaledBitmap(bitmap!!, 960, 1280, true)
                paint.setColor(Color.BLUE)
                canvas.drawBitmap(bitmap!!, 0f, 0f, null)
                pdfDocument.finishPage(page)


                val root = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    "PDF Converter"
                )
                if (!root.exists()) {
                    root.mkdirs()
                }
                val alphabet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
                 val randomString: String = List(20) { alphabet.random() }.joinToString("")
                val file = File(root, "$randomString.pdf")
                try {
                    val fileOutputStream = FileOutputStream(file)
                    pdfDocument.writeTo(fileOutputStream)
                    Toast.makeText(
                        this,
                        "Image Convert Successfully in PDF | internal mermery/Documents/my_directory",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Something Wrong, Please Try agin!!", Toast.LENGTH_LONG)
                        .show()
                }
                pdfDocument.close()
            } else {
                Toast.makeText(this, "Please Select the picture First", Toast.LENGTH_LONG).show()
            }
        }

//
//        //save bitmap image
//        val root = File(Environment.getExternalStorageDirectory(), "PDF Folder 12")
//        if (!root.exists()) {
//            root.mkdirs()
//        }
//        val alphabet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
//        val randomString: String = List(20) { alphabet.random() }.joinToString("")
//        val file = File(root, "$randomString.pdf")
//        try {
//            val fileOutputStream = FileOutputStream(file)
//            pdfDocument.writeTo(fileOutputStream)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        pdfDocument.close()
    }

    private fun askPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) + ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            + ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, permissionOfarray, REQUEST_CODE)
        } else {
            if (isCamera) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                resultLauncher.launch(intent)
            } else {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                resultLauncher.launch(intent)
            }


        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
//            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            if (isCamera) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                resultLauncher.launch(intent)
            } else {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                resultLauncher.launch(intent)
            }

        }
    }


    val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.e("resultLauncher", result.toString())
            when (result.resultCode) {
                Activity.RESULT_OK -> {

                    if (isCamera) {
                        bitmap = result.data?.getExtras()?.get("data") as Bitmap
                        // Set the image in imageview for display
                        // Set the image in imageview for display
                        image.setImageBitmap(bitmap)

                    } else {
                        val selectedImageUrl = result.data?.data
                        val filepath = arrayOf(MediaStore.Images.Media.DATA)
                        val cursor =
                            contentResolver.query(selectedImageUrl!!, filepath, null, null, null)
                        cursor!!.moveToFirst()
                        val columIndex = cursor!!.getColumnIndex(filepath[0].toString())
                        val myPath = cursor!!.getString(columIndex)
                        cursor.close()
                        bitmap = BitmapFactory.decodeFile(myPath)
                        image.setImageBitmap(bitmap)
                    }


                }
            }
        }
}