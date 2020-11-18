package com.e.myapplicationtest

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.e.myapplicationtest.Classifier.Classifier
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileDescriptor
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private lateinit var classifier: Classifier
    private val RESULT_IMAGEFILE = 1001 //画像取得時に使用するリクエストコード

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        textView = findViewById(R.id.textView)

        try {
            classifier = Classifier.create(this, Classifier.Device.CPU, 2)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        button.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, RESULT_IMAGEFILE)
        }

    }

    //別アクティビティから戻ってきたときの処理　リクエストコードで認識する
    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
//        val newRequestCode = requestCode and 0xffff
        lateinit var bmp: Bitmap
        lateinit var results: List<Classifier.Recognition>
        var resulttext: String? = ""

        resultData?.let { resultData ->
            if (resultCode == Activity.RESULT_OK) {
                //終了リザルトが画像選択アクテビティ
                if (requestCode == RESULT_IMAGEFILE) {
                    var uri: Uri? = resultData.data
                    var pfDescriptor = contentResolver.openFileDescriptor(uri!!, "r")
                    val fileDescriptor: FileDescriptor = pfDescriptor!!.fileDescriptor
                    bmp = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                    pfDescriptor.close()
                }
                imageView.setImageBitmap(bmp)
                results =
                    classifier.recognizeImage(bmp, 1)
                resulttext += results[0]
                this.textView.text = resulttext
            }
        }
    }
}
