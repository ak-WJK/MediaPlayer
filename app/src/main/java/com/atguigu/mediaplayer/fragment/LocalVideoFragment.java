package com.atguigu.mediaplayer.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.atguigu.mediaplayer.BaseFragment;
import com.atguigu.mediaplayer.R;
import com.atguigu.mediaplayer.adapter.LocalMediaAdapter;
import com.atguigu.mediaplayer.domain.LocalMediaBean;
import com.atguigu.mediaplayer.player.SystemVideoPlayerActivity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/19.
 */

public class LocalVideoFragment extends BaseFragment {
    private TextView tv_hint;
    private ListView listview;
    private LocalMediaAdapter adapter;

    private ArrayList<LocalMediaBean> mediaBeens;


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (mediaBeens != null && mediaBeens.size() > 0) {
                tv_hint.setVisibility(View.GONE);

                adapter = new LocalMediaAdapter(context, mediaBeens);

                listview.setAdapter(adapter);

                listview.setOnItemClickListener(new MyOnItemClickListener());


            } else {
                tv_hint.setVisibility(View.VISIBLE);
            }


        }
    };


    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            LocalMediaBean item = adapter.getItem(position);

            //调用系统播发器播放视频
//            Intent intent = new Intent();
//            intent.setDataAndType(Uri.parse(item.getAddress()), "video/*");
//            startActivity(intent);


            Intent intent = new Intent(context,SystemVideoPlayerActivity.class);
            intent.setDataAndType(Uri.parse(item.getAddress()), "video/*");
            startActivity(intent);




        }
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.local_video_layout, null);

        tv_hint = (TextView) view.findViewById(R.id.tv_hint);
        listview = (ListView) view.findViewById(R.id.listview);

        return view;
    }

    @Override
    public void initData() {
        super.initData();

        getData();


    }

    public void getData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mediaBeens = new ArrayList<LocalMediaBean>();
                ContentResolver resolver = context.getContentResolver();

                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] obj = {
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DURATION,
                        MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.DATA,

                };
                Cursor cursor = resolver.query(uri, obj, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                        String address = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));

                        mediaBeens.add(new LocalMediaBean(name, duration, size, address));
                    }
                    cursor.close();
                }
                handler.sendEmptyMessage(0);
            }
        }.start();


    }
}
