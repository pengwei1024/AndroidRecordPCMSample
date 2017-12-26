# AndroidRecordPCMSample
Android 录音保存为 pcm demo 

- 为什么按byte[] 读取保存不能播放呢???

```java
// 16K采集率
int frequency = 16000;
// 格式
int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
// 16Bit
int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
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
```

- 用short[] 读取的就可以

```java
OutputStream os = new FileOutputStream(file);
BufferedOutputStream bos = new BufferedOutputStream(os);
DataOutputStream dos = new DataOutputStream(bos);
int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, bufferSize);
short[] buffer = new short[bufferSize];
byte[] bs = new byte[bufferSize];
audioRecord.startRecording();
Log.i(TAG, "开始录音");
isRecording.set(true);
while (isRecording.get()) {
    int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
        for (int i = 0; i < bufferReadResult; i++) {
             dos.writeShort(buffer[i]);
        }
    }
    audioRecord.stop();
    dos.close();
```