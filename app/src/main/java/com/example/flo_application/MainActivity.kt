package com.example.flo_application

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private val urlInfo = "https://grepp-programmers-challenges.s3.ap-northeast-2.amazonaws.com/2020-flo/song.json"
    private val mp: MediaPlayer = MediaPlayer()
    private lateinit var mContext:Activity
    private lateinit var runTread:Runnable
    private lateinit var viewModel :MainViewModel
    private var currentPosition:Int = -1
    private lateinit var lyrics:ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext=this
        val transaction = supportFragmentManager.beginTransaction()
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MainViewModel::class.java)

        RestAPITask(transaction).execute(urlInfo)
    }
    public fun showFullLyricBtn(){
        mContext.totalLyricBtn.visibility = View.VISIBLE
    }
    public fun setSeekTo(progress:Int){
        if(mp!=null){
            // 자막 왔다갔다 때문에 약간의 버퍼 주기
            // 없으면 이전 자막이 잠깐 불러와진다.
            mp.seekTo(progress+10) // 재생위치를 바꿔준다(움직인 곳에서의 음악재생)
            setCurrentPosition()
        }
    }
    inner class RestAPITask : AsyncTask<String, JSONObject, JSONObject>{
        var mTransaction:FragmentTransaction
        constructor(transaction: FragmentTransaction){
            mTransaction=transaction
        }
        override fun onPreExecute() {
            super.onPreExecute()
        }
        override fun doInBackground(vararg urlStr: String): JSONObject? {
            var jsonObject: JSONObject? = null
            try{
                val url: URL = URL(urlStr[0])
                val conn : HttpURLConnection = url.openConnection() as HttpURLConnection
                conn.requestMethod ="GET"
                if(conn.responseCode == HttpURLConnection.HTTP_OK){
                    val streamReader = InputStreamReader(conn.inputStream)
                    val buffered = BufferedReader(streamReader)
                    val content = StringBuilder()

                    while(true) {
                        val line = buffered.readLine() ?: break
                        content.append(line)
                    }
                    buffered.close()
                    conn.disconnect()
                    jsonObject = JSONObject(content.toString())

                    mp.setDataSource(jsonObject.getString("file"))
                    mp.prepare()
                }
            }catch (e: Exception){
                Log.e("REST_API", "GET method failed: " + e.toString());
                e.printStackTrace();
            }
            return jsonObject
        }
        //        UI Update
        override fun onPostExecute(result: JSONObject?) {
            super.onPostExecute(result)
            Log.d("응답 결과 ",result.toString())
            if (result!=null){
                val alumImgURL = result.getString("image") as String
                val singerTxt = result.getString("singer") as String
                val albumTxt = result.getString("album") as String
                val titleTxt = result.getString("title") as String

                lyrics = result.getString("lyrics").split("[","]").toMutableList() as ArrayList<String>
                lyrics.removeAt(0)

                for (i in 0..lyrics.size-1 step 1){
                    if(i%2==0){
                        //시간 초로 변환
                        var str = lyrics[i].split(":")
                        lyrics[i]=(Integer.parseInt(str[0])*60000+Integer.parseInt(str[1])*1000+Integer.parseInt(str[2])).toString()
                    }else{
                        // 공백문자 제거
                        lyrics[i] = lyrics[i].substring(0,lyrics[i].length-1)
                    }
                }
                mTransaction = supportFragmentManager.beginTransaction()
                mTransaction.replace(R.id.flagment,
                    PlayFragment().apply {
                        arguments = Bundle().apply{
                            putString("alumImgURL",alumImgURL)
                            putString("singerTxt",singerTxt)
                            putString("albumTxt",albumTxt)
                            putString("titleTxt",titleTxt)
                            putSerializable("lyrics",lyrics as ArrayList<String>)
                        }
                    }).commitAllowingStateLoss()
                mContext.totalLyricBtn.setOnClickListener() {
                    mTransaction = supportFragmentManager.beginTransaction()
                    mTransaction.replace(R.id.flagment,
                        FullScreenLyricFragment().apply {
                            arguments = Bundle().apply{
                                putSerializable("lyrics",lyrics as ArrayList<String>)
                            }
                        })
                        .addToBackStack(null)
                        .commit()
                    mContext.totalLyricBtn.visibility = View.GONE

                }
                settingSeekBar()
            }
        }

        fun settingSeekBar(){
            val handler:Handler = Handler()
            if (mp!=null){
                mContext.seekBar.max = mp.duration
                runTread=runnable(handler)
                mContext.runOnUiThread(runTread)
            }
            mp.setOnPreparedListener(object:MediaPlayer.OnPreparedListener{
                override fun onPrepared(p0: MediaPlayer?) {
                    mp.start();
                }
            });

            mContext.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (mp!=null && fromUser){
                        // 사용자가 시크바를 움직이면
                        mp.seekTo(progress) // 재생위치를 바꿔준다(움직인 곳에서의 음악재생)
                    }
                }
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })
            mContext.startBtn.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked){
                    mp.start()
                    mContext.startBtn.background = mContext.getDrawable(R.drawable.pause)
                }else{
                    mp.pause()
                    mContext.startBtn.background = mContext.getDrawable(R.drawable.play)
                }
            }
        }
        fun runnable(handler:Handler): Runnable {
            return object : Runnable {
                override fun run() {
                    if (mp.isPlaying){ // 재생 중일 때만 seekbar update
                        mContext.seekBar.setProgress(mp.getCurrentPosition())
                        setCurrentPosition()
                    }
                    handler.postDelayed(this,100)
                }
            }
        }

    }
    fun setCurrentPosition(){
        val p = countLyricPosition(lyrics,mp.getCurrentPosition())
        if (currentPosition!=p){
            currentPosition=p
            viewModel.setPlayPosition(currentPosition)
        }
    }
    fun countLyricPosition(lyrics: ArrayList<String>,duration:Int):Int{
        var i = 0
        var index = -1
        while(i<lyrics.size-1 && duration>Integer.parseInt(lyrics[i])){
            index=i
            i+=2
        }
        return index
    }
    override fun onDestroy() {
        super.onDestroy()
        mp.release()
    }
}
