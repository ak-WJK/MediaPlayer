package com.atguigu.mediaplayer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.atguigu.mediaplayer.BaseFragment;
import com.atguigu.mediaplayer.R;
import com.atguigu.mediaplayer.adapter.NetVideoAdapter;
import com.atguigu.mediaplayer.domain.LocalMediaBean;
import com.atguigu.mediaplayer.domain.MoveInfo;
import com.atguigu.mediaplayer.player.SystemVideoPlayerActivity;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/19.
 */

public class NetVideoFragment extends BaseFragment {
    private ListView lv;
    private TextView tv_nodata;
    private NetVideoAdapter adapter;
    private Intent intent;
    private MaterialRefreshLayout refresh;
    private boolean isLoadMore = false;
    private ArrayList<LocalMediaBean> mediaBeens;
    private List<MoveInfo.TrailersBean> datas;

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.fragment_net_video_pager, null);
        lv = (ListView) view.findViewById(R.id.lv);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);
        refresh = (MaterialRefreshLayout) view.findViewById(R.id.refresh);
        refresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            //下拉刷新
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                isLoadMore = false;
                getDataFromNet();
                Log.e("TAG", "isLoadMore4" + isLoadMore);

            }

            //上拉加载
            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);
                isLoadMore = true;
                getMoreData();
                Log.e("TAG", "isLoadMore5" + isLoadMore);
            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MoveInfo.TrailersBean item = adapter.getItem(position);

//                Intent intent = new Intent(context, SystemVideoPlayerActivity.class);
//                intent.setDataAndType(Uri.parse(item.getUrl()), "video/*");
//                startActivity(intent);


                if (mediaBeens != null && mediaBeens.size() > 0) {
                    Intent intent = new Intent(context, SystemVideoPlayerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("mediaBeens", mediaBeens);

                    Log.e("TAG", "mediaBeans" + mediaBeens.size());

                    intent.putExtras(bundle);
                    intent.putExtra("position", position);
                    startActivity(intent);
                }

            }
        });


        return view;
    }

    @Override
    public void initData() {

        getDataFromNet();


    }

    public void getDataFromNet() {
        RequestParams requestParams = new RequestParams("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                processData(result);
                refresh.finishRefresh();

//                try {
//                    analysisJson(result);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    private ArrayList<LocalMediaBean> lists;

    private void analysisJson(String json) throws JSONException {

        lists = new ArrayList<>();


        JSONObject jsonObject = new JSONObject(json);
        JSONArray trailers = jsonObject.optJSONArray("trailers");
        if (trailers != null && trailers.length() > 0) {
            for (int i = 0; i < trailers.length(); i++) {
                JSONObject object = (JSONObject) trailers.get(i);

                String movieName = object.optString("movieName");
                String videoTitle = object.optString("videoTitle");
                String uri = object.optString("url");
                long videoLength = object.optLong("videoLength");
                String coverImg1 = object.optString("coverImg");

                lists.add(new LocalMediaBean(movieName, videoLength, videoLength, uri));

            }

        }


    }


    private void processData(String json) {
        MoveInfo moveInfo = new Gson().fromJson(json, MoveInfo.class);


        if (!isLoadMore) {
            datas = moveInfo.getTrailers();

            if (datas != null && datas.size() > 0) {
                mediaBeens = new ArrayList<>();
                for (int i = 0; i < datas.size(); i++) {
                    MoveInfo.TrailersBean trailersBean = datas.get(i);
                    LocalMediaBean bean = new LocalMediaBean();
                    bean.setAddress(trailersBean.getUrl());
                    bean.setName(trailersBean.getMovieName());
                    mediaBeens.add(bean);
                }

                tv_nodata.setVisibility(View.GONE);
                adapter = new NetVideoAdapter(context, datas);
                lv.setAdapter(adapter);

            } else {
                tv_nodata.setVisibility(View.VISIBLE);
            }

        } else {

            List<MoveInfo.TrailersBean> trailersBeanList = moveInfo.getTrailers();

            for (int i = 0; i < datas.size(); i++) {
                LocalMediaBean bean = new LocalMediaBean();
                bean.setAddress(trailersBeanList.get(i).getUrl());
                bean.setName(trailersBeanList.get(i).getMovieName());
                mediaBeens.add(bean);
            }

            //加入到原来集合的数据
            datas.addAll(trailersBeanList);

            //刷新适配器
            adapter.notifyDataSetChanged();

        }


    }


    public void datasList() {
        mediaBeens = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            MoveInfo.TrailersBean trailersBean = datas.get(i);
            LocalMediaBean bean = new LocalMediaBean();
            bean.setAddress(trailersBean.getUrl());
            bean.setName(trailersBean.getMovieName());
            mediaBeens.add(bean);
        }

    }


    public void getMoreData() {
        RequestParams requestParams = new RequestParams("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                processData(result);
                refresh.finishRefreshLoadMore();


//                try {
//                    analysisJson(result);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });


    }
}
