package com.example.flo_application

import android.app.Activity
import android.graphics.drawable.Drawable
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

class MainActivity : AppCompatActivity() {
    private val urlInfo = "https://grepp-programmers-challenges.s3.ap-northeast-2.amazonaws.com/2020-flo/song.json"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RestAPITask(this).execute(urlInfo)
    }

    class RestAPITask : AsyncTask<String, JSONObject, JSONObject>{
        private var mContext:Activity
        private lateinit var mp: MediaPlayer
        constructor(context:Activity) {
            mContext =context
        }
        override fun onPreExecute() {
            super.onPreExecute()
            mp = MediaPlayer()


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
                    Log.d("File",Uri.parse(jsonObject.getString("file") as String).toString())

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

                settingSeekBar()
                Glide.with(mContext).load(alumImgURL)
                .transform(CenterCrop(), RoundedCorners(30)).into(mContext.albumImg);
            }
        }
        fun settingSeekBar(){
            if (mp!=null){
                mContext.seekBar.max = mp.duration

                // 재생 시 seekBar update
                var handler:Handler = Handler()
                mContext.runOnUiThread(object : Runnable {
                    override fun run() {
                        Thread.sleep(100)
                        if (mp.isPlaying){ // 재생 중일 때만 seekbar update
                            mContext.seekBar.setProgress(mp.getCurrentPosition())
                        }
                        handler.postDelayed(this,1000)
                    }
                })
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
    }

}
