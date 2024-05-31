package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

class MainActivity(dragDirs: Int) : AppCompatActivity() {
    lateinit var mainBinding: ActivityMainBinding
    var firebaseDatabase: FirebaseDatabase= FirebaseDatabase.getInstance()
    var ref: DatabaseReference= firebaseDatabase.reference.child("products")
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
                    }
                    mainBinding.recyclerView.adapter = productAdapter
                    mainBinding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        ItemTouchHelper(object:ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                AlertDialog.Builder(this@MainActivity).setTitle("Delete").setMessage("Are you sure you want to delete this product?")
                    .setPositiveButton("Yes"){
                        dialog,which->
                        var id = productAdapter.getProductId(viewHolder.adapterPosition)
                        ref.child(id).removeValue()
                        productAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                    }
                    .setNegativeButton("No"){
                        dialog,which->
                        productAdapter.notifyItemChanged(viewHolder.adapterPosition)
                    }.show()

            }
        }).attachToRecyclerView(mainBinding.recyclerView)

        mainBinding.floatingActionButton.setOnClickListener{
            var intent = Intent(this@MainActivity,MainActivity2::class.java)
            startActivity(intent)
        }
    }
}