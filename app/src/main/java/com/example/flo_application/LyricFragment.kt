package com.example.flo_application

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
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
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

data class Lyrics(var index:Int, var lyricStr:String)
class LyricFragment : Fragment() {
    private lateinit var activity: MainActivity
    private lateinit var viewModel: MainViewModel
    private var COLOR:Int = Color.BLACK
    private lateinit var observer:Observer<Int>
    private lateinit var switchObserver:Observer<Int>
    private var firstLoad:Boolean = true

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity = context as MainActivity
    }

    override fun onPause() {
        super.onPause()
        // 해제 안해주면 fragment 변경해도 observer가 살아있음
        viewModel.getPlayPosition().removeObserver(observer)
        viewModel.getPlayPosition().removeObserver(switchObserver)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var fragmentView = inflater.inflate(R.layout.fragment_lyric, null)
        val lyricsInfo = arguments?.getSerializable("lyrics") as ArrayList<String>

        //argument touchMode 1(default) == fullScreen Lyric, -1 == playScreen Lyric
        var touchMode = arguments?.getInt("touchMode", 1)
        viewModel = ViewModelProvider(
            activity,
            ViewModelProvider.NewInstanceFactory()
        ).get(MainViewModel::class.java)

        // fullScreen의 경우

        if(touchMode == 1){
            if(viewModel.getTouchToggle().value!=null){
                touchMode = viewModel.getTouchToggle().value
            }else{
                touchMode = 0
            }
        }

        val lyrics = ArrayList<Lyrics>()
        var c=0
        for (i in 1..lyricsInfo.size - 1 step 2) {
            lyrics.add(Lyrics(c++,lyricsInfo.get(i)))
        }

        var adapter = ListViewAdapter(inflater, lyrics)
        fragmentView.frameLyricList.adapter = adapter

        fragmentView.frameLyricList.setOnItemClickListener { parent, view, position, id ->
            if (touchMode == 1) { // touchMode인 경우
                // 재생구간 변경하기
                activity.setSeekTo(Integer.parseInt(lyricsInfo[position * 2]))
                textColorSetting(fragmentView,view,-1)
            } else if (touchMode == 0) { // touchMode 아닌 경우
                // 뒤로가기
                activity.onBackPressed()
                activity.showFullLyricBtn()
            } else { // play부분에 mini 화면

            }
        }
        loadPage(touchMode,inflater,fragmentView,adapter,lyrics)

        switchObserver = Observer { _touchMode->
            if(_touchMode!=null && touchMode!=-1){
                touchMode = _touchMode
                loadPage(touchMode,inflater,fragmentView,adapter,lyrics)
            }
        }
        viewModel.getTouchToggle().observe(this,switchObserver)

        return fragmentView
    }
    fun loadPage(touchMode:Int?,inflater: LayoutInflater,fragmentView:View,adapter:ListViewAdapter,lyrics: ArrayList<Lyrics>){
        // touchMode인 경우 색상 변경
        if (touchMode == 1) {
            COLOR = ContextCompat.getColor(inflater.context,R.color.colorBlue)
        } else{
            COLOR = ContextCompat.getColor(inflater.context,R.color.colorAccent)
        }
        updateLyricPosition(fragmentView,adapter, lyrics, touchMode!!)
    }

    // 현재 재생구간 or 터치 구간 text color/bold 변경
    fun textColorSetting(view:View, selectView:View?, index: Int){
        if(index==-1){ // 실시간 자막 update
            for (i in 0..view.frameLyricList.size-1){
                view.frameLyricList.getChildAt(i).textView.setTextColor(Color.GRAY)
                view.frameLyricList.getChildAt(i).textView.setTypeface(null,Typeface.NORMAL)
            }
            selectView!!.textView.setTextColor(COLOR)
            selectView!!.textView.setTypeface(null,Typeface.BOLD)
        }else{ // focus없이 실시간 자막 update
            for (i in 0..view.frameLyricList.size-1){
                var lyric = view.frameLyricList.getItemAtPosition(i) as Lyrics
                if(lyric.index == index){
                    view.frameLyricList.getChildAt(i).textView.setTextColor(COLOR)
                    view.frameLyricList.getChildAt(i).textView.setTypeface(null,Typeface.BOLD)
                }else{
                    view.frameLyricList.getChildAt(i).textView.setTextColor(Color.GRAY)
                    view.frameLyricList.getChildAt(i).textView.setTypeface(null,Typeface.NORMAL)
                }
            }
        }
    }
    // focus 맞추기
    fun focusLyricIndex(view:View,index:Int){
        val height = view.frameLyricList.height
        val itemHeight = view.frameLyricList.getChildAt(0).height
        view.frameLyricList.setSelectionFromTop(index, height / 2 - itemHeight / 2)
    }
    fun updateLyricPosition(view: View, adapter:ListViewAdapter, lyrics: ArrayList<Lyrics>,touchMode:Int) {

        view.frameLyricList.viewTreeObserver.addOnDrawListener {
            val playPosition = viewModel.getPlayPosition().value
            if(playPosition!=null&&firstLoad){
                adapter.notifyDataSetChanged()
                focusLyricIndex(view, playPosition/2)
                firstLoad=false
            }
        }

        observer = Observer<Int> { position ->
            val arrayIndex = position/2
            // 아래에 선언하면 포커스 업데이트가 안된다.
            adapter.notifyDataSetChanged()
            if(touchMode==1){
                // touchMode의 경우 focus 이동은 하지 않는다.
                // 현재 재생 중인 가사 index와 화면에 표시되어 있는 가사 index가 일치하는 경우 text Color 변경
                textColorSetting(view,null,arrayIndex)
            }else{
                if (position > -1 && arrayIndex < lyrics.size && view.frameLyricList.size != 0) {
                    // focus 맞추기
                    focusLyricIndex(view,arrayIndex)
                    val HalfSize = (view.frameLyricList.size+1)/2
                    if(arrayIndex<HalfSize){ // 화면의 중간 가사보다 arrayIndex이 작다면, 해당 position의 text color만 변경
                        textColorSetting(view,view.frameLyricList.getChildAt(arrayIndex),-1)
                    }else if(arrayIndex>=lyrics.size-HalfSize){ // 화면의 중간 가사보다 arrayIndex가 크다면 이후 인덱스 text color만 변경
                        textColorSetting(view,view.frameLyricList.getChildAt(view.frameLyricList.size-(lyrics.size-arrayIndex)),-1)
                    }
                    else{
                        textColorSetting(view,view.frameLyricList.getChildAt(HalfSize),-1)
                    }
                }
            }
        }
        viewModel.getPlayPosition().observe(this, observer)
    }
    inner class ListViewAdapter(inflater: LayoutInflater, private val items:ArrayList<Lyrics>):BaseAdapter(){
        private val mLayoutInflater: LayoutInflater
        init {
            mLayoutInflater = inflater
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
            val playPosition = viewModel.getPlayPosition().value
            if(playPosition!=null && playPosition/2==position){
                convertView.textView.setTextColor(COLOR)
                convertView.textView.setTypeface(null,Typeface.BOLD)
            }else{
                convertView.textView.setTextColor(Color.GRAY)
                convertView.textView.setTypeface(null,Typeface.NORMAL)
            }
            return convertView
        }
    }
}
