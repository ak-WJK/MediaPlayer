package com.atguigu.mediaplayer.player;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.atguigu.mediaplayer.IMusicPlayService;
import com.atguigu.mediaplayer.R;
import com.atguigu.mediaplayer.service.MusicPlayerService;
import com.atguigu.mediaplayer.utils.Utils;

public class SystemAudioPlayerActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int PROGRESS = 1;
    private RelativeLayout llBottemControls;
    private TextView tvAudioTime;
    private SeekBar sbAudioPragressControl;
    private LinearLayout llControlButton;
    private ImageButton ibFor;
    private ImageButton ibPre;
    private ImageButton ibSwitchcontrol;
    private ImageButton ibNext;
    private ImageButton ibGeci;
    private IMusicPlayService service;
    private ImageView audio_frequencu;
    private TextView tv_music_name;
    private TextView tv_artist;
    private ServiceConnection conn;

    //传过来的位置
    private int position;
    private Utils utils;


    private boolean notification;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROGRESS:
                    try {
                        int currentPosition = service.getCurrentPosition();
                        sbAudioPragressControl.setProgress(currentPosition);
                        tvAudioTime.setText(utils.stringForTime(currentPosition) + "/" + utils.stringForTime(service.getDuration()));


                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);

                    break;
            }


        }
    };
    private MyBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utils = new Utils();
        findViews();
        musicFrequency();
        getData();
        startAndBindService();
        initData();


    }

    private void initData() {


        receiver = new MyBroadcastReceiver();

        IntentFilter filter = new IntentFilter(MusicPlayerService.GETDATAS);
//        filter.addAction(MusicPlayerService.GETDATAS);
        registerReceiver(receiver, filter);


    }

    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            setViewData();
        }
    }

    private void setViewData() {

        try {
            setButtonImage();
            tv_artist.setText(service.getArtistName());
            tv_music_name.setText(service.getMusicName());
            int duration = service.getDuration();
            sbAudioPragressControl.setMax(duration);


        } catch (RemoteException e) {
            e.printStackTrace();
        }
        //发消息更新进度
        handler.sendEmptyMessage(PROGRESS);

    }

    private void musicFrequency() {
        AnimationDrawable background = (AnimationDrawable) audio_frequencu.getBackground();
        background.start();
    }

    private void startAndBindService() {


        Intent intent = new Intent(this, MusicPlayerService.class);

        conn = new ServiceConnection() {


            //连接时调用
            @Override
            public void onServiceConnected(ComponentName name, IBinder iBinder) {

                service = IMusicPlayService.Stub.asInterface(iBinder);


                if (service != null) {
                    try {

                        if (notification) {
                            setViewData();
                        } else {

                            service.openAudio(position);
                            ibSwitchcontrol.setBackgroundResource(R.drawable.audio_switchcontrol2_select);
                        }


                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }


            }

            //断开时调用
            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };


        bindService(intent, conn, Context.BIND_AUTO_CREATE);

        //防止多次实例化
        startService(intent);
    }


    private void findViews() {

        setContentView(R.layout.activity_system_audio_player);
        tv_artist = (TextView) findViewById(R.id.tv_artist);
        tv_music_name = (TextView) findViewById(R.id.tv_music_name);
        audio_frequencu = (ImageView) findViewById(R.id.audio_frequencu);

        llBottemControls = (RelativeLayout) findViewById(R.id.ll_bottem_controls);
        tvAudioTime = (TextView) findViewById(R.id.tv_audio_time);
        sbAudioPragressControl = (SeekBar) findViewById(R.id.sb_audio_pragress_control);
        llControlButton = (LinearLayout) findViewById(R.id.ll_control_button);
        ibFor = (ImageButton) findViewById(R.id.ib_for);
        ibPre = (ImageButton) findViewById(R.id.ib_pre);
        ibSwitchcontrol = (ImageButton) findViewById(R.id.ib_switchcontrol);
        ibNext = (ImageButton) findViewById(R.id.ib_next);
        ibGeci = (ImageButton) findViewById(R.id.ib_geci);

        ibFor.setOnClickListener(this);
        ibPre.setOnClickListener(this);
        ibSwitchcontrol.setOnClickListener(this);
        ibNext.setOnClickListener(this);
        ibGeci.setOnClickListener(this);


        sbAudioPragressControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    try {
                        service.seekTo(progress);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

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


    @Override
    public void onClick(View v) {
        if (v == ibFor) {
            setPlayMode();
        } else if (v == ibPre) {
            try {
                service.preMusic();

            } catch (RemoteException e) {
                e.printStackTrace();
            }

        } else if (v == ibSwitchcontrol) {

            try {
                if (service.isPlayer()) {
                    service.pauseMusic();
                    ibSwitchcontrol.setBackgroundResource(R.drawable.audio_switchcontrol1_select);

                } else {
                    service.playerMusic();
                    ibSwitchcontrol.setBackgroundResource(R.drawable.audio_switchcontrol2_select);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (v == ibNext) {
            try {
                service.nextMusic();
            } catch (RemoteException e) {
                e.printStackTrace();
            }


        } else if (v == ibGeci) {


        }
    }

    private void setPlayMode() {
        try {
            int playmode = service.getPlaymode();
            if (playmode == MusicPlayerService.REPEAT_NORMAL) {
                playmode = MusicPlayerService.REPEAT_SINGLE;
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                playmode = MusicPlayerService.REPEAT_ALL;
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                playmode = MusicPlayerService.REPEAT_NORMAL;
            }
            //保存到服务里面
            service.setPlaymode(playmode);

            setButtonImage();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void setButtonImage() {
        try {
            //从服务得到播放模式
            int playmode = service.getPlaymode();
            if (playmode == MusicPlayerService.REPEAT_NORMAL) {
                ibFor.setBackgroundResource(R.drawable.audio_for_control_select);
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                ibFor.setBackgroundResource(R.drawable.audio_for1_control_select);
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                ibFor.setBackgroundResource(R.drawable.audio_for2_control_select);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }


    //销毁时解绑
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conn != null) {
            unbindService(conn);
            conn = null;
        }

        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;

        }

    }

    public void getData() {
        notification = getIntent().getBooleanExtra("notification", false);
        if (!notification) {
            position = getIntent().getIntExtra("position", 0);
        }

    }
}
