package com.atguigu.mediaplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.atguigu.mediaplayer.IMusicPlayService;
import com.atguigu.mediaplayer.R;
import com.atguigu.mediaplayer.domain.LocalMediaBean;
import com.atguigu.mediaplayer.player.SystemVideoPlayerActivity;

import java.io.IOException;
import java.util.ArrayList;

public class MusicPlayerService extends Service {
    public static String GETDATAS = "com.atguigu.mediaplayer.service.MusicPlayerService";
    private MediaPlayer mediaPlayer;


    /**
     * 顺序播放
     */
    public static final int REPEAT_NORMAL = 1;

    /**
     * 单曲循环播放
     */
    public static final int REPEAT_SINGLE = 2;

    /**
     * 全部循环播放
     */
    public static final int REPEAT_ALL = 3;

    /**
     * 播放模式
     */
    private int playmode = REPEAT_NORMAL;


    /**
     * true:正常播放完成
     * false:人为手动点击下一个
     */
    private boolean isCompletion = false;

    private SharedPreferences sp;


    private ArrayList<LocalMediaBean> mediaBeens;
    IMusicPlayService.Stub stub = new IMusicPlayService.Stub() {

        MusicPlayerService service = MusicPlayerService.this;

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);

        }

        @Override
        public void playerMusic() throws RemoteException {
            service.playerMusic();

        }

        @Override
        public void pauseMusic() throws RemoteException {

            service.pauseMusic();
        }

        @Override
        public void preMusic() throws RemoteException {
            service.preMusic();
        }

        @Override
        public void nextMusic() throws RemoteException {
            service.nextMusic();

        }

        @Override
        public void forMusic() throws RemoteException {
            service.forMusic();

        }

        @Override
        public int getDuration() throws RemoteException {
            return mediaPlayer.getDuration();
        }

        @Override
        public int getSize() throws RemoteException {
            return service.getSize();
        }

        @Override
        public String getMusicName() throws RemoteException {
            return bean.getName();
        }

        @Override
        public String getArtistName() throws RemoteException {
            return bean.getArtist();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return mediaPlayer.getCurrentPosition();
        }

        @Override
        public boolean isPlayer() throws RemoteException {
            return mediaPlayer.isPlaying();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            service.seekTo(position);
        }

        @Override
        public int getPlaymode() throws RemoteException {
            return service.getPlaymode();
        }

        @Override
        public void setPlaymode(int playmode) throws RemoteException {
            service.setPlaymode(playmode);
        }
    };
    private MusicPlayerService service;
    /*
    * 音频下标的位置
    *
    * */
    private int position;
    private LocalMediaBean bean;
    private NotificationManager nm;


    public MusicPlayerService() {

    }

    @Override
    public IBinder onBind(Intent intent) {

        return stub;
    }

    /**
     * 根据位置点击实现播放和暂停
     *
     * @param position
     */

    public void openAudio(int position) {


        this.position = position;

        if (mediaBeens != null && mediaBeens.size() > 0) {
            if (position < mediaBeens.size()) {
                bean = mediaBeens.get(position);


                if (mediaPlayer != null) {
                    mediaPlayer.reset();
                    mediaPlayer = null;
                }
                mediaPlayer = new MediaPlayer();

                try {
                    Log.e("TAG", "openAudio+++bean" + bean.getAddress());
                    mediaPlayer.setDataSource(bean.getAddress());


                    mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
                    mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
                    mediaPlayer.setOnErrorListener(new MyOnErrorListener());

                    mediaPlayer.prepareAsync();

                    if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                        isCompletion = false;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MusicPlayerService.this, "音频还没有加载完成", Toast.LENGTH_SHORT).show();
            }


        }


    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            sendChangeBroadcast(GETDATAS);
            playerMusic();

        }
    }

    private void sendChangeBroadcast(String action) {

        Intent intent = new Intent(action);

        sendBroadcast(intent);

    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            isCompletion = true;
            nextMusic();

        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {


        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            nextMusic();
            return false;
        }
    }


    /**
     * 播放音乐
     */
    public void playerMusic() {
        mediaPlayer.start();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, SystemVideoPlayerActivity.class);
        intent.putExtra("notification", true);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notifation = new Notification.Builder(this)
                .setSmallIcon(R.drawable.notification_music_playing)
                .setContentTitle("321音乐")
                .setContentText("正在播放：" + getMusicName())
                .setContentIntent(pi)
                .build();
        nm.notify(1, notifation);


    }

    /**
     * 暂停播放
     */
    public void pauseMusic() {
        mediaPlayer.pause();
//取消通知
        nm.cancel(1);

    }


    /**
     * 实现播放上一首
     */
    public void preMusic() {
        //1.根据不同的播放模式设置不同的下标位置
        setPrePosition();

        //2.根据不同的下标位置打开对应的音频并且播放，边界处理
        openPrePosition();

    }


    private void openPrePosition() {
        int playmode = getPlaymode();

        if (playmode == MusicPlayerService.REPEAT_NORMAL) {
            if (position >= 0) {
                //合法范围
                openAudio(position);

            } else {
                //变为合法
                position = 0;
            }
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
            if (position >= 0) {
                //合法范围
                openAudio(position);
            } else {
                //变为合法
                position = 0;
            }

        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
            openAudio(position);
        }
    }

    private void setPrePosition() {
        int playmode = getPlaymode();

        if (playmode == MusicPlayerService.REPEAT_NORMAL) {
            //还没有越界处理
            position--;
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
            if (!isCompletion) {
                position--;
            }

        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
            //合法的位置
            position--;
            if (position < 0) {
                position = mediaBeens.size() - 1;
            }
        }
    }


    /**
     * 实现播放下一首
     */
    public void nextMusic() {
        //1.根据不同的播放模式设置不同的下标位置
        setNextPosition();

        //2.根据不同的下标位置打开对应的音频并且播放，边界处理
        openNextPosition();

    }


    //根据不同的下标位置打开对应的音频并且播放，边界处理
    private void openNextPosition() {
        int playmode = getPlaymode();

        if (playmode == MusicPlayerService.REPEAT_NORMAL) {
            if (position < mediaBeens.size()) {
                //合法范围
                openAudio(position);

            } else {
                //变为合法
                position = mediaBeens.size() - 1;
            }
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
            if (position < mediaBeens.size()) {
                //合法范围
                openAudio(position);
            } else {
                //变为合法
                position = mediaBeens.size() - 1;
            }

        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
            openAudio(position);
        }
    }

    /**
     * 根据不同的播放模式设置不同的下标位置
     */
    private void setNextPosition() {
        int playmode = getPlaymode();

        if (playmode == MusicPlayerService.REPEAT_NORMAL) {
            //还没有越界处理
            position++;
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
            if (!isCompletion) {
                position++;
            }

        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
            //合法的位置
            position++;
            if (position > mediaBeens.size() - 1) {
                position = 0;
            }
        }
    }


    /**
     * 实现循环播放
     */
    public void forMusic() {

    }

    /**
     * 得到时长
     */
    public int getDuration() {
        return 0;
    }

    /**
     * 得到大小
     */
    public int getSize() {

        return 0;
    }

    /**
     * 得到音乐名
     */
    public String getMusicName() {
        return null;
    }

    /**
     * 得到艺术家
     */
    public String getArtistName() {
        return null;
    }

    /**
     * 得到播放音乐的当前进度
     */
    public int getCurrentPosition() {
        return 0;
    }

    public void seekTo(int position) {
        mediaPlayer.seekTo(position);

    }

    /**
     * 得到播放模式
     *
     * @return
     */
    public int getPlaymode() {
        return playmode;
    }

    /**
     * 设置播放模式
     *
     * @param playmode
     */
    public void setPlaymode(int playmode) {
        this.playmode = playmode;
        sp.edit().putInt("playmode", playmode).commit();
    }


    @Override
    public void onCreate() {
        Log.e("TAG", "onCreate" + "服务创建了");
        super.onCreate();

        sp = getSharedPreferences("atguigu", MODE_PRIVATE);
        playmode = sp.getInt("playmode", getPlaymode());

        getAudioData();


    }


    public void getAudioData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mediaBeens = new ArrayList<LocalMediaBean>();
                ContentResolver resolver = getContentResolver();

                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] obj = {
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ARTIST

                };
                Cursor cursor = resolver.query(uri, obj, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                        String address = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

                        Log.e("TAG", "name" + name + "duration" + duration + "size" + size + "addraess" + address + "artis" + artist);

                        //过滤小文件
                        if (duration > 10 * 1000) {
                            mediaBeens.add(new LocalMediaBean(name, duration, size, address, artist));
                        }


                    }
                    cursor.close();
                }

            }
        }.start();


    }

}
