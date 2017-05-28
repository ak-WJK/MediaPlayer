package com.atguigu.mediaplayer.viewhodler;

import android.view.View;
import android.widget.TextView;

import com.atguigu.mediaplayer.R;
import com.atguigu.mediaplayer.domain.NetListBean;

/**
 * Created by Administrator on 2017/5/28.
 */

public class TextHodler extends BaseViewHolder {
    private NetListBean.ListBean data;
    private TextView tvContext;

    public TextHodler(View convertView) {
        super(convertView);
        tvContext = (TextView) convertView.findViewById(R.id.tv_context);


    }

    public void setData(NetListBean.ListBean data) {
        this.data = data;

        tvContext.setText(data.getText() + "_" + data.getText());

    }
}
