package com.example.newcameraapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Build a button to open camera to take picture
        findViewById<Button>(R.id.button).setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    private val REQUEST_IMAGE_CAPTURE = 1

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    //Get Thumbnail
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            val imageBitmap = data?.extras?.get("data") as Bitmap

            findViewById<ImageView>(R.id.imageView).setImageBitmap(imageBitmap)

            val image = InputImage.fromBitmap(imageBitmap, 0)

            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

            labeler.process(image)
                .addOnSuccessListener { labels ->
                    // Task completed successfully
                    Log.i("Dom", "successfully processed image")
                    for (label in labels) {
                        val text = label.text
                        val confidence = label.confidence.toString()
                        val index = label.index
                        val textView = findViewById<TextView>(R.id.textView)
                        Log.i("Dom", "detected: " + text + " with confidence: " + confidence)
                        if (confidence > 0.9.toString())
                        textView.text = "Detected " + text + " with confidence " + confidence
                        textView.movementMethod = ScrollingMovementMethod()

                    }

                }
                        .addOnFailureListener { e ->
                    // Task failed with an exception
                    Log.i("Dom", "Error processing")
                }

        }
        }
    }
