package com.atguigu.mediaplayer.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.mediaplayer.R;
import com.atguigu.mediaplayer.domain.LocalMediaBean;
import com.atguigu.mediaplayer.utils.Utils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/5/19.
 */

public class LocalMediaAdapter extends BaseAdapter {


    private final Context context;
    private final ArrayList<LocalMediaBean> mediaBeens;
    @Bind(R.id.iv_icon)
    ImageView ivIcon;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_duration)
    TextView tvDuration;
    @Bind(R.id.tv_size)
    TextView tvSize;
    private final Utils utils;

    public LocalMediaAdapter(Context context, ArrayList<LocalMediaBean> mediaBeens) {


        this.context = context;
        this.mediaBeens = mediaBeens;
        utils = new Utils();
    }

    @Override
    public int getCount() {
        return mediaBeens.size();
    }

    @Override
    public LocalMediaBean getItem(int position) {
        return mediaBeens.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHodler;
        if (convertView == null) {

            convertView = View.inflate(context, R.layout.local_video_item, null);
            viewHodler = new ViewHolder(convertView);

            convertView.setTag(viewHodler);
        } else {
            viewHodler = (ViewHolder) convertView.getTag();
        }
        LocalMediaBean mediaBean = mediaBeens.get(position);

        viewHodler.tvDuration.setText(utils.stringForTime((int) mediaBean.getDuration()));
        viewHodler.tvName.setText(mediaBean.getName());
        viewHodler.tvSize.setText(Formatter.formatFileSize(context, mediaBean.getSize()));

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
