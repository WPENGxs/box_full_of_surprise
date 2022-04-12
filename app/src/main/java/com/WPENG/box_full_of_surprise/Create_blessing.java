package com.WPENG.box_full_of_surprise;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Create_blessing extends AppCompatActivity {

    public static List<VideoInfo> VideoList = null;// 视频信息集合
    MediaRecorder mediaRecorder;//录音实例
    String audioFilePath;//录音文件总路径
    String audioSaveDir;//录音保存路径
    String audioFileName;//录音文件名称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_blessing);


        Button choose_video_btu=findViewById(R.id.button_choose_video);
        Button record_audio_btu=findViewById(R.id.button_record_audio);
        choose_video_btu.setOnClickListener(new View.OnClickListener() {//选择视频按钮的点击事件
            @Override
            public void onClick(View v) {
                VideoList = new ArrayList</*VideoInfo*/>();//视频列表
                getVideoFile(VideoList, Environment.getExternalStorageDirectory());// 获得视频文件
                String[] Video_name=new String[VideoList.size()];
                for(int i=0;i<VideoList.size();i++)//将List中的name赋值到新的数组中，便于弹窗的调用
                    Video_name[i]=VideoList.get(i).DisplayName;
                AlertDialog.Builder Dialog = new AlertDialog.Builder(Create_blessing.this);
                Dialog.setTitle("选择视频文件：");
                Dialog.setItems(Video_name, (dialog, which) -> {
                    choose_video_btu.setText(VideoList.get(which).DisplayName);
                    //在此将视频路径和名字存到SQLite数据库中
                    SQLite.SQLite_data.video_name=VideoList.get(which).DisplayName;
                    SQLite.SQLite_data.video_path=VideoList.get(which).Path;
                });
                Dialog.show();
            }
        });

        record_audio_btu.setOnClickListener(new View.OnClickListener() {//点击录音按钮的事件
            @Override
            public void onClick(View v) {
                if(ActivityCompat.shouldShowRequestPermissionRationale(Create_blessing.this,Manifest.permission.RECORD_AUDIO)){
                    AlertDialog.Builder Dialog_permission = new AlertDialog.Builder(Create_blessing.this);
                    Dialog_permission.setTitle("权限申请");
                    Dialog_permission.setMessage("录音权限申请");
                    Dialog_permission.setPositiveButton("好的", (dialog,which)->{
                        ActivityCompat.requestPermissions(Create_blessing.this,
                                new String[]{Manifest.permission.RECORD_AUDIO}, 1);//申请权限
                    });
                    Dialog_permission.show();
                }//检查授权

                AlertDialog.Builder recAudio=new AlertDialog.Builder(Create_blessing.this);
                recAudio.setTitle("录音界面");
                recAudio.setPositiveButton("开始/重新录音", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Create_blessing.this,"开始录音",Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder recordingAudio=new AlertDialog.Builder(Create_blessing.this);
                        recordingAudio.setTitle("正在录音");
                        recordingAudio.setPositiveButton("结束录音", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                stopRecord();
                                Toast.makeText(Create_blessing.this,"录音已保存",Toast.LENGTH_SHORT).show();
                            }
                        });
                        startRecord();
                        SQLite.SQLite_data.audio_name=audioFileName;
                        SQLite.SQLite_data.audio_path=audioFilePath;
                        recordingAudio.show();
                    }
                });
                recAudio.setNeutralButton("播放录音", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Create_blessing.this,"正在播放",Toast.LENGTH_SHORT).show();
                        MediaPlayer player=new MediaPlayer();
                        try {
                            player.setDataSource(SQLite.SQLite_data.audio_path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            player.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        player.start();
                        Toast.makeText(Create_blessing.this,"播放结束",Toast.LENGTH_SHORT).show();
                    }
                });
                recAudio.show();
            }
        });
    }
    @Override
    public void onBackPressed(){//重写返回事件防止跳到后一步
        Intent intent = new Intent(this, MainActivity.class);//显示intent
        startActivity(intent);
    }

    /**
     * 开始录音
     * 录音文件
     */
    public void startRecord() {
        // 开始录音
        File appDir = new File(Create_blessing.this.getExternalFilesDir(null).getPath()+ "Audio/");//创建录音文件夹
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        audioSaveDir=getExternalFilesDir(null).getPath()+ "Audio/";
        /* ①Initial：实例化MediaRecorder对象 */
        if (mediaRecorder == null)
            mediaRecorder = new MediaRecorder();
        try {
            /* ②setAudioSource/setVideoSource */
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
            /*
             * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
             * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             */
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            audioFileName = DateFormat.format("yyyyMMdd_HHmmss", Calendar.getInstance(Locale.CHINA)) + ".mp4";
            audioFilePath = audioSaveDir + audioFileName;
            /* ③准备 */
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.prepare();
            /* ④开始 */
            mediaRecorder.start();
        } catch (IllegalStateException | IOException e) {
            Log.i("record audio error","call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        }
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            audioFilePath = "";

        } catch (RuntimeException e) {
            Log.e("record audio error",e.toString());
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;

            File file = new File(audioFilePath);
            if (file.exists())
                file.delete();

            audioFilePath = "";
        }
    }

    public static class VideoInfo{//视频文件的类
        String DisplayName;
        String Path;
        public void setDisplayName(String displayName) {
            DisplayName = displayName;
        }
        public void setPath(String path) {
            Path = path;
        }
    }

    public static void getVideoFile(final List<VideoInfo> list, File file) {// 获得视频文件
        file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
// sdCard找到视频名称
                String name = file.getName();

                int i = name.indexOf('.');
                if (i != -1) {
                    name = name.substring(i);
                    if (name.equalsIgnoreCase(".mp4")
                            || name.equalsIgnoreCase(".3gp")
                            || name.equalsIgnoreCase(".wmv")
                            || name.equalsIgnoreCase(".ts")
                            || name.equalsIgnoreCase(".rmvb")
                            || name.equalsIgnoreCase(".mov")
                            || name.equalsIgnoreCase(".m4v")
                            || name.equalsIgnoreCase(".avi")
                            || name.equalsIgnoreCase(".m3u8")
                            || name.equalsIgnoreCase(".3gpp")
                            || name.equalsIgnoreCase(".3gpp2")
                            || name.equalsIgnoreCase(".mkv")
                            || name.equalsIgnoreCase(".flv")
                            || name.equalsIgnoreCase(".divx")
                            || name.equalsIgnoreCase(".f4v")
                            || name.equalsIgnoreCase(".rm")
                            || name.equalsIgnoreCase(".asf")
                            || name.equalsIgnoreCase(".ram")
                            || name.equalsIgnoreCase(".mpg")
                            || name.equalsIgnoreCase(".v8")
                            || name.equalsIgnoreCase(".swf")
                            || name.equalsIgnoreCase(".m2v")
                            || name.equalsIgnoreCase(".asx")
                            || name.equalsIgnoreCase(".ra")
                            || name.equalsIgnoreCase(".ndivx")
                            || name.equalsIgnoreCase(".xvid")) {
                        VideoInfo vi = new VideoInfo();
                        vi.setDisplayName(file.getName());
                        vi.setPath(file.getAbsolutePath());
                        list.add(vi);
                        return true;
                    }
                } else if (file.isDirectory()) {
                    getVideoFile(list, file);
                }
                return false;
            }
        });
    }
    public void To_set_password(View view){
        EditText editText_title=findViewById(R.id.editText_title);
        EditText editText_contents=findViewById(R.id.editText_contents);
        SQLite.SQLite_data.title=editText_title.getText().toString();
        SQLite.SQLite_data.contents=editText_contents.getText().toString();
        if(SQLite.SQLite_data.title.equals("")||SQLite.SQLite_data.contents.equals("")){
            AlertDialog.Builder DIalog=new AlertDialog.Builder(Create_blessing.this);
            DIalog.setTitle("警告");
            DIalog.setMessage("没有标题或文案怎么祝福别人呢？");
            DIalog.show();
        }
        else{
            Intent intent = new Intent(this, Set_password.class);//显示intent
            startActivity(intent);
        }
    }

    public void Back_to_view(View view){
        Intent intent = new Intent(this, MainActivity.class);//显示intent
        startActivity(intent);
    }

}