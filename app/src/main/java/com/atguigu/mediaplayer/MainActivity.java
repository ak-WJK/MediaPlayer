package com.atguigu.mediaplayer;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.atguigu.mediaplayer.fragment.ListViewFragment;
import com.atguigu.mediaplayer.fragment.LocalAudioFragment;
import com.atguigu.mediaplayer.fragment.LocalVideoFragment;
import com.atguigu.mediaplayer.fragment.NetAudioFragment;
import com.atguigu.mediaplayer.fragment.NetVideoFragment;

import java.util.ArrayList;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

public class MainActivity extends AppCompatActivity {
    private FrameLayout fl_add_fragment;
    private RadioGroup rg_main;
    private ArrayList<BaseFragment> fragments;

    SensorManager sensorManager;

    JCVideoPlayer.JCAutoFullscreenListener sensorEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rg_main = (RadioGroup) findViewById(R.id.rg_main);

        initFragment();


        fl_add_fragment = (FrameLayout) findViewById(R.id.fl_add_fragment);
        rg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        rg_main.check(R.id.rb_local_video);


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorEventListener = new JCVideoPlayer.JCAutoFullscreenListener();

        isGrantExternalRW(this);

    }


    @Override
    protected void onResume() {
        super.onResume();

        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);


    }


    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(sensorEventListener);
        JCVideoPlayer.releaseAllVideos();

    }

    @Override
    public void onBackPressed() {

        if (JCVideoPlayer.backPress()) {
            return;

        }
        super.onBackPressed();
    }

    private void initFragment() {
        fragments = new ArrayList<>();
        fragments.add(new LocalVideoFragment());
        fragments.add(new LocalAudioFragment());
        fragments.add(new NetVideoFragment());
        fragments.add(new NetAudioFragment());
        fragments.add(new ListViewFragment());
    }


    private Fragment tempFragment;
    private int position;

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            switch (checkedId) {
                case R.id.rb_local_video:
                    position = 0;
                    break;
                case R.id.rb_local_audio:
                    position = 1;
                    break;
                case R.id.rb_net_video:
                    position = 2;
                    break;
                case R.id.rb_net_audio:
                    position = 3;
                    break;
                case R.id.rb_net_listview:
                    position = 4;
                    break;
            }
            BaseFragment currFragment = fragments.get(position);
            addFragment(currFragment);

        }
    }

    private void addFragment(Fragment currFragment) {
        //
        if (tempFragment != currFragment) {

            FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
            //判断fragment是否添加--没有添加
            if (!currFragment.isAdded()) {
                if (tempFragment != null) {
                    //隐藏之前的
                    fm.hide(tempFragment);
                }
                //添加现在的
                fm.add(R.id.fl_add_fragment, currFragment);

            } else {
                //添加 --> 隐藏之前的
                if (tempFragment != null) {
                    fm.hide(tempFragment);
                }
                //显示现在的
                fm.show(currFragment);
            }
            fm.commit();
            tempFragment = currFragment;

        }

    }

    /**
     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     *
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }

    boolean isExit = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (position != 0) {
                rg_main.check(R.id.rb_local_video);
                return true;
            } else if (!isExit) {
                Toast.makeText(MainActivity.this, "两秒内点击退出", Toast.LENGTH_SHORT).show();
                isExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);
                return true;

            }
        }


        return super.onKeyDown(keyCode, event);
    }
}