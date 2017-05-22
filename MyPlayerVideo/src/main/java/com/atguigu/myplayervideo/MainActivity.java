package com.atguigu.myplayervideo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void playerVideo(View v) {

        //调用系统播放器播放视频
        Intent intent = new Intent();
        intent.setDataAndType(Uri.parse("http://vfx.mtime.cn/Video/2017/05/17/mp4/170517091619822234.mp4"), "video/*");
        startActivity(intent);

    }
}
