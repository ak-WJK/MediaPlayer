package com.atguigu.mediaplayer.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.atguigu.mediaplayer.R;
import com.atguigu.mediaplayer.domain.LocalMediaBean;
import com.atguigu.mediaplayer.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SystemVideoPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PROGRESS = 1;
    private VideoView vv_player;
    private Utils utils;
    private int systemTime;
    private BatteryReceiver receiver;
    private int level;
    //播放的视频的位置
    private int position;
    private ArrayList<LocalMediaBean> mediaBeens;
    private Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video_player);
        vv_player = (VideoView) findViewById(R.id.vv_player);
        utils = new Utils();

        findViews();

        //获取数据列表
        getListDatas();
        setData();

        //设置播放的三种状态的监听
        playerStartListener();
        //设置电池电量变化的监听
        initData();


        sbVideoPragressControl.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());


    }

    private void setData() {
        if (mediaBeens != null && mediaBeens.size() > 0) {
            //得到数据并设置视频名称及播放地址
            LocalMediaBean localMediaBean = mediaBeens.get(position);
            tvVideoName.setText(localMediaBean.getName());
            vv_player.setVideoPath(localMediaBean.getAddress());
        }


    }

    private void getListDatas() {
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        uri = intent.getData();

        mediaBeens = (ArrayList<LocalMediaBean>) intent.getSerializableExtra("mediaBeens");


    }

    private void initData() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        receiver = new BatteryReceiver();
        registerReceiver(receiver, filter);
    }

    //电量广播的监听
    class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//            leven = intent.getIntExtra("leven", 0);
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            setBattery();
        }
    }

    private void setBattery() {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level > 0 && level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level > 10 && level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level > 20 && level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level > 40 && level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level > 60 && level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level > 80 && level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }

    }


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case PROGRESS:

                    //得到播放时的每一小段时间
                    int currentPosition = vv_player.getCurrentPosition();

                    //设置时间到seekBar更新进度
                    sbVideoPragressControl.setProgress(currentPosition);

                    //播放时间的更新
                    tvVideoTime.setText(utils.stringForTime(currentPosition));
                    //得到系统时间
                    tvSystemTime.setText(getSystemTime());


                    removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS, 0);


                    break;
            }

        }
    };


    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {


        //进度改变的时候调用此方法
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //是否为为用户拖动
            if (fromUser) {
                vv_player.seekTo(progress);
            }

        }

        //手指开始滑动的时候回调
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        //手指停止滑动的时候回调
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    private void playerStartListener() {

        vv_player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                vv_player.start();
                //得到视频的总时长
                int duration = vv_player.getDuration();
                //设置视频总时长
                tvVideoTotaltime.setText(utils.stringForTime(duration));
                //设置seekBar的最大长度为对应的视频的总长度
                sbVideoPragressControl.setMax(duration);

                handler.sendEmptyMessage(PROGRESS);

            }
        });


        //当视频播放完成的时候回调
        vv_player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //自动播放下一条
                setNextPlayer();

            }
        });


        vv_player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(SystemVideoPlayerActivity.this, "视频播放出错", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

//        Intent intent = getIntent();
//        Uri uri = intent.getData();
//        vv_player.setVideoURI(uri);
//        //设置系统媒体控制器
//        vv_player.setMediaController(new MediaController(this));
    }

    //设置自动播放下一条
    private void setNextPlayer() {
        Log.e("TAG", "setNextPlayer");
        if (mediaBeens != null && mediaBeens.size() > 0) {
            position++;
            if (position < mediaBeens.size()) {
                //得到视频名称并设置
                LocalMediaBean bean = mediaBeens.get(position);
                tvVideoName.setText(bean.getName());
//
//                //得到播放地址
                vv_player.setVideoPath(bean.getAddress());

                if (position == mediaBeens.size() - 1) {
                    Toast.makeText(SystemVideoPlayerActivity.this, "最后一个视频", Toast.LENGTH_SHORT).show();

                    //设置下一个不可点击
                    
                }

            } else {
                position = mediaBeens.size() - 1;
                finish();
            }


        } else if (uri != null) {
            finish();
        }


    }


    private RelativeLayout llVideoInfo;
    private TextView tvVideoName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private LinearLayout llTopControls;
    private ImageButton ibVolune;
    private SeekBar sbVolumeControl;
    private ImageButton ivShera;
    private LinearLayout llBottemControls;
    private TextView tvVideoTime;
    private SeekBar sbVideoPragressControl;
    private TextView tvVideoTotaltime;
    private LinearLayout llControlButton;
    private ImageButton ibBack;
    private ImageButton ibPre;
    private ImageButton ibSwitchcontrol;
    private ImageButton ibNext;
    private ImageButton ibFullscreen;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-05-20 12:58:10 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        llVideoInfo = (RelativeLayout) findViewById(R.id.ll_video_info);
        tvVideoName = (TextView) findViewById(R.id.tv_video_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystemTime = (TextView) findViewById(R.id.tv_system_time);
        llTopControls = (LinearLayout) findViewById(R.id.ll_top_controls);
        ibVolune = (ImageButton) findViewById(R.id.ib_volune);
        sbVolumeControl = (SeekBar) findViewById(R.id.sb_volume_control);
        ivShera = (ImageButton) findViewById(R.id.iv_shera);
        llBottemControls = (LinearLayout) findViewById(R.id.ll_bottem_controls);
        tvVideoTime = (TextView) findViewById(R.id.tv_video_time);
        sbVideoPragressControl = (SeekBar) findViewById(R.id.sb_video_pragress_control);
        tvVideoTotaltime = (TextView) findViewById(R.id.tv_video_totaltime);
        llControlButton = (LinearLayout) findViewById(R.id.ll_control_button);
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        ibPre = (ImageButton) findViewById(R.id.ib_pre);
        ibSwitchcontrol = (ImageButton) findViewById(R.id.ib_switchcontrol);
        ibNext = (ImageButton) findViewById(R.id.ib_next);
        ibFullscreen = (ImageButton) findViewById(R.id.ib_fullscreen);

        ibVolune.setOnClickListener(this);
        ivShera.setOnClickListener(this);
        ibBack.setOnClickListener(this);
        ibPre.setOnClickListener(this);
        ibSwitchcontrol.setOnClickListener(this);
        ibNext.setOnClickListener(this);
        ibFullscreen.setOnClickListener(this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-05-20 12:58:10 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == ibVolune) {
            // Handle clicks for ibVolune
        } else if (v == ivShera) {
            // Handle clicks for ivShera
        } else if (v == ibBack) {
            finish();
        } else if (v == ibPre) {


        } else if (v == ibSwitchcontrol) {
            if (vv_player.isPlaying()) {

                vv_player.pause();

                ibSwitchcontrol.setBackgroundResource(R.drawable.media_switchcontrol2_select);

            } else {
                vv_player.start();
                ibSwitchcontrol.setBackgroundResource(R.drawable.media_switchcontrol1_select);
            }

        } else if (v == ibNext) {
            // Handle clicks for ibNext
        } else if (v == ibFullscreen) {
            // Handle clicks for ibFullscreen
        }
    }

    //得到系统时间
    public String getSystemTime() {
        SimpleDateFormat fromat = new SimpleDateFormat("HH:mm:ss");
        return fromat.format(new Date());
    }
}