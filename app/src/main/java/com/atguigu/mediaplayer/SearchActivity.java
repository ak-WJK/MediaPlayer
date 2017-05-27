package com.atguigu.mediaplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.mediaplayer.adapter.SearchAdapter;
import com.atguigu.mediaplayer.domain.SearchBean;
import com.atguigu.mediaplayer.utils.JsonParser;
import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText etSousuo;
    private ImageView ivVoice;
    private TextView tvGo;
    private ListView lv;


    public static final String NET_SEARCH_URL = "http://hot.news.cntv.cn/index.php?controller=list&action=searchList&sort=date&n=20&wd=";

    public String newUrl;


    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private List<SearchBean.ItemsBean> itemsBeen;
    private SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        findViews();
    }


    private void findViews() {
        etSousuo = (EditText) findViewById(R.id.et_sousuo);
        ivVoice = (ImageView) findViewById(R.id.iv_voice);
        tvGo = (TextView) findViewById(R.id.tv_go);
        lv = (ListView) findViewById(R.id.lv);

        //设置点击事件
        ivVoice.setOnClickListener(this);
        tvGo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_voice:
                //语言输入
                showVoiceDialog();
                break;
            case R.id.tv_go:
                toSearch();
                break;
        }


    }

    private void toSearch() {

        String content = etSousuo.getText().toString();

        newUrl = NET_SEARCH_URL + content;

        RequestParams request = new RequestParams(newUrl);
        x.http().get(request, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "result" + result);
                analysisJson(result);

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

    private void analysisJson(String json) {
        SearchBean searchBean = new Gson().fromJson(json, SearchBean.class);
        itemsBeen = searchBean.getItems();
        Log.e("TAG", "itemsBean" + itemsBeen.size());
        if(itemsBeen!=null && itemsBeen.size()>0) {
            adapter = new SearchAdapter(SearchActivity.this,itemsBeen);
            lv.setAdapter(adapter);

        }



    }

    private void showVoiceDialog() {
//1.创建 RecognizerDialog 对象
        RecognizerDialog mDialog = new RecognizerDialog(this, new MyInitListener());

//若要将 RecognizerDialog 用于语义理解，必须添加以下参数设置，设置之后 onResult 回调返回将是语义理解的结果
// mDialog.setParameter("asr_sch", "1");
// mDialog.setParameter("nlp_version", "3.0");

//3.设置回调接口
        mDialog.setListener(new MyRecognizerDialogListener());

//4.显示 dialog，接收语音输入
        mDialog.show();

    }

    class MyRecognizerDialogListener implements RecognizerDialogListener {


        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {


            String resultString = recognizerResult.getResultString();

            printResult(recognizerResult);


        }

        @Override
        public void onError(SpeechError speechError) {

        }
    }

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        String str = resultBuffer.toString();
        String replaceStr = str.replace("。", "");


        etSousuo.setText(replaceStr);
        etSousuo.setSelection(etSousuo.length());


    }

    class MyInitListener implements InitListener {

        @Override
        public void onInit(int i) {
            if (i == ErrorCode.SUCCESS) {
                Toast.makeText(SearchActivity.this, "初始化成功", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
