# AndroidRecordPCMSample
Android 录音保存为 pcm demo 

- 为什么按byte[] 读取保存不能播放呢???

```java
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
