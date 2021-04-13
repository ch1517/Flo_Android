package com.example.flo_application

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_total_lyric.view.*

class FullScreenLyricFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    fun loadLyrics(lyrics:ArrayList<String>,transaction: FragmentTransaction,state:Boolean){
        transaction.replace(R.id.frameLyricList,
            TotalLyricFragment().apply {
                arguments = Bundle().apply{
                    putSerializable("lyrics",lyrics)
                    putBoolean("touchMode",state)
                }
            }).commitAllowingStateLoss()
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.activity_total_lyric, null)
        val lyrics = arguments?.getSerializable("lyrics") as ArrayList<String>
        loadLyrics(lyrics,childFragmentManager.beginTransaction(),fragmentView.touchPlayBtn.isChecked)

        fragmentView.touchPlayBtn.setOnCheckedChangeListener { compoundButton, b ->
            if(b){
                loadLyrics(lyrics,childFragmentManager.beginTransaction(),true)
            }else{
                loadLyrics(lyrics,childFragmentManager.beginTransaction(),false)
            }
        }
        fragmentView.closeBtn.setOnClickListener {
            // 뒤로가기
            this.fragmentManager?.popBackStack()
        }
        return fragmentView
    }

}