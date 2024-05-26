package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMain2Binding
import com.example.myapplication.model.ProductModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity2 : AppCompatActivity() {
    lateinit var main2Binding: ActivityMain2Binding
    var firebaseDatabase : FirebaseDatabase=FirebaseDatabase.getInstance()
    var ref: DatabaseReference =firebaseDatabase.reference.child("products")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        main2Binding=ActivityMain2Binding.inflate(layoutInflater)
        setContentView(main2Binding.root)

        main2Binding.button.setOnClickListener {
            var name: String=main2Binding.editTextText2.text.toString()
            var price: String=main2Binding.editTextText3.text.toString()
            var desc: String=main2Binding.editTextText4.text.toString()

            var id=ref.push().key

            var data= ProductModel(price,name,desc)
            if (id != null) {
                ref.child(id).setValue(data).addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(applicationContext,"Product Added",Toast.LENGTH_LONG).show()
                        finish()
                    } else{
                        Toast.makeText(applicationContext,it.exception?.message,Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}