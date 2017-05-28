package com.atguigu.mediaplayer.viewhodler;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.mediaplayer.R;
import com.atguigu.mediaplayer.domain.NetListBean;
import com.atguigu.mediaplayer.utils.LogUtils;
import com.atguigu.mediaplayer.utils.Utils;
import com.bumptech.glide.Glide;

import org.xutils.x;

import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by Administrator on 2017/5/27.
 */

public class VideoHodler extends BaseViewHolder {


    private NetListBean.ListBean data;

    Utils utils;
    TextView tvContext;
    JCVideoPlayerStandard jcvVideoplayer;
    TextView tvPlayNums;
    TextView tvVideoDuration;
    ImageView ivCommant;
    TextView tvCommantContext;

    private Context context;

    public VideoHodler(View convertView, Context context) {
        super(convertView);

        this.context = context;

        tvContext = (TextView) convertView.findViewById(R.id.tv_context);
        utils = new Utils();
        tvPlayNums = (TextView) convertView.findViewById(R.id.tv_play_nums);
        tvVideoDuration = (TextView) convertView.findViewById(R.id.tv_video_duration);
        ivCommant = (ImageView) convertView.findViewById(R.id.iv_commant);
        tvCommantContext = (TextView) convertView.findViewById(R.id.tv_commant_context);
        jcvVideoplayer = (JCVideoPlayerStandard) convertView.findViewById(R.id.jcv_videoplayer);


    }


    public void setData(NetListBean.ListBean data) {
        this.data = data;
        if (data.getU() != null && data.getU().getHeader() != null && data.getU().getHeader().get(0) != null) {
            x.image().bind(ivHeadpic, data.getU().getHeader().get(0));

            if (data.getU() != null && data.getU().getName() != null) {
                tvName.setText(data.getU().getName() + "");
            }
            tvTimeRefresh.setText(data.getPasstime());


            //设置标签
            List<NetListBean.ListBean.TagsBean> tagsBeanList = data.getTags();
            if (tagsBeanList != null && tagsBeanList.size() > 0) {
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < tagsBeanList.size(); i++) {
                    buffer.append(tagsBeanList.get(i).getName() + " ");
                }
                tvVideoKindText.setText(buffer.toString());
            }


            //设置点赞，踩,转发
            tvShenheDingNumber.setText(data.getUp());
            tvShenheCaiNumber.setText(data.getDown() + "");
            tvPostsNumber.setText(data.getForward() + "");


            tvContext.setText(data.getText() + "_" + data.getText());


//            String url = "http://wvideo.spriteapp.cn/video/2017/0527/a1f44fc4-42cf-11e7-8dd3-1866daeb0df1_wpd.mp4";
//
//            boolean setUp = jcvVideoplayer.setUp(url, jcvVideoplayer.SCREEN_LAYOUT_LIST);
//
            boolean setUp = jcvVideoplayer.setUp(data.getVideo().getVideo().get(0), JCVideoPlayer.SCREEN_LAYOUT_LIST, "");

            LogUtils.e("TAG", "url=====" + data.getVideo().getVideo().get(0));

            if (setUp) {

                Glide.with(context).load(data.getVideo().getThumbnail_small().get(0)).into(jcvVideoplayer.thumbImageView);

                LogUtils.e("TAG", "thumbnail" + data.getVideo().getThumbnail_small().get(0));

            }

            tvPlayNums.setText(data.getVideo().getPlaycount() + "次播放");

            tvVideoDuration.setText(utils.stringForTime(data.getVideo().getDuration() * 1000) + "");

        }


    }
}
