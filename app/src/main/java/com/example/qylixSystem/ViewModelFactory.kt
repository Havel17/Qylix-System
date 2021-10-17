package com.example.qylixSystem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory() : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == MainViewModel::class.java)
            MainViewModel() as T
        else throw IllegalArgumentException("Wrong ViewModel class")
    }
}