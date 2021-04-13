package com.example.flo_application

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.view.size
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.custom_list_item.view.*
import kotlinx.android.synthetic.main.fragment_lyric.view.*
import kotlin.collections.ArrayList

class TotalLyricFragment : Fragment() {
    private lateinit var activity:MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity = context as MainActivity
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_lyric, null)
        val lyricsInfo = arguments?.getSerializable("lyrics") as ArrayList<String>
        val touchMode = arguments?.getBoolean("touchMode",false)
        val lyrics = ArrayList<String>()
        for (i in 1..lyricsInfo.size-1 step 2){
            lyrics.add(lyricsInfo.get(i))
        }
        val adapter = ListViewAdapter(inflater,lyrics)
        fragmentView.frameLyricList.adapter = adapter
        fragmentView.frameLyricList.setOnItemClickListener { parent, view, position, id ->
            if(touchMode==true){
                // 나머지 선택안된 list는 색 초기화
                adapter.changeSelectCount(position)
                for (i in 0..fragmentView.frameLyricList.size-2){
                    fragmentView.frameLyricList.getChildAt(i).textView.setTextColor(Color.GRAY)
                }
                activity.setSeekTo(Integer.parseInt(lyricsInfo[position*2]))
                view.textView.setTextColor(Color.BLACK)
            }else{
                // 뒤로가기
                activity.onBackPressed()
            }
        }

        return fragmentView
    }

}

private class ListViewAdapter(inflater: LayoutInflater, private val items:ArrayList<String>):BaseAdapter(){
    private val mLayoutInflater: LayoutInflater
    private var mSelectCount:Int
    init {
        mLayoutInflater = inflater
        mSelectCount=-1
    }
    fun changeSelectCount(count:Int){
        mSelectCount = count
    }
    override fun getCount(): Int {
        return items.size
    }
    override fun getItem(position: Int): Any {
        return items.get(position)
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val convertView = mLayoutInflater.inflate(R.layout.custom_list_item, parent, false)
        convertView.textView.text = items[position]
        if(mSelectCount==position){
            convertView.textView.setTextColor(Color.BLACK)
        }else{
            convertView.textView.setTextColor(Color.GRAY)
        }
        return convertView
    }
}