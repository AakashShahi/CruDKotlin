package com.example.myapplication.repository

import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.myapplication.model.ProductModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class ProductRepositoryImpl:ProductRepository {
    var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    var ref: DatabaseReference = firebaseDatabase.reference.child("products")

    var firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    var storageReference : StorageReference = firebaseStorage.reference.child("products")

    override fun addProducts(productModel: ProductModel, callback: (Boolean, String?) -> Unit) {
        var id = ref.push().key.toString()
        productModel.id = id

        Log.d("data",productModel.toString())
        ref.child(id).setValue(productModel).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("data","DATA SAVED")
                callback(true,"Data saved successfully")
            } else {
               callback(false,"Data not saved")
            }
        }
    }

    override fun uploadImages(imageUri: Uri, callback: (Boolean, String?, String?) -> Unit) {
        var imageName = UUID.randomUUID().toString()
        var imageReference = storageReference.child("products").child(imageName)

        imageUri?.let { url ->
            imageReference.putFile(url).addOnSuccessListener {
                callback(true,imageName,"")

                imageReference.downloadUrl.addOnSuccessListener { url ->
                    var imageUrl = url.toString()
                    Log.d("error","i am inside")
                    callback(true,imageName,imageUrl)
                }
            }.addOnFailureListener {
                callback(false,"","")
            }
        }
    }

}