package com.example.qylixSystem.currencies

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qylixSystem.MainViewModel
import com.example.qylixSystem.R
import com.example.qylixSystem.ViewModelFactory
import com.example.qylixSystem.model.Currency
import com.example.qylixSystem.repository.SharedPreferencesRepository
import kotlinx.android.synthetic.main.fragment_adapter.*
import java.text.SimpleDateFormat
import java.util.*


class CurFragment : Fragment() {
    private lateinit var viewModel: MainViewModel
    private lateinit var curAdapter: CurAdapter

    private val pref = SharedPreferencesRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this.requireActivity().viewModelStore,
            ViewModelFactory()
        ).get(MainViewModel::class.java)
        curAdapter = CurAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_adapter, container, false)
        val recyclerview = view.findViewById<RecyclerView>(R.id.recyclerview)
        recyclerview.layoutManager = LinearLayoutManager(this.context)
        recyclerview.adapter = curAdapter



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currenciesXml.observe(viewLifecycleOwner, getString)

        init()

    }


    fun init() {

        val date = Date()
        val cal = Calendar.getInstance()
        val format = SimpleDateFormat("MM.dd.yyyy")
        val format2 = SimpleDateFormat("dd.MM.yyyy")
        val currentDate = format.format(date)
        if (viewModel.getCurrencies(currentDate)) {
            cal.time = date
            cal.add(Calendar.DATE, +1)
            date1.text = format2.format(date)
            date2.text = format2.format(cal.time)
        } else {
            cal.time = date
            cal.add(Calendar.DATE, -1)
            date1.text = format2.format(cal.time)
            date2.text = format2.format(date)
        }
    }


    private val getString = Observer<MutableList<Currency>> {

        curAdapter.currencies = loadPref(it)
        curAdapter.notifyDataSetChanged()
    }

    fun loadPref(it: MutableList<Currency>): MutableList<Currency> {
        val newList = mutableListOf<Currency>()
        val curDefault = mutableListOf("RUB", "USD", "EUR")
        if (!pref.getBoolean("FirstStart")) {
            pref.putBoolean("FirstStart", true)
            it.forEachIndexed { i, it ->
                pref.putInt(it.charCode + "_pos",i)
                if(curDefault.contains(it.charCode)){
                    pref.putBoolean(it.charCode, true)
                    newList.add(it)
                }
            }
            pref.saveData()
        } else {
            val sortedList = mutableMapOf<Int,Currency>()
            it.forEach {
                val KEY = it.charCode
                if (pref.getBoolean(KEY)) {
                    sortedList.put( pref.getInt(KEY+"_pos"),it)
                }
            }
            newList.addAll(sortedList.toSortedMap().values)
        }
        return newList
    }
}