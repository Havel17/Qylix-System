package com.example.qylixSystem.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qylixSystem.MainViewModel
import com.example.qylixSystem.R
import com.example.qylixSystem.ViewModelFactory
import com.example.qylixSystem.model.Currency
import com.example.qylixSystem.repository.SharedPreferencesRepository
import java.util.*

class SetFragment : Fragment() ,SetAdapter.OnStartDragListener{
    private lateinit var viewModel: MainViewModel
    private lateinit var setAdapter: SetAdapter
    private lateinit var  myHelper: ItemTouchHelper
    private val pref = SharedPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this.requireActivity().viewModelStore,
            ViewModelFactory()
        ).get(MainViewModel::class.java)
        setAdapter = SetAdapter(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_adapter, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerview = view.findViewById<RecyclerView>(R.id.recyclerview)

        recyclerview.layoutManager = LinearLayoutManager(this.context)
        recyclerview.adapter = setAdapter
        myHelper = ItemTouchHelper(myCallback)
        myHelper.attachToRecyclerView(recyclerview)
        val sortedList = mutableMapOf<Int, Currency>()
        viewModel.currenciesXml.value!!.forEach {
            sortedList.put( pref.getInt(it.charCode+"_pos"),it)
        }
        val newList = mutableListOf<Currency>()
        newList.addAll(sortedList.toSortedMap().values)
        setAdapter.currencies=  newList
        setAdapter.notifyDataSetChanged()



    }
    val myCallback = object: ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            return makeMovementFlags(dragFlags,0)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val firstPos= viewHolder.absoluteAdapterPosition
            val secondPos = target.absoluteAdapterPosition
            Collections.swap(setAdapter.currencies,firstPos,secondPos)
            setAdapter.notifyItemMoved(firstPos,secondPos)
            SharedPreferencesRepository.putInt(setAdapter.currencies[firstPos].charCode+"_pos",firstPos)
            SharedPreferencesRepository.putInt(setAdapter.currencies[secondPos].charCode+"_pos",secondPos)
            return true
        }

        override fun isLongPressDragEnabled(): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        }


    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        myHelper.startDrag(viewHolder)
    }

}