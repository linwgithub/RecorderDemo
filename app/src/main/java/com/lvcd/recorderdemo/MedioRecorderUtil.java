package com.lvcd.recorderdemo;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MedioRecorderUtil {

    private static final String TAG = "MedioRecorderUtil";

    private static MedioRecorderUtil instance;
    private MediaRecorder mRecorder = null;
    private static String mFileName = null;

    private Timer timer;

    private void startTimer() {
        stopTimer();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                double ratio = getAmplitude();
                Log.e(TAG, "getAmplitude: 当前音量：" + ratio);
            }
        }, 0, 300);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    public static MedioRecorderUtil getInstance(Context context) {
        if (instance == null) {
            synchronized(MedioRecorderUtil.class) {
                if (instance == null) {
                    instance = new MedioRecorderUtil(context);
                }
            }
        }

        return instance;
    }


    private MedioRecorderUtil(Context context) {
        mFileName = context.getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
    }

    public String getFilePath() {
        return mFileName;
    }

    public void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                Log.e(TAG, "onInfo: what:" + what + ", extra:" + extra);
            }
        });

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    public void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    private double getAmplitude() {
        if (mRecorder == null) {
            return 0;
        }
        double ratio = mRecorder.getMaxAmplitude();

        if (ratio > 1) {
            ratio = 20 * Math.log10(ratio);
        }

        return ratio;
    }
}
