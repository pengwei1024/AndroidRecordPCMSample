package com.apkfuns.androidrecordpcmsample;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.os.Environment.getExternalStorageDirectory;

/**
 * Created by pengwei on 2017/12/21.
 * 参考: http://blog.csdn.net/qq_26787115/article/details/53078951
 */

public class MainActivity3 extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity2";
    private AtomicBoolean isRecording = new AtomicBoolean(false);
//    private File file = new File(getExternalStorageDirectory().getAbsolutePath() + "/baiduHi/reverseme.pcm");
    private File file = new File(getExternalStorageDirectory().getAbsolutePath() + "/BaiduHi/audio/temp1.hd.pcm");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnStartRecord).setOnClickListener(this);
        findViewById(R.id.btnStopRecord).setOnClickListener(this);
        findViewById(R.id.btnPlayRecord).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnStartRecord:
                isRecording.set(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startRecord();
                    }
                }).start();
                break;
            case R.id.btnStopRecord:
                isRecording.set(false);
                break;
            case R.id.btnPlayRecord:
                PlayRecord();
                break;
            default:
                break;
        }
    }

    //开始录音
    public void startRecord() {
        Log.i(TAG, "开始录音");
        //16K采集率
        int frequency = 44100;
        //格式
        int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
        //16Bit
        int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        //生成PCM文件
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/reverseme.pcm");
        Log.i(TAG, "生成文件");
        //如果存在，就先删除再创建
        if (file.exists())
            file.delete();
        Log.i(TAG, "删除文件");
        try {
            file.createNewFile();
            Log.i(TAG, "创建文件");
        } catch (IOException e) {
            Log.i(TAG, "未能创建");
            throw new IllegalStateException("未能创建" + file.toString());
        }
        try {
            //输出流
            FileOutputStream fos = new FileOutputStream(file);
            int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, bufferSize);
            bufferSize = bufferSize *2;
            byte[] buffer = new byte[bufferSize];
            audioRecord.startRecording();
            Log.i(TAG, "开始录音");
            isRecording.set(true);
            while (isRecording.get()) {
                int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                fos.write(buffer, 0, bufferReadResult);
            }
            audioRecord.stop();
            fos.close();
        } catch (Throwable t) {
            Log.e(TAG, "录音失败");
        }
    }

    //播放文件
    public void PlayRecord() {
        if(file == null){
            return;
        }
        //读取文件
        int musicLength = (int) (file.length() / 2);
        short[] music = new short[musicLength];
        try {
            InputStream is = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            DataInputStream dis = new DataInputStream(bis);
            int i = 0;
            while (dis.available() > 0) {
                music[i] = dis.readShort();
                i++;
            }
            dis.close();
            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    16000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    musicLength * 2,
                    AudioTrack.MODE_STREAM);
            audioTrack.play();
            audioTrack.write(music, 0, musicLength);
            audioTrack.stop();
        } catch (Throwable t) {
            Log.e(TAG, "播放失败");
        }
    }
}
