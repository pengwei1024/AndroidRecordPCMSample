package com.apkfuns.androidrecordpcmsample

import android.media.AudioFormat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Environment.getExternalStorageDirectory
import android.util.Log
import android.media.MediaRecorder
import android.media.AudioRecord
import java.io.*
import java.util.concurrent.atomic.AtomicBoolean
import android.media.AudioTrack
import android.media.AudioManager


class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = "MainActivity"
    }

    private val isRecording: AtomicBoolean = AtomicBoolean(false)
    private var file: File = File(getExternalStorageDirectory().getAbsolutePath() + "/baiduHi/reverseme.pcm")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnStartRecord.setOnClickListener {
            isRecording.set(true)
            Thread(Runnable { startRecord() }).start()
        }
        btnStopRecord.setOnClickListener {
            isRecording.set(false)
            Log.i(TAG, "停止录音")
        }
        btnPlayRecord.setOnClickListener { PlayRecord() }
    }

    /**
     * 开始录音
     */
    private fun startRecord() {
        Log.i(TAG, "开始录音")
        //16K采集率
        val frequency = 16000
        //格式
        val channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO
        //16Bit
        val audioEncoding = AudioFormat.ENCODING_PCM_16BIT
        //生成PCM文件
        Log.i(TAG, "生成文件")
        //如果存在，就先删除再创建
        if (file.exists())
            file.delete()
        Log.i(TAG, "删除文件")
        try {
            file.createNewFile()
            Log.i(TAG, "创建文件")
        } catch (e: IOException) {
            Log.i(TAG, "未能创建")
            throw IllegalStateException("未能创建" + file.toString())
        }
        try {
            //输出流
            val os = FileOutputStream(file)
            val bos = BufferedOutputStream(os)
            val dos = DataOutputStream(bos)
            val bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding)
            val audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, bufferSize)

            val buffer = ShortArray(bufferSize)
            audioRecord.startRecording()
            Log.i(TAG, "开始录音")
            while (isRecording.get()) {
                val bufferReadResult = audioRecord.read(buffer, 0, bufferSize)
                for (i in 0 until bufferReadResult) {
                    dos.writeByte(buffer[i].toInt())
                }
            }
            audioRecord.stop()
            dos.close()
        } catch (t: Throwable) {
            Log.e(TAG, "录音失败")
        }
    }

    /**
     * 播放录音
     */
    fun PlayRecord() {
        if (!file.exists()) {
            return
        }
        //读取文件
        val musicLength = (file.length() / 2).toInt()
        val music = ShortArray(musicLength)
        try {
            val `is` = FileInputStream(file)
            val bis = BufferedInputStream(`is`)
            val dis = DataInputStream(bis)
            var i = 0
            while (dis.available() > 0) {
                music[i] = dis.readShort()
                i++
            }
            dis.close()
            val audioTrack = AudioTrack(AudioManager.STREAM_MUSIC,
                    16000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    musicLength * 2,
                    AudioTrack.MODE_STREAM)
            audioTrack.play()
            audioTrack.write(music, 0, musicLength)
            audioTrack.stop()
        } catch (t: Throwable) {
            Log.e(TAG, "播放失败")
        }

    }
}
