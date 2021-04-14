package com.example.flo_application

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel:ViewModel(){
    private val  playPosition: MutableLiveData<Int> = MutableLiveData<Int>()

    public fun getPlayPosition():LiveData<Int>{
        return playPosition
    }
    public fun setPlayPosition(position:Int){
        playPosition.value = position
    }
}