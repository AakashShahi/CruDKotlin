package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.ProductModel

class ProductAdapter(var data: ArrayList<ProductModel>):RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var name: TextView= view.findViewById(R.id.namelabl)
        var price: TextView= view.findViewById(R.id.pricelbl)
        var description: TextView= view.findViewById(R.id.desclbl)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.samplelayout,parent,false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.name.text = data[position].name
        holder.price.text = data[position].price
        holder.description.text = data[position].desc
    }
}