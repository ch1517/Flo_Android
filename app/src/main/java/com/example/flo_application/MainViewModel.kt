package com.example.flo_application

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel:ViewModel(){
    private val  playPosition: MutableLiveData<Int> = MutableLiveData<Int>() // 재생위치
    private var touchToggle: MutableLiveData<Int> = MutableLiveData<Int>() // 터치모드 switch toggle state

    fun getPlayPosition():LiveData<Int>{
        return playPosition
    }
    fun setPlayPosition(position:Int){
        playPosition.value = position
    }

    fun getTouchToggle():LiveData<Int>{
        return touchToggle
    }
    fun setTouchToggle(state:Int){
        touchToggle.value = state
    }

}