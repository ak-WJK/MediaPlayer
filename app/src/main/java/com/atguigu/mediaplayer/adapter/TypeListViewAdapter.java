package com.atguigu.mediaplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.atguigu.mediaplayer.R;
import com.atguigu.mediaplayer.domain.NetListBean;
import com.atguigu.mediaplayer.viewhodler.ADHodler;
import com.atguigu.mediaplayer.viewhodler.GifHodler;
import com.atguigu.mediaplayer.viewhodler.ImageHodler;
import com.atguigu.mediaplayer.viewhodler.TextHodler;
import com.atguigu.mediaplayer.viewhodler.VideoHodler;

import java.util.List;

/**
 * Created by Administrator on 2017/5/27.
 */

public class TypeListViewAdapter extends BaseAdapter {

    private final Context context;
    private final List<NetListBean.ListBean> listBeen;


    /**
     * 视频
     */
    private static final int TYPE_VIDEO = 0;

    /**
     * 图片
     */
    private static final int TYPE_IMAGE = 1;

    /**
     * 文字
     */
    private static final int TYPE_TEXT = 2;

    /**
     * GIF图片
     */
    private static final int TYPE_GIF = 3;


    /**
     * 软件推广
     */
    private static final int TYPE_AD = 4;


    public TypeListViewAdapter(Context context, List<NetListBean.ListBean> listBeen) {

        this.context = context;
        this.listBeen = listBeen;
    }


    @Override
    public int getCount() {
        return listBeen.size();
    }

    /**
     * 返回总数据类型
     *
     * @return
     */
    @Override
    public int getViewTypeCount() {
        return 5;
    }

    /**
     * 当前的item是什么类型
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        int itemViewType = -1;

        NetListBean.ListBean listBean = listBeen.get(position);
        String type = listBean.getType();
        if ("video".equals(type)) {

            itemViewType = TYPE_VIDEO;

        } else if ("image".equals(type)) {

            itemViewType = TYPE_IMAGE;

        } else if ("text".equals(type)) {

            itemViewType = TYPE_TEXT;

        } else if ("gif".equals(type)) {

            itemViewType = TYPE_GIF;

        }else  {
            itemViewType = TYPE_AD;
        }

        return itemViewType;
    }

    @Override
    public Object getItem(int position) {
        return listBeen.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = initView(convertView, getItemViewType(position), listBeen.get(position));

        return convertView;
    }

    private View initView(View convertView, int itemViewType, NetListBean.ListBean listBean) {

        switch (itemViewType) {
            case TYPE_VIDEO:


                VideoHodler videoHodler;
                if(convertView == null) {

                    convertView = View.inflate(context, R.layout.video_item, null);
                    videoHodler = new VideoHodler(convertView , context);

                    convertView.setTag(videoHodler);
                }else{
                  videoHodler= (VideoHodler) convertView.getTag();
                }

                    videoHodler.setData(listBean);


                break;
            case TYPE_IMAGE:

                ImageHodler  imageHodler;
                if(convertView == null) {

                    convertView = View.inflate(context, R.layout.image_item, null);
                    imageHodler = new ImageHodler(convertView , context);

                    convertView.setTag(imageHodler);
                }else{
                    imageHodler= (ImageHodler) convertView.getTag();
                }

                imageHodler.setData(listBean);


                break;
            case TYPE_TEXT:
                TextHodler textHodler;
                if(convertView == null) {

                    convertView = View.inflate(context, R.layout.text_item, null);
                    textHodler = new TextHodler(convertView);

                    convertView.setTag(textHodler);
                }else{
                    textHodler= (TextHodler) convertView.getTag();
                }


                textHodler.setData(listBean);


                break;
            case TYPE_GIF:

                GifHodler gifHodler;
                if(convertView == null) {

                    convertView = View.inflate(context, R.layout.gif_item, null);
                    gifHodler = new GifHodler(convertView ,context);

                    convertView.setTag(gifHodler);
                }else{
                    gifHodler= (GifHodler) convertView.getTag();
                }

                gifHodler.setData(listBean);


                break;
            case TYPE_AD:

                ADHodler adHodler;
                if(convertView == null) {

                    convertView = View.inflate(context, R.layout.ad_item, null);
                    adHodler = new ADHodler(convertView);

                    convertView.setTag(adHodler);
                }else{
                    adHodler= (ADHodler) convertView.getTag();
                }

                adHodler.setData(listBean);

                break;
        }


        return convertView;
    }
}
