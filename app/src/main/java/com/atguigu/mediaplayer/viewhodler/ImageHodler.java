package com.atguigu.mediaplayer.viewhodler;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.mediaplayer.R;
import com.atguigu.mediaplayer.domain.NetListBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by Administrator on 2017/5/28.
 */

public class ImageHodler extends BaseViewHolder {
    private NetListBean.ListBean data;

    TextView tvContext;
    ImageView ivImageIcon;
private Context context;

    public ImageHodler(View convertView, Context context) {
        super(convertView);
        this.context = context;

        tvContext = (TextView) convertView.findViewById(R.id.tv_context);
        ivImageIcon = (ImageView) convertView.findViewById(R.id.iv_image_icon);



    }


    public void setData(NetListBean.ListBean data) {
        this.data = data;

        tvContext.setText(data.getText()+ "_" + data.getType());

        ivImageIcon.setImageResource(R.drawable.bg_item);
        if(data.getImage() != null && data.getImage().getSmall()!= null) {
            Glide.with(context).load(data.getImage().getDownload_url().get(0))
                    .placeholder(R.drawable.bg_item)
                    .error(R.drawable.bg_item)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivImageIcon);

        }



    }
}
