package com.atguigu.mediaplayer.viewhodler;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atguigu.mediaplayer.R;

/**
 * Created by Administrator on 2017/5/28.
 */

public class BaseViewHolder {
    ImageView ivHeadpic;
    TextView tvName;
    TextView tvTimeRefresh;
    ImageView ivRightMore;
    ImageView ivVideoKind;
    TextView tvVideoKindText;
    TextView tvShenheDingNumber;
    TextView tvShenheCaiNumber;
    TextView tvPostsNumber;
    LinearLayout llDownload;

    public BaseViewHolder (View convertView ) {

        //公共的
        ivHeadpic = (ImageView) convertView.findViewById(R.id.iv_headpic);
        tvName = (TextView) convertView.findViewById(R.id.tv_name);
        tvTimeRefresh = (TextView) convertView.findViewById(R.id.tv_time_refresh);
        ivRightMore = (ImageView) convertView.findViewById(R.id.iv_right_more);

        //bottom
        ivVideoKind = (ImageView) convertView.findViewById(R.id.iv_video_kind);
        tvVideoKindText = (TextView) convertView.findViewById(R.id.tv_video_kind_text);
        tvShenheDingNumber = (TextView) convertView.findViewById(R.id.tv_shenhe_ding_number);
        tvShenheCaiNumber = (TextView) convertView.findViewById(R.id.tv_shenhe_cai_number);
        tvPostsNumber = (TextView) convertView.findViewById(R.id.tv_posts_number);
        llDownload = (LinearLayout) convertView.findViewById(R.id.ll_download);
    }





}
