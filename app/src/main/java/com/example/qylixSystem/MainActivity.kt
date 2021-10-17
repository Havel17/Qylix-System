package com.example.qylixSystem

import android.content.Context
import android.net.ConnectivityManager
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet.GONE
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.qylixSystem.currencies.CurFragment
import com.example.qylixSystem.repository.SharedPreferencesRepository
import com.example.qylixSystem.settings.SetFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    // true: открыть настройки: false: открыть курсы валют
    private var event = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory()
        ).get(MainViewModel::class.java)

        viewModel.isError.observe(this, isError)
        SharedPreferencesRepository.init(this)
        init()

    }

    fun init() {
        var fr: Fragment = CurFragment()
        openFr(fr)
        setting.setOnClickListener {
            if (event) {
                setting.setImageResource(android.R.drawable.ic_menu_save)
                fr = SetFragment(); event = !event
            } else {
                setting.setImageResource(android.R.drawable.ic_menu_preferences)
                SharedPreferencesRepository.saveData()
                fr = CurFragment();event = !event
            }
            openFr(fr)
        }
    }

    fun openFr(fr: Fragment) {
        if (isInternetAvailable(baseContext)) {
            supportFragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .replace(
                    R.id.recycler,
                    fr
                )
                .commit()
        } else {
            val frag = supportFragmentManager.findFragmentById(R.id.recycler)
            if (frag != null) {
                supportFragmentManager
                    .beginTransaction()
                    .remove(frag)
                    .commit()
            }
                setting.visibility = View.GONE
                Toast.makeText(baseContext, "Нет интернет соединения", Toast.LENGTH_LONG).show();
        }
    }


    private val isError = Observer<Boolean> {
        val frag = supportFragmentManager.findFragmentById(R.id.recycler)
        if (frag != null) {
            supportFragmentManager
                .beginTransaction()
                .remove(frag)
                .commit()
        }
        setting.visibility = View.GONE
        Toast.makeText(baseContext, "Не удалось получить курсы валют", Toast.LENGTH_LONG).show();

    }

    fun isInternetAvailable(context: Context): Boolean {
        val mConMgr = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return (mConMgr.activeNetworkInfo != null && mConMgr.activeNetworkInfo!!.isAvailable
                && mConMgr.activeNetworkInfo!!.isConnected)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (!event) {
                    event = !event
                    onBackPressed()
                    true
                } else {
                    super.onOptionsItemSelected(item)
                }
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}