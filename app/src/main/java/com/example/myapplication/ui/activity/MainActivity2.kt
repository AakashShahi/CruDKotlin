package com.example.myapplication.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityMain2Binding
import com.example.myapplication.model.ProductModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.UUID

class MainActivity2 : AppCompatActivity() {
    lateinit var addProductBinding: ActivityMain2Binding
    var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    var ref: DatabaseReference = firebaseDatabase.reference.child("products")

    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    var imageUri: Uri? = null

    var firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    var storageReference = firebaseStorage.reference

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            var intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addProductBinding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(addProductBinding.root)

        registerActivityForResult()

        addProductBinding.imageView.setOnClickListener {
            var permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                android.Manifest.permission.READ_MEDIA_IMAGES
            } else {
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            }
            if (ContextCompat.checkSelfPermission(this, permissions) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(permissions), 1)
            } else {
                var intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                activityResultLauncher.launch(intent)
            }
        }

        addProductBinding.button.setOnClickListener {
            if (imageUri != null) {
                uploadPhoto()
            } else {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show()
            }
        }

    }

     fun addProducts(url: String,imageName: String) {
        Log.d("errorr","add product function called")
        var name: String = addProductBinding.editTextText2.text.toString()
        var price: String = addProductBinding.editTextText3.text.toString()
        var description: String = addProductBinding.editTextText4.text.toString()

        var id = ref.push().key.toString()
        var data = ProductModel(id, name, price, description, url,imageName)

         Log.d("data",data.toString())
        ref.child(id).setValue(data).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("data","DATA ASAVED")
                Toast.makeText(applicationContext, "Data Saved", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(applicationContext, it.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun registerActivityForResult() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->

                var resultCode = result.resultCode
                var imageData = result.data
                if (resultCode == RESULT_OK && imageData != null) {
                    imageUri = imageData.data
                    imageUri?.let {
                        Picasso.get().load(it).into(addProductBinding.imageView)
                    }
                }
            })
    }

    private fun uploadPhoto() {
        var imageName = UUID.randomUUID().toString()
        var imageReference = storageReference.child("products").child(imageName)

        imageUri?.let { url ->
            imageReference.putFile(url).addOnSuccessListener {
                Toast.makeText(applicationContext, "Image Uploaded", Toast.LENGTH_LONG).show()

                imageReference.downloadUrl.addOnSuccessListener { url ->
                    var imageUrl = url.toString()
                    Log.d("errorr","i am inside")
                    addProducts(imageUrl,imageName)
                }
            }.addOnFailureListener {
                Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
}