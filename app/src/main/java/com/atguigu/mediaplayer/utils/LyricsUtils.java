package com.atguigu.mediaplayer.utils;

import com.atguigu.mediaplayer.domain.Lyric;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Administrator on 2017/5/26.
 */

public class LyricsUtils {

    private boolean isLyric = false;
    private FileInputStream fis;
    private ArrayList<Lyric> lyrics;


    /**
     * 是否歌词存在
     *
     * @return
     */
    public boolean isLyric() {
        return isLyric;
    }


    public void readFile(File file) {
        if (file == null || !file.exists()) {
            isLyric = false;
        } else {
            try {
                lyrics = new ArrayList<>();
                isLyric = true;
                //一行一行读取文件

                fis = new FileInputStream(file);

                //将字节流转换为字符流
                InputStreamReader isr = new InputStreamReader(fis, "GBK");
                //包装缓冲流
                BufferedReader bufferedReader = new BufferedReader(isr);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    //解析每一句歌词
                    analysisLyric(line);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Collections.sort(lyrics, new Comparator<Lyric>() {
                @Override
                public int compare(Lyric o1, Lyric o2) {
                    if (o1.getTimePoint() < o2.getTimePoint()) {
                        return -1;
                    } else if (o1.getTimePoint() > o1.getTimePoint()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });

            //计算每一条的高亮的的持续时间
            for (int i = 0; i < lyrics.size(); i++) {
                Lyric onelyric = lyrics.get(i);
                if (i + 1 < lyrics.size()) {
                    Lyric twolyric = lyrics.get(i + 1);
                    onelyric.setSleepTime(twolyric.getTimePoint() - onelyric.getTimePoint());
                }

            }


        }


    }

    //解析歌词
    private void analysisLyric(String line) {
        int pos1 = line.indexOf("[");
        int pos2 = line.indexOf("]");
        if (pos1 == 0 && pos2 != -1) {//至少有一行

            long[] timelongs = new long[getcountTag(line)];
            String timeStr = line.substring(pos1 + 1, pos2);//00:59.73
            //将事件解析为long型
            timelongs[0] = stringToLong(timeStr);
            if (timelongs[0] == -1) {
                return;

            }

            int i = 1;
            String content = line;
            while (pos1 == 0 && pos2 != -1) {
                content = content.substring(pos2 + 1);
                pos1 = content.indexOf("[");
                pos2 = content.indexOf("]");
                if (pos1 == 0 && pos2 != -1) {

                    timeStr = content.substring(pos1 + 1, pos2);
                    timelongs[i] = stringToLong(timeStr);

                    if (timelongs[i] != -1) {
                        return;
                    }
                    i++;
                }

            }

            for (int j = 0; j < timelongs.length; j++) {
                if (timelongs[j] != 0) {
                    Lyric lyric = new Lyric();

                    lyric.setContent(content);
                    lyric.setTimePoint(timelongs[j]);

                    lyrics.add(lyric);


                }
            }


        }


    }

    private long stringToLong(String timeStr) {
        long result = -1;

        try {
            String[] s1 = timeStr.split(":");
            String[] s2 = s1[1].split("\\.");

            long min = Long.valueOf(s1[0]);
            long second = Long.valueOf(s2[0]);
            long mil = Long.valueOf(s2[1]);

            result = min * 60 * 1000 + second * 1000 + mil * 10;

        } catch (Exception e) {

        }


        return result;
    }


    //    [02:04.12][03:37.32][00:59.73]我在这里欢笑
//    s1[  02:04.12] ,  03:37.32] ,  00:59.73]我在这里欢笑    ]
//    s2[  [02:04.12 ,  [03:37.32 ,  [00:59.73  , 我在这里欢笑    ]
    private int getcountTag(String line) {
        int result = 1;
        String[] s1 = line.split("\\[");
        String[] s2 = line.split("\\]");
        if (s1.length == 0 && s2.length == 0) {
            result = 1;
        } else if (s1.length > s2.length) {
            result = s2.length;


        } else {
            result = s1.length;
        }


        return result;
    }

    public ArrayList<Lyric> getLyric() {
        return lyrics;
    }
}
