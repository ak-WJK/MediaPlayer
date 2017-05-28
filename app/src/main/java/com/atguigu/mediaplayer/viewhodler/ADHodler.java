package com.atguigu.mediaplayer.viewhodler;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.mediaplayer.R;
import com.atguigu.mediaplayer.domain.NetListBean;

/**
 * Created by Administrator on 2017/5/28.
 */

public class ADHodler extends BaseViewHolder {
    private NetListBean.ListBean data;
    TextView tvContext;
    ImageView ivImageIcon;
    Button btnInstall;


    public ADHodler(View convertView) {
        super(convertView);
        tvContext = (TextView) convertView.findViewById(R.id.tv_context);
        btnInstall = (Button) convertView.findViewById(R.id.btn_install);
        ivImageIcon = (ImageView) convertView.findViewById(R.id.iv_image_icon);
    }

    public void setData(NetListBean.ListBean data) {
        this.data = data;




    }
}
