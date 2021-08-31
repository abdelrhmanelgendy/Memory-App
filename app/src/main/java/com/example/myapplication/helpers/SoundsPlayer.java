package com.example.myapplication.helpers;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundsPlayer {
    MediaPlayer soundsPlayer;

    public SoundsPlayer(Context context, int resId) {
        soundsPlayer = MediaPlayer.create(context, resId);
        soundsPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });
    }
    public void play()
    {
        if (soundsPlayer!=null)
        {
            if (!soundsPlayer.isPlaying())
            {

                soundsPlayer.start();

            }
        }
    }
}
