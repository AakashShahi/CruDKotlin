package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.ProductAdapter
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.model.ProductModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityMainBinding
    var firebaseDatabase: FirebaseDatabase= FirebaseDatabase.getInstance()
    var ref: DatabaseReference= firebaseDatabase.reference.child("products")
    var productList=ArrayList<ProductModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                var productList = ArrayList<ProductModel>()
                for (eachData in snapshot.children){
                    var product = eachData.getValue(ProductModel::class.java)
                    productList.add(product!!)
                    if (product!=null){
                        Log.d("my data",product.name)
                        Log.d("my data",product.price)
                        Log.d("my data",product.desc)
                    }
                    var adapter=ProductAdapter(productList)
                    mainBinding.recyclerView.adapter = adapter
                    mainBinding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        mainBinding.floatingActionButton.setOnClickListener{
            var intent = Intent(this@MainActivity,MainActivity2::class.java)
            startActivity(intent)
        }
    }
}