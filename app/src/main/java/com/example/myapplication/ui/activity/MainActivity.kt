package com.example.myapplication.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.ProductAdapter
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.model.ProductModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MainActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityMainBinding
    var firebaseDatabase: FirebaseDatabase= FirebaseDatabase.getInstance()
    var ref: DatabaseReference= firebaseDatabase.reference.child("products")

    var firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    var storageReference: StorageReference = firebaseStorage.reference
    var productList=ArrayList<ProductModel>()

    lateinit var productAdapter: ProductAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        productAdapter=ProductAdapter(this@MainActivity,productList)

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
                        productList.add(product)
                    }
                    mainBinding.recyclerView.adapter = productAdapter
                    mainBinding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,error.message,Toast.LENGTH_SHORT).show()
            }
        })

        ItemTouchHelper(object:ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                AlertDialog.Builder(this@MainActivity).setTitle("Delete").setMessage("Are you sure you want to delete this product?")
                    .setPositiveButton("Yes"){
                        dialog,which->
                        var id = productAdapter.getProductId(viewHolder.adapterPosition)
                        var imageName=productAdapter.getImageName(viewHolder.adapterPosition)
                        ref.child(id).removeValue().addOnCompleteListener {
                            if(it.isSuccessful){
                                storageReference.child("products").child(imageName).delete()
                                productAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                            }
                        }
                    }
                    .setNegativeButton("No"){
                        dialog,which->
                        productAdapter.notifyItemChanged(viewHolder.adapterPosition)
                    }.show()

            }
        }).attachToRecyclerView(mainBinding.recyclerView)

        mainBinding.floatingActionButton.setOnClickListener{
            var intent = Intent(this@MainActivity, MainActivity2::class.java)
            startActivity(intent)
        }
    }
}