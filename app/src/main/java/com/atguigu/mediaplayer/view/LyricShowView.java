package com.atguigu.mediaplayer.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.atguigu.mediaplayer.domain.Lyric;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/26.
 */

public class LyricShowView extends android.support.v7.widget.AppCompatTextView {
    private int width;
    private int height;
    private Paint paintGreen;
    private Paint paintWhite;

    private ArrayList<Lyric> lyricsLists;
    private float textHeight = 100;
    private int currentPosition;


    public LyricShowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        paintGreen = new Paint();
        paintGreen.setColor(Color.GREEN);
        paintGreen.setAntiAlias(true);
        paintGreen.setTextSize(66);
        paintGreen.setTextAlign(Paint.Align.CENTER);


        paintWhite = new Paint();
        paintWhite.setColor(Color.WHITE);
        paintWhite.setAntiAlias(true);
        paintWhite.setTextSize(66);
        paintWhite.setTextAlign(Paint.Align.CENTER);


//        lyricsLists = new ArrayList<>();
//        Log.e("TAG", "aaaaa" + lyricsLists);
//        Lyric lyric = new Lyric();
//        for (int i = 0; i < 1000; i++) {
//            lyric.setContent("abcdefg " + i);
//            lyric.setTimePoint((long) 2000);
//            lyric.setSleepTime((long) (2000 * i));
//
//            lyricsLists.add(lyric);
//
//            lyric = new Lyric();
//
//        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height = h;
        width = w;

    }

    private int index = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制歌词
        if (lyricsLists != null && lyricsLists.size() > 0) {


            //绘制中间的歌词
            String currentContent = lyricsLists.get(index).getContent();
            canvas.drawText(currentContent, width / 2, height / 2, paintGreen);


            //得到中间句的坐标
            float tempY = height / 2;


            //绘制上半部分歌词
            for (int i = index - 1; i >= 0; i--) {
                //得到歌词
                String preContent = lyricsLists.get(i).getContent();

                //得到每一句的坐标

                tempY = tempY - textHeight;
                if (tempY < 0) {
                    break;
                }
                canvas.drawText(preContent, width / 2, tempY, paintWhite);

            }


            //重新赋值
            tempY = height / 2;

            //绘制下半部分
            for (int i = index + 1; i < lyricsLists.size(); i++) {
                //得到每一行的内容
                String nextContent = lyricsLists.get(i).getContent();
                //得到每一行的坐标
                tempY = tempY + textHeight;

                if (tempY > height) {
                    break;
                }
                canvas.drawText(nextContent, width / 2, tempY, paintWhite);

            }


        } else {
            canvas.drawText("没有歌词", width / 2, height / 2, paintGreen);
        }


    }

//    //同步歌词
//    public void setShowLyric(int currentPosition) {
//        this.currentPosition = currentPosition;
//        if (lyricsLists == null || lyricsLists.size() == 0)
//            return;
//
//
//        for (int i = 1; i < lyricsLists.size(); i++) {
//            if (currentPosition < lyricsLists.get(i).getTimePoint()) {
//                int tempindex = i - 1;
//                if (currentPosition >= lyricsLists.get(tempindex).getTimePoint()) {
//                    index = tempindex;
//                }
//
//            }
//
//        }
//
//        invalidate();
//    }


    /**
     * 根据播放的位置查找或者计算出当前该高亮显示的是哪一句
     * 并且得到这一句对应的相关信息
     *
     * @param currentPosition
     */
    public void setShowLyric(int currentPosition) {

        this.currentPosition = currentPosition;
        if (lyricsLists == null || lyricsLists.size() == 0)
            return;

        for (int i = 1; i < lyricsLists.size(); i++) {

            if (currentPosition < lyricsLists.get(i).getTimePoint()) {
                int tempIndex = i - 1;
                if (currentPosition >= lyricsLists.get(tempIndex).getTimePoint()) {
                    //中间高亮显示的哪一句
                    index = tempIndex;
                }
            }

        }

        //什么方法导致onDraw
        invalidate();


    }









    public void setLyrics(ArrayList<Lyric> lyrics) {
        this.lyricsLists = lyrics;

    }
}
