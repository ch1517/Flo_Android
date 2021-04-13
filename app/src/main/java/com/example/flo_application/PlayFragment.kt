package com.example.flo_application

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.android.synthetic.main.fragment_play.view.*

class PlayFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_play, null)
        // 처리
        if(arguments!=null){
            val alumImgURL = arguments?.getString("alumImgURL")
            val singerTxt = arguments?.getString("singerTxt")
            val albumTxt = arguments?.getString("albumTxt")
            val titleTxt = arguments?.getString("titleTxt")
            val lyrics = arguments?.getSerializable("lyrics") as ArrayList<String>

            view.singerTxt.text = singerTxt
            view.albumTxt.text = albumTxt
            view.titleTxt.text = titleTxt


            val transaction = childFragmentManager.beginTransaction()
            transaction.replace(R.id.flgmentLyric,
                TotalLyricFragment().apply {
                    arguments = Bundle().apply{
                        putSerializable("lyrics",lyrics)
                        putBoolean("touchMode",false)
                    }
                }).commit()

            Glide.with(this).load(alumImgURL)
                .transform(CenterCrop(), RoundedCorners(30)).into(view.albumImg);
        }

        return view
    }

//    fun settingLyrics(view:View, duration: Int, lyrics: List<String>){
//            var i = 0
//            var index = -1
//            while(i<lyrics.size-1 && duration>Integer.parseInt(lyrics[i])){
//                index=i
//                i+=2
//            }
//            if(index>-1 && index<lyrics.size-1){
//                view.lyricsTxt.text = lyrics[index+1]
//            }
//    }
}