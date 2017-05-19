package com.atguigu.mediaplayer.player;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.atguigu.mediaplayer.R;

public class SystemVideoPlayerActivity extends AppCompatActivity {
    private VideoView vv_player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video_player);
        vv_player = (VideoView) findViewById(R.id.vv_player);

        //设置播放的三种状态的监听

        vv_player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                vv_player.start();

            }
        });


        vv_player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });

        vv_player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(SystemVideoPlayerActivity.this, "视频播放出错", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        Intent intent = getIntent();
        Uri uri = intent.getData();
        vv_player.setVideoURI(uri);
        //设置系统媒体控制器
        vv_player.setMediaController(new MediaController(this));
    }
}
