package com.example.flo_application

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.FieldPosition

class MainActivity : AppCompatActivity() {
    private val urlInfo = "https://grepp-programmers-challenges.s3.ap-northeast-2.amazonaws.com/2020-flo/song.json"
    private val mp: MediaPlayer = MediaPlayer()
    private lateinit var mContext:Activity
    private lateinit var runTread:Runnable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext=this
        RestAPITask().execute(urlInfo)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
    inner class RestAPITask : AsyncTask<String, JSONObject, JSONObject>(){
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
                mContext.singerTxt.text = result.getString("singer") as String
                mContext.albumTxt.text = result.getString("album") as String
                mContext.titleTxt.text = result.getString("title") as String
                var lyrics = result.getString("lyrics").split("[","]").toMutableList()
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

                mContext.totalLyricBtn.setOnClickListener {
                    val intent = Intent(mContext,TotalLyricActivity::class.java)
                    intent.putExtra("lyrics",lyrics.toTypedArray())
                    mContext.startActivityForResult(intent,1)
                }
                settingSeekBar(lyrics)
                Glide.with(mContext).load(alumImgURL)
                .transform(CenterCrop(), RoundedCorners(30)).into(mContext.albumImg);
            }
        }

        fun settingSeekBar(lyrics: List<String>){
            var handler:Handler = Handler()
            if (mp!=null){
                mContext.seekBar.max = mp.duration
                runTread=runnable(handler,lyrics)
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

        fun runnable(handler:Handler,lyrics:List<String>): Runnable {
            return object : Runnable {
                override fun run() {
                    if (mp.isPlaying){ // 재생 중일 때만 seekbar update
                        mContext.seekBar.setProgress(mp.getCurrentPosition())
                        settingLyrics(mp.getCurrentPosition(),lyrics)
                    }
                    handler.postDelayed(this,200)
                }
            }
        }
        fun settingLyrics(duration: Int, lyrics: List<String>){
            var i = 0
            var index = -1
            while(i<lyrics.size-1 && duration>Integer.parseInt(lyrics[i])){
                index=i
                i+=2
            }
            if(index>-1 && index<lyrics.size-1){
                mContext.lyricsTxt.text = lyrics[index+1]
            }
        }
    }

}
