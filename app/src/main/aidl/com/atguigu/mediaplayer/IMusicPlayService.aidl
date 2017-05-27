// IMusicPlayService.aidl
package com.atguigu.mediaplayer;

// Declare any non-default types here with import statements

interface IMusicPlayService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);



       /**
          * 根据位置点击实现播放和暂停
          *
          * @param postion
          */

         void openAudio(int position);

         /**
          * 播放音乐
          */
         void playerMusic();

         /**
          * 暂停播放
          */
         void pauseMusic();


         /**
          * 实现播放上一首
          */
          void preMusic();

         /**
          * 实现播放下一首
          */
          void nextMusic();


         /**
          * 得到时长
          */
        int  getDuration();

         /**
          * 得到大小
          */
         int getSize();

         /**
          * 得到音乐名
          */
          String getMusicName();

         /**
          * 得到艺术家
          */
          String getArtistName();

         /**
          * 得到播放音乐的当前进度
          */
        int getCurrentPosition();

           /**
          * 是否播放
          */
        boolean isPlayer();
           /**
          * 得到进度
          */
        void seekTo(int position);



           int getPlaymode();


          void setPlaymode(int playmode);
        /**
         * 得到歌曲地址
         */
        String audioPath();


}
