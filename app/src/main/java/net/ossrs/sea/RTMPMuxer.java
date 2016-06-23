package net.ossrs.sea;

import android.util.Log;
/**
 * Created by faraklit on 08.02.2016.
 */
public class RTMPMuxer {

    private static final String TAG = "RTMPMuxer_java";

    static {
        System.loadLibrary("rtmp");
        System.loadLibrary("rtmp-jni");
    }

    public native int open(String url);

    /**
     * write h264 nal units
     * @param data
     * @param offset
     * @param length
     * @param timestamp
     * @return 0 if it writes network successfully
     * -1 if it could not write
     */
    public native int writeVideo(byte[] data, int offset, int length, int timestamp);

    /**
     * Write raw aac data
     * @param data
     * @param offset
     * @param length
     * @param timestamp
     * @return 0 if it writes network successfully
     * -1 if it could not write
     */
    public native int writeAudio(byte[] data, int offset, int length, int timestamp);

    public native int close();


    public native void write_flv_header(boolean is_have_audio, boolean is_have_video);

    public native void file_open(String filename);

    public native void file_close();

    /**
     *
     * @return 1 if it is connected
     * 0 if it is not connected
     */
    public native int isConnected();

    public synchronized int writeSampleData(byte[] data, int offset, int length, int timestamp,boolean audio){
        Log.w(TAG,String.format("enter writeSampelData:%s",audio?"audio":"video"));
        int ret =0;
        if(audio)
            ret = writeAudio(data,offset,length,timestamp);
        else
            ret = writeVideo(data,offset,length,timestamp);
        Log.w(TAG,String.format("exit writeSampelData:%s",audio?"audio":"video"));
        return ret;
    }
}
