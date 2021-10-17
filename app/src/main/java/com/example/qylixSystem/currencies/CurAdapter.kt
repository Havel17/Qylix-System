package com.example.qylixSystem.currencies

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Color.green
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qylixSystem.R
import com.example.qylixSystem.model.Currency
import kotlinx.android.synthetic.main.model_currency.view.*

class CurAdapter : RecyclerView.Adapter<CurAdapter.CurViewHolder>() {
    var currencies = mutableListOf<Currency>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.model_currency, parent, false)
        return CurViewHolder(view)
    }

    override fun getItemCount() = currencies.size

    override fun onBindViewHolder(holder: CurViewHolder, position: Int) {
        val currency = currencies[position]
        holder.bind(currency)
    }

     inner class CurViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val charCode = view.name
        private val scaleName = view.scaleName
        private val rate1 = view.rate1
        private val rate2 = view.rate2
        @SuppressLint("ResourceAsColor")
        fun bind(currency: Currency) {
            charCode.text = currency.charCode
            scaleName.text = currency.scale.toString() + " "+ currency.name
            if(currency.rate1>=currency.rate2){
                rate2.setTextColor(Color.rgb(0,200,0))
            }else{
                rate2.setTextColor(Color.rgb(200,0,0))
            }
            rate1.text = currency.rate1.toString()
            rate2.text = currency.rate2.toString()
        }
    }
}