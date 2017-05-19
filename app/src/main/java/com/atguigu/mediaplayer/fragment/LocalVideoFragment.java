package com.atguigu.mediaplayer.fragment;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.atguigu.mediaplayer.BaseFragment;

/**
 * Created by Administrator on 2017/5/19.
 */

public class LocalVideoFragment extends BaseFragment {

    private TextView textView;

    @Override
    public View initView() {
        textView = new TextView(context);
        textView.setTextSize(20);
        textView.setTextColor(Color.BLACK);
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        textView.setText("本地视频");

    }
}
