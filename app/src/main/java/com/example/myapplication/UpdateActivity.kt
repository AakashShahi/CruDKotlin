package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.databinding.ActivityUpdateBinding
import com.example.myapplication.model.ProductModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UpdateActivity : AppCompatActivity() {
    lateinit var upadteBinding: ActivityUpdateBinding
    var id=""
    var firebaseDatabase: FirebaseDatabase=FirebaseDatabase.getInstance()
    var ref: DatabaseReference =firebaseDatabase.getReference("products")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        upadteBinding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(upadteBinding.root)

        var product: ProductModel=intent.getParcelableExtra("product")!!
        upadteBinding.editTextText22.setText(product.name)
        upadteBinding.editTextText33.setText(product.price)
        upadteBinding.editTextText44.setText(product.desc)
        id=product.id.toString()

        upadteBinding.button.setOnClickListener {
            var udname=upadteBinding.editTextText22.text.toString()
            var udprice=upadteBinding.editTextText33.text.toString()
            var uddesc=upadteBinding.editTextText44.text.toString()

            var updatedMap=mutableMapOf<String,Any>()
            updatedMap["name"]=udname
            updatedMap["price"]=udprice
            updatedMap["desc"]=uddesc
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

    }
}