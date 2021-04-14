package com.example.flo_application

import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.view.size
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.custom_list_item.view.*
import kotlinx.android.synthetic.main.fragment_lyric.view.*
import kotlin.collections.ArrayList
import android.os.Handler
import android.util.Half
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.core.view.get
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

data class Lyrics(var index:Int, var lyricStr:String)
class LyricFragment : Fragment() {
    private lateinit var activity: MainActivity
    private lateinit var viewModel: MainViewModel
    private var COLOR:Int = Color.BLACK
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_lyric, null)
        val lyricsInfo = arguments?.getSerializable("lyrics") as ArrayList<String>
        val touchMode = arguments?.getInt("touchMode", 0)
        val lyrics = ArrayList<Lyrics>()
        var c=0
        for (i in 1..lyricsInfo.size - 1 step 2) {
            lyrics.add(Lyrics(c++,lyricsInfo.get(i)))
        }
        var adapter = ListViewAdapter(inflater, lyrics)
        fragmentView.frameLyricList.adapter = adapter
        fragmentView.frameLyricList.setOnItemClickListener { parent, view, position, id ->
            if (touchMode == 1) {
                COLOR = R.color.colorBlue
                // 재생구간 변경하기
                activity.setSeekTo(Integer.parseInt(lyricsInfo[position * 2]))
                textColorSetting(fragmentView,view)
            } else if (touchMode == 0) {
                COLOR = Color.BLACK
                // 뒤로가기
                activity.onBackPressed()
                activity.showFullLyricBtn()
            } else {
                COLOR = Color.BLACK

            }
        }
        updateLyricPosition(fragmentView,adapter, lyrics, touchMode!!)

        return fragmentView
    }
    // 현재 재생구간 or 터치 구간 색 변경
    fun textColorSetting(view:View, selectView:View){
        for (i in 0..view.frameLyricList.size-1){
            view.frameLyricList.getChildAt(i).textView.setTextColor(Color.GRAY)
        }
        selectView.textView.setTextColor(COLOR)
    }
    fun updateLyricPosition(view: View,adapter:ListViewAdapter, lyrics: ArrayList<Lyrics>,touchMode:Int) {
        viewModel = ViewModelProvider(
            activity,
            ViewModelProvider.NewInstanceFactory()
        ).get(MainViewModel::class.java)

        viewModel.getPlayPosition().observe(activity, Observer { position ->
            val arrayIndex = position/2
            adapter.setSelectIndex(arrayIndex)
            if(touchMode==1){
                // touchMode의 경우 focus 이동은 하지 않는다.
                // 현재 재생 중인 가사 index와 화면에 표시되어 있는 가사 index가 일치하는 경우 text Color 변경
                for (i in 0..view.frameLyricList.size-1){
                    var lyric = view.frameLyricList.getItemAtPosition(i) as Lyrics
                    if(lyric.index == arrayIndex){
                        view.frameLyricList.getChildAt(i).textView.setTextColor(COLOR)
                    }else{
                        view.frameLyricList.getChildAt(i).textView.setTextColor(Color.GRAY)
                    }
                }
                adapter.notifyDataSetChanged()
            }else{
                if (position > -1 && arrayIndex < lyrics.size && view.frameLyricList.size != 0) {
                    val height = view.frameLyricList.height
                    val itemHeight = view.frameLyricList.getChildAt(0).height
                    view.frameLyricList.setSelectionFromTop(arrayIndex, height / 2 - itemHeight / 2)
                    val HalfSize = (view.frameLyricList.size+1)/2
                    if(arrayIndex<HalfSize){ // 화면의 중간 가사보다 arrayIndex이 작다면, 해당 position의 text color만 변경
                        textColorSetting(view,view.frameLyricList.getChildAt(arrayIndex))
                    }else if(arrayIndex>=lyrics.size-HalfSize){ // 화면의 중간 가사보다 arrayIndex가 크다면 이후 인덱스 text color만 변경
                        textColorSetting(view,view.frameLyricList.getChildAt(view.frameLyricList.size-(lyrics.size-arrayIndex)))
                    }
                    else{
                        textColorSetting(view,view.frameLyricList.getChildAt(HalfSize))
                    }
                }
            }
        })
    }
    inner class ListViewAdapter(inflater: LayoutInflater, private val items:ArrayList<Lyrics>):BaseAdapter(){
        private val mLayoutInflater: LayoutInflater
        private var mSelectIndex: Int
        init {
            mLayoutInflater = inflater
            mSelectIndex = -1
        }
        fun setSelectIndex(selectIndex:Int){
            mSelectIndex = selectIndex
        }
        override fun getCount(): Int {
            return items.size
        }
        override fun getItem(position: Int): Lyrics {
            return items.get(position)
        }
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
        override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
            val convertView = mLayoutInflater.inflate(R.layout.custom_list_item, parent, false)
            convertView.textView.text = items[position].lyricStr
            if(mSelectIndex==position){
                convertView.textView.setTextColor(COLOR)
            }else{
                convertView.textView.setTextColor(Color.GRAY)
            }
            return convertView
        }
    }
}
