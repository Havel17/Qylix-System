package com.example.qylixSystem

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.qylixSystem.repository.RepositoryService

class ViewModelFactory() : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == MainViewModel::class.java)
            MainViewModel(Application()) as T
        else throw IllegalArgumentException("Wrong ViewModel class")
    }
}