package com.app.imageconvert2

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent,120)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode ==120 && resultCode == RESULT_OK&& data!=null){
            val selectedImageUrl = data.data
            val filepath = arrayOf(MediaStore.Images.Media.DATA)

            val cursor = contentResolver.query(selectedImageUrl!!,filepath,null,null,null)
            cursor!!.moveToFirst()
            val columIndex = cursor!!.getColumnIndex(filepath[0].toString())
            val myPath = cursor!!.getString(columIndex)
            cursor.close()
            var bitmap = BitmapFactory.decodeFile(myPath)
            image.setImageBitmap(bitmap)


            val pdfDocument = PdfDocument()
            val pageinfo = PdfDocument.PageInfo.Builder(960, 1280, 1).create()
            val page = pdfDocument.startPage(pageinfo)
            val canvas = page.canvas
            val paint = Paint()
//            paint.set(Color.parseColor("#FFFFFF"))

            canvas.drawPaint(paint)

            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width,bitmap.height,true)
            paint.setColor(Color.BLUE)
            canvas.drawBitmap(bitmap,0f,0f,null)
            pdfDocument.finishPage(page)


            //save bitmap image
            val root = File(Environment.getExternalStorageDirectory(),"PDF Folder 12")
            if (!root.exists()){
                root.mkdirs()
            }
            val alphabet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
            val randomString: String = List(20) { alphabet.random() }.joinToString("")
            val file = File(root,"$randomString.pdf")
            try {
                val fileOutputStream = FileOutputStream(file)
                pdfDocument.writeTo(fileOutputStream)
            }catch (e:Exception){
                e.printStackTrace()
            }
            pdfDocument.close()



        }
    }
}