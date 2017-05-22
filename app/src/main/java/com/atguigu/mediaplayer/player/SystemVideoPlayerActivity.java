package com.atguigu.mediaplayer.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.mediaplayer.R;
import com.atguigu.mediaplayer.domain.LocalMediaBean;
import com.atguigu.mediaplayer.utils.Utils;
import com.atguigu.mediaplayer.view.VideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SystemVideoPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PROGRESS = 1;
    private static final int FULL_SCREEN = 3;
    private static final int DEFUALT_SCREEN = 4;
    private VideoView vv_player;
    private Utils utils;
    private int systemTime;
    private BatteryReceiver receiver;
    private int level;
    //播放的视频的位置
    private int position;
    private ArrayList<LocalMediaBean> mediaBeens;
    private Uri uri;
    private GestureDetector detector;
    private static final int HIDE_MEDIA_CONTROLLER = 2;

    private boolean isShowControl;
    private boolean isShowControlPlayer = true;
    private int screenHeight;
    private int screenWidth;
    private int videoWidth;
    private int videoHeight;
    private AudioManager am;
    private int currentVoice;
    private int maxVoice;
    boolean isMute = false;
    private float downY;
    private int volume;
    private int min;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video_player);
        vv_player = (VideoView) findViewById(R.id.vv_player);
        utils = new Utils();

        findViews();

        //获取数据列表
        getListDatas();

        //定义手势识别器实现控制面板的隐藏和显示及视频的控制
        getGesture();

        //得到音量
        getVolume();

        //关联最大音量
        sbVolumeControl.setMax(maxVoice);
        //设置当前音量进度
        sbVolumeControl.setProgress(currentVoice);


        setData();

        //设置播放的三种状态的监听
        playerStartListener();
        //设置电池电量变化的监听
        initData();
        //seekBar拖动改变的监听
        sbVideoPragressControl.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());


    }

    private void getGesture() {
        //定义手势识别器实现控制面板的隐藏和显示及视频的控制
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {


            //长按的
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                //播放和暂停
                startAndPause();


            }


            //双击的
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                getScreenInfo();
                if (isFullScreen) {
                    //默认
                    setVideoType(DEFUALT_SCREEN);
                } else {
                    //全屏
                    setVideoType(FULL_SCREEN);
                }
                return super.onDoubleTap(e);


            }

            //单击的
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {

                if (isShowControlPlayer) {
                    hideControlPlayer();
                    handler.removeMessages(HIDE_MEDIA_CONTROLLER);
                } else {
                    showControlPlayer();
                }
                return true;
            }
        });

        getScreenInfo();


        sbVolumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {

                    updateVolume(progress);
                    handler.removeMessages(HIDE_MEDIA_CONTROLLER);
                    handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void getVolume() {
        //得到当前音量及系统音量

        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    private void updateVolume(int progress) {
        currentVoice = progress;
        am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVoice, 0);
        sbVolumeControl.setProgress(currentVoice);

        if (currentVoice <= 0) {
            isMute = true;
        } else {
            isMute = false;
        }


    }

    private void getScreenInfo() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;
    }


    /**
     * 显示控制面板
     */
    protected void showControlPlayer() {
        rl_layout.setVisibility(View.VISIBLE);
        isShowControlPlayer = true;
    }

    /**
     * 隐藏控制面板
     */
    protected void hideControlPlayer() {
        rl_layout.setVisibility(View.GONE);
        isShowControlPlayer = false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);


        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                //得到当前声音
                volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                //得到最小值
                min = Math.min(screenHeight, screenWidth);

                handler.removeMessages(HIDE_MEDIA_CONTROLLER);

                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = event.getY();
                float distanceY = downY - moveY;
                //  得到滑动了多少声音 =   滑动距离/屏幕距离 * 最大声音值;
                float delta = (distanceY / min) * maxVoice;

                if (delta != 0) {
                    //当前声音
                    int mVolume = (int) Math.min(Math.max(delta + volume, 0), maxVoice);
                    //更新声音
                    updateVolume(mVolume);
                }


                break;
            case MotionEvent.ACTION_UP:
                handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);

                break;
        }

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            currentVoice--;
            updateVolume(currentVoice);


            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);

            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            currentVoice++;
            updateVolume(currentVoice);


            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isNetUri;

    private void setData() {

        if (mediaBeens != null && mediaBeens.size() > 0) {
            //得到数据并设置视频名称及播放地址
            LocalMediaBean localMediaBean = mediaBeens.get(position);
            tvVideoName.setText(localMediaBean.getName());
            vv_player.setVideoPath(localMediaBean.getAddress());
            //本地地址
            isNetUri = Utils.isNetUri(localMediaBean.getAddress());


        } else if (uri != null) {
            //播放外界传入的地址的视频(点击视频调用自己播放器)
            vv_player.setVideoURI(uri);
            tvVideoName.setText(uri.toString());
            //网络地址
            isNetUri = Utils.isNetUri(uri.toString());

        }
        setButtonStart();

    }

    private void getListDatas() {
        Intent intent = getIntent();

        //获取从外界获取到的播放地址
//        uri = intent.getData();
        uri = getIntent().getData();

//        Log.e("TAG", "uri" + uri.toString());

        position = intent.getIntExtra("position", 0);

        mediaBeens = (ArrayList<LocalMediaBean>) intent.getSerializableExtra("mediaBeens");


    }

    private void initData() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        receiver = new BatteryReceiver();
        registerReceiver(receiver, filter);
    }

    private boolean isFullScreen = false;

    public void setVideoType(int videoType) {
        switch (videoType) {
            case FULL_SCREEN:
                isFullScreen = true;

                ibFullscreen.setBackgroundResource(R.drawable.media_fullscreen2_control_select);

                //设置视频画面的宽和高
                vv_player.setVideoSize(screenWidth, screenHeight);

                break;
            case DEFUALT_SCREEN:
                isFullScreen = false;

                ibFullscreen.setBackgroundResource(R.drawable.media_fullscreen_control_select);

                //视频原生的宽和高
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;

                //计算好的要显示的视频的宽和高
                int width = screenWidth;
                int height = screenHeight;

                // for compatibility, we adjust size based on aspect ratio
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }

                vv_player.setVideoSize(width, height);
                ibFullscreen.setBackgroundResource(R.drawable.media_fullscreen_control_select);
                isFullScreen = false;

                break;
        }


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

                    //如果是网络视频就缓存
                    if (isNetUri) {

                        //得到缓冲的百分比
                        int bufferPercentage = vv_player.getBufferPercentage();//0-100;

                        //要缓存的长度
                        int totalBuffer = bufferPercentage * sbVideoPragressControl.getMax();

                        int secondaryProgress = totalBuffer / 100;
                        sbVideoPragressControl.setSecondaryProgress(secondaryProgress);

                    } else {
                        sbVideoPragressControl.setSecondaryProgress(0);
                    }


                    removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS, 0);


                    break;
                case HIDE_MEDIA_CONTROLLER:
                    hideControlPlayer();
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

                handler.removeMessages(HIDE_MEDIA_CONTROLLER);
                handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
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
                //得到视频的宽和高
                videoWidth = mp.getVideoWidth();
                videoHeight = mp.getVideoHeight();


                vv_player.start();
                //得到视频的总时长
                int duration = vv_player.getDuration();
                //设置视频总时长
                tvVideoTotaltime.setText(utils.stringForTime(duration));
                //设置seekBar的最大长度为对应的视频的总长度
                sbVideoPragressControl.setMax(duration);

                handler.sendEmptyMessage(PROGRESS);

                handler.removeMessages(HIDE_MEDIA_CONTROLLER);
                handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);

                //设置默认屏幕
                setVideoType(DEFUALT_SCREEN);

            }
        });


        //当视频播放完成的时候回调
        vv_player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //自动播放下一条
                playNextPlayer();

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
    private void playNextPlayer() {
        Log.e("TAG", "setNextPlayer");
        if (mediaBeens != null && mediaBeens.size() > 0) {
            position++;
            if (position < mediaBeens.size()) {
                //得到视频名称并设置
                LocalMediaBean bean = mediaBeens.get(position);

                //得到地址
                isNetUri = Utils.isNetUri(bean.getAddress());

                tvVideoName.setText(bean.getName());

                //设置播放地址
                vv_player.setVideoPath(bean.getAddress());

                //设置按钮状态
                setButtonStart();


            } else {
                finish();
            }


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
    private RelativeLayout rl_layout;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-05-20 12:58:10 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        rl_layout = (RelativeLayout) findViewById(R.id.rl_layout);
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
            isMute = !isMute;

            updateVoice(isMute);

        } else if (v == ivShera) {
            // Handle clicks for ivShera
        } else if (v == ibBack) {
            finish();
        } else if (v == ibPre) {
            playPreVideo();

        } else if (v == ibSwitchcontrol) {
            startAndPause();

        } else if (v == ibNext) {
            playNextPlayer();

        } else {
            if (v == ibFullscreen) {
                getScreenInfo();
                if (isFullScreen) {
                    //默认
                    setVideoType(DEFUALT_SCREEN);
                } else {
                    //全屏
                    setVideoType(FULL_SCREEN);
                }

            }
        }
        handler.removeMessages(HIDE_MEDIA_CONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
    }

    private void updateVoice(boolean isMute) {
        if (isMute) {
            //静音的
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            sbVolumeControl.setProgress(0);
        } else {
//非静音的
            am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVoice, 0);
            sbVolumeControl.setProgress(currentVoice);

        }
    }

    private void startAndPause() {
        if (vv_player.isPlaying()) {

            vv_player.pause();

            ibSwitchcontrol.setBackgroundResource(R.drawable.media_switchcontrol2_select);

        } else {
            vv_player.start();
            ibSwitchcontrol.setBackgroundResource(R.drawable.media_switchcontrol1_select);
        }
    }

    private void setPreOrNextVideo(boolean isEnable) {
        ibPre.setEnabled(isEnable);
        ibNext.setEnabled(isEnable);
        if (isEnable) {
            ibPre.setBackgroundResource(R.drawable.media_pre_control_select);
            ibNext.setBackgroundResource(R.drawable.media_next_control_select);
        } else {
            ibPre.setBackgroundResource(R.drawable.btn_pre_gray);
            ibNext.setBackgroundResource(R.drawable.btn_next_gray);
        }


    }

    private void playPreVideo() {
        if (mediaBeens != null && mediaBeens.size() > 0) {


            position--;
            if (position >= 0) {
                LocalMediaBean localMediaBean = mediaBeens.get(position);
                //得到地址
                isNetUri = Utils.isNetUri(localMediaBean.getAddress());

                tvVideoName.setText(localMediaBean.getName());
                vv_player.setVideoPath(localMediaBean.getAddress());

                //设置按钮的状态
                setButtonStart();

            } else {
                position = 0;
            }
        }

    }

    private void setButtonStart() {
        if (mediaBeens != null && mediaBeens.size() > 0) {
            setPreOrNextVideo(true);
            if (position == 0) {
                ibPre.setEnabled(false);
                ibPre.setBackgroundResource(R.drawable.btn_pre_gray);
            }
            if (position == mediaBeens.size() - 1) {
                ibNext.setEnabled(false);
                ibNext.setBackgroundResource(R.drawable.btn_next_gray);
            }


        }


    }

    //得到系统时间
    public String getSystemTime() {
        SimpleDateFormat fromat = new SimpleDateFormat("HH:mm:ss");
        return fromat.format(new Date());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        unregisterReceiver(receiver);
    }
}