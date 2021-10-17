package com.example.qylixSystem.settings

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qylixSystem.R
import com.example.qylixSystem.model.Currency
import com.example.qylixSystem.repository.SharedPreferencesRepository
import kotlinx.android.synthetic.main.model_currency.view.name
import kotlinx.android.synthetic.main.model_currency.view.scaleName
import kotlinx.android.synthetic.main.model_setting.view.*


class SetAdapter() : RecyclerView.Adapter<SetAdapter.SetViewHolder>() {
    var currencies = mutableListOf<Currency>()
    private val pref = SharedPreferencesRepository
    private lateinit var mDragStartListener: OnStartDragListener

    constructor(dragStartListener: OnStartDragListener) : this() {
        mDragStartListener = dragStartListener
    }

    interface OnStartDragListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.model_setting, parent, false)
        return SetViewHolder(view)
    }

    override fun getItemCount() = currencies.size

    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        val currency = currencies[position]
        holder.handleView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                mDragStartListener.onStartDrag(holder)
                return false
            }
        })
        holder.bind(currency)
    }

    inner class SetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val charCode = view.name
        private val scaleName = view.scaleName
        private val switch = view.switch1
        val handleView = view.handle
        fun bind(currency: Currency) {
            charCode.text = currency.charCode
            scaleName.text = currency.scale.toString() + " " + currency.name
            switch.isChecked = pref.getBoolean(currency.charCode)

            switch.setOnClickListener {
                pref.putBoolean(currency.charCode, switch.isChecked)
            }
        }
    }
}