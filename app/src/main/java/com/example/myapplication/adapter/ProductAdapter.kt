package com.example.myapplication.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.ui.activity.UpdateActivity
import com.example.myapplication.model.ProductModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class ProductAdapter(var context: Context, var data: ArrayList<ProductModel>):RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.namelabl)
        var price: TextView = view.findViewById(R.id.pricelbl)
        var description: TextView = view.findViewById(R.id.desclbl)
        var edit: TextView = view.findViewById(R.id.editlbl)
        var imageView: ImageView = view.findViewById(R.id.imageView2)
        var progressBar: ProgressBar = view.findViewById(R.id.progressBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.sample_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.name.text = data[position].name
        holder.price.text = data[position].price
        holder.description.text = data[position].desc

        var imageURL=data[position].url
        Picasso.get().load(imageURL).into(holder.imageView,object: Callback {
            override fun onSuccess() {
                holder.progressBar.visibility = View.GONE
            }

            override fun onError(e: Exception?) {
                holder.progressBar.visibility = View.GONE
            }

        })

        holder.edit.setOnClickListener {
            var intent = Intent(context, UpdateActivity::class.java)
            intent.putExtra("product", data[position])
            context.startActivity(intent)
        }


    }
    fun getProductId(position:Int):String {
        return data[position].id
    }

    fun getImageName(position: Int): String{
        return data[position].imageName
    }
}
