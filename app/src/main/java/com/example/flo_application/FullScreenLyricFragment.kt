package com.example.flo_application

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_total_lyric.view.*

class FullScreenLyricFragment : Fragment() {
    private lateinit var activity: MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    fun loadLyrics(lyrics:ArrayList<String>,transaction: FragmentTransaction,state:Int){
        transaction.replace(R.id.frameLyricList,
            LyricFragment().apply {
                arguments = Bundle().apply{
                    putSerializable("lyrics",lyrics)
                    putInt("touchMode",state)
                }
            }).commitAllowingStateLoss()
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity =  context as MainActivity
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.activity_total_lyric, null)
        val lyrics = arguments?.getSerializable("lyrics") as ArrayList<String>
        loadLyrics(lyrics,childFragmentManager.beginTransaction(), if(fragmentView.touchPlayBtn.isChecked) 1 else 0)
        fragmentView.touchPlayBtn.setOnCheckedChangeListener { compoundButton, b ->
            if(b){
                loadLyrics(lyrics,childFragmentManager.beginTransaction(),1)
            }else{
                loadLyrics(lyrics,childFragmentManager.beginTransaction(),0)
            }
        }
        fragmentView.closeBtn.setOnClickListener {
            // 뒤로가기
            this.fragmentManager?.popBackStack()
            activity.showFullLyricBtn()
        }
        return fragmentView
    }

    override fun onPause() {
        super.onPause()
        activity.showFullLyricBtn()
    }

}