package com.atguigu.mediaplayer.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.mediaplayer.BaseFragment;
import com.atguigu.mediaplayer.R;
import com.atguigu.mediaplayer.ShowImageAndGifActivity;
import com.atguigu.mediaplayer.adapter.TypeListViewAdapter;
import com.atguigu.mediaplayer.domain.NetListBean;
import com.atguigu.mediaplayer.utils.LogUtils;
import com.atguigu.mediaplayer.utils.SPUtils;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/5/19.
 */

public class ListViewFragment extends BaseFragment {

    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.progressbar)
    ProgressBar progressbar;
    @Bind(R.id.tv_nomedia)
    TextView tvNomedia;
    private TextView textView;
    private static final String URL = "http://s.budejie.com/topic/list/jingxuan/1/budejie-android-6.2.8/0-20.json?market=baidu&udid=863425026599592&appname=baisibudejie&os=4.2.2&client=android&visiting=&mac=98%3A6c%3Af5%3A4b%3A72%3A6d&ver=6.2.8";
    private TypeListViewAdapter adapter;
    private List<NetListBean.ListBean> listBeen;

    private static final  String TAG = "ListViewFragment===";

    @Override
    public View initView() {

        View view = View.inflate(context, R.layout.fragment_list_view, null);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void initData() {
        super.initData();


        //取出数据并解析
        final String saveJson = (String) SPUtils.get(context, "result", null);
        if (!TextUtils.isEmpty(saveJson)) {
            analysisJson(saveJson);
        }


        //联网请求数据
        getDataFromNet();


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                NetListBean.ListBean bean = listBeen.get(position);

                if (bean != null) {

                    Intent intent = new Intent(context, ShowImageAndGifActivity.class);
                    if (bean.getType().equals("gif")) {
                        String url = bean.getGif().getImages().get(0);
                        LogUtils.e(TAG + "gif", url);

                        intent.putExtra("url", url);
                        startActivity(intent);
                    } else if (bean.getType().equals("image")) {
                        String url = bean.getImage().getBig().get(0);
                        LogUtils.e(TAG + "image", url);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }

                }


            }
        });


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void getDataFromNet() {
        RequestParams request = new RequestParams(URL);
        x.http().get(request, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
//                LogUtils.e("TAG", "result====" + result);
                if (!TextUtils.isEmpty(result)) {
                    //存储数据
                    SPUtils.put(context, "result", result);
                    //解析数据
                    analysisJson(result);
                }


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
//                LogUtils.e("TAG", "网络连接失败onError" + ex.getMessage());
                Toast.makeText(context, "网络连接失败,不能更新数据", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    private void analysisJson(String json) {
        NetListBean netListBean = new Gson().fromJson(json, NetListBean.class);
        listBeen = netListBean.getList();
        //设置适配器
        if (listBeen != null && listBeen.size() > 0) {

            tvNomedia.setVisibility(View.GONE);

            adapter = new TypeListViewAdapter(context, listBeen);
            listview.setAdapter(adapter);
        } else {

            tvNomedia.setVisibility(View.VISIBLE);

        }
        progressbar.setVisibility(View.GONE);


    }
}
