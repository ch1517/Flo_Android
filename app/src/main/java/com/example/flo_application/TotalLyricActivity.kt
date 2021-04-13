package com.example.flo_application

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.view.size
import kotlinx.android.synthetic.main.activity_total_lyric.*
import kotlinx.android.synthetic.main.custom_list_item.view.*

class TotalLyricActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_total_lyric)

        val lyricsInfo = intent.getSerializableExtra("lyrics") as Array<String>
        val lyrics = ArrayList<String>()
        for (i in 1..lyricsInfo.size-1 step 2){
            lyrics.add(lyricsInfo.get(i))
        }
        val adapter = ListViewAdapter(this,lyrics)
        listView.adapter = adapter
        listView.setOnItemClickListener { parent, view, position, id ->
            if(touchPlayBtn.isChecked){
                // 나머지 선택안된 list는 색 초기화
                adapter.changeSelectCount(position)
                for (i in 0..listView.size-2){
                    listView.getChildAt(i).textView.setTextColor(Color.GRAY)
                }
                view.textView.setTextColor(Color.BLACK)
            }else{ //touchPlayBtn Off시 가사 전체화면 종료
                finish()
            }
        }

        closeBtn.setOnClickListener {
            this.finish()
        }
    }
}

private class ListViewAdapter(context: Context, private val items:ArrayList<String>):BaseAdapter(){
    private val mContext: Context
    private var mSelectCount:Int
    init {
        mContext = context
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
        val layoutInflater = LayoutInflater.from(mContext)
        val convertView = layoutInflater.inflate(R.layout.custom_list_item, parent, false)
        convertView.textView.text = items[position]
        if(mSelectCount==position){
            convertView.textView.setTextColor(Color.BLACK)
        }else{
            convertView.textView.setTextColor(Color.GRAY)
        }
        return convertView
    }
}