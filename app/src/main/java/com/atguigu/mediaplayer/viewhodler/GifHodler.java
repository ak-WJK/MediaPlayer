package com.atguigu.mediaplayer.viewhodler;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.mediaplayer.R;
import com.atguigu.mediaplayer.domain.NetListBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.xutils.image.ImageOptions;

/**
 * Created by Administrator on 2017/5/28.
 */

public class GifHodler extends BaseViewHolder {
    private NetListBean.ListBean data;

    TextView tvContext;
    ImageView ivImageGif;
    private ImageOptions imageOptions;
    private Context context;


    public GifHodler(View convertView, Context context) {
        super(convertView);
        this.context = context;

        tvContext = (TextView) convertView.findViewById(R.id.tv_context);
        ivImageGif = (ImageView) convertView.findViewById(R.id.iv_image_gif);


//        imageOptions = new ImageOptions.Builder()
//                //包裹类型
//                .setSize(ViewGroup.LayoutParams.WRAP_CONTENT, -2)
//                //设置圆角
//                .setRadius(DensityUtil.dip2px(5))
//                .setIgnoreGif(false)//是否忽略gif图。false表示不忽略。不写这句，默认是true
//                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
//                .setLoadingDrawableId(R.drawable.video_default)
//                .setFailureDrawableId(R.drawable.video_default)
//                .build();

    }

    public void setData(NetListBean.ListBean data) {
        this.data = data;


        tvContext.setText(data.getText() + "_" + data.getType());

        if (data.getGif() != null && data.getGif().getImages() != null) {

            Glide.with(context)
                    .load(data.getGif().getImages().get(0))
                    .asGif()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(ivImageGif);

//            x.image().bind(ivImageGif, data.getGif().getImages().get(0), imageOptions);
        }


    }
}
