package com.example.myapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityUpdateBinding
import com.example.myapplication.model.ProductModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class UpdateActivity : AppCompatActivity() {
    lateinit var upadteBinding: ActivityUpdateBinding
    var id=""
    var firebaseDatabase: FirebaseDatabase=FirebaseDatabase.getInstance()
    var ref: DatabaseReference =firebaseDatabase.getReference("products")
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    var imageUri: Uri? = null
    var imageName=""

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
        upadteBinding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(upadteBinding.root)

        registerActivityForResult()

        var product: ProductModel=intent.getParcelableExtra("product")!!
        upadteBinding.editTextText22.setText(product.name)
        upadteBinding.editTextText33.setText(product.price)
        upadteBinding.editTextText44.setText(product.desc)

        Picasso.get().load(product.url).into(upadteBinding.updtImg)

        id=product?.id.toString()
        imageName=product?.imageName.toString()

        upadteBinding.button.setOnClickListener {
           uploadPhoto()
        }

        upadteBinding.updtImg.setOnClickListener {
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
                        Picasso.get().load(it).into(upadteBinding.updtImg)
                    }
                }
            })
    }

    private fun updateProduct(url:String){
        var udname=upadteBinding.editTextText22.text.toString()
        var udprice=upadteBinding.editTextText33.text.toString()
        var uddesc=upadteBinding.editTextText44.text.toString()

        var updatedMap=mutableMapOf<String,Any>()
        updatedMap["name"]=udname
        updatedMap["price"]=udprice
        updatedMap["desc"]=uddesc
        updatedMap["id"]=id
        updatedMap["url"]=url
        ref.child(id).updateChildren(updatedMap).addOnCompleteListener {
            if(it.isSuccessful){
                var intent=Intent(this,MainActivity::class.java)
                startActivity(intent)
                Toast.makeText(this,"Product Updated", Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Toast.makeText(this,"Product Not Updated", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadPhoto() {
        var imageReference = storageReference.child("products").child(imageName)

        imageUri?.let { url ->
            imageReference.putFile(url).addOnSuccessListener {
                Toast.makeText(applicationContext, "Image Uploaded", Toast.LENGTH_LONG).show()

                imageReference.downloadUrl.addOnSuccessListener { url ->
                    var imageUrl = url.toString()
                    updateProduct(imageUrl)
                }
            }.addOnFailureListener {
                Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
}