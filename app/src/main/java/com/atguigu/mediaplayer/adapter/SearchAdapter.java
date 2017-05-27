package com.atguigu.mediaplayer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.mediaplayer.R;
import com.atguigu.mediaplayer.domain.SearchBean;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/5/22.
 */

public class SearchAdapter extends BaseAdapter {

    private final Context context;
    private final List<SearchBean.ItemsBean> itemsBeen;
//    private  ImageOptions imageOptions;


    public SearchAdapter(Context context, List<SearchBean.ItemsBean> itemsBeen) {
        this.context = context;
        this.itemsBeen = itemsBeen;

        Log.e("TAG", "itemsBean" + itemsBeen.size());

//        imageOptions = new ImageOptions.Builder()
//                .setIgnoreGif(false)//是否忽略gif图。false表示不忽略。不写这句，默认是true
//                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
//                .setFailureDrawableId(R.drawable.video_default)
//                .setLoadingDrawableId(R.drawable.video_default)
//                .build();


    }

    @Override
    public int getCount() {

        return itemsBeen.size();
    }

    @Override
    public SearchBean.ItemsBean getItem(int position) {
        return itemsBeen.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_net_video, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //根据位置得到对应的数据
        SearchBean.ItemsBean trailersBean = itemsBeen.get(position);

        viewHolder.tvName.setText(trailersBean.getItemTitle());
        viewHolder.tvSize.setText(trailersBean.getDatecheck());
        viewHolder.tvDuration.setText(trailersBean.getKeywords());
//        x.image().bind(viewHolder.ivIcon, trailersBean.getCoverImg(), imageOptions);

        Picasso.with(context)
                .load(trailersBean.getItemImage().getImgUrl1())
                .placeholder(R.drawable.video_default)
                .error(R.drawable.video_default)
                .into(viewHolder.ivIcon);


        return convertView;


    }

    static class ViewHolder {
        @Bind(R.id.iv_icon)
        ImageView ivIcon;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_duration)
        TextView tvDuration;
        @Bind(R.id.tv_size)
        TextView tvSize;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
