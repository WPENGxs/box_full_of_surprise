package com.WPENG.box_full_of_surprise;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Set_tips extends AppCompatActivity {

    public String select_tip=null;

    MediaRecorder mediaRecorder;//录音实例
    String audioFilePath;//录音文件总路径
    String audioSaveDir;//录音保存路径
    String audioFileName;//录音文件名称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_tips);

        Spinner spinner_tips=findViewById(R.id.spinner_tips);//选择框

        String[] tips={"不设置提示","问题提示","回忆音频提示","回忆视频提示","密室小游戏"};
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,tips);
        spinner_tips.setAdapter(adapter);//为spinner设置adapter
        spinner_tips.setOnItemSelectedListener(new spinnerListener());
    }
     class spinnerListener implements android.widget.AdapterView.OnItemSelectedListener{
        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {//将选择的元素显示出来
            select_tip = parent.getItemAtPosition(position).toString();//获得选择的字符串
            if(select_tip.equals("不设置提示")){
                LinearLayout tips_out=findViewById(R.id.tips_out);//布局文件
                TextView textView=new TextView(Set_tips.this);
                tips_out.removeAllViews();//清除所有布局
                tips_out.addView(textView);
                textView.setText("不设置密码提示");
                textView.setTextSize(20);

                SQLite.SQLite_data.tips="不设置提示";
                SQLite.SQLite_data.tips_item="";
            }else if(select_tip.equals("问题提示")){
                LinearLayout tips_out=findViewById(R.id.tips_out);//布局文件
                TextView textView=new TextView(Set_tips.this);
                EditText editText=new EditText(Set_tips.this);
                tips_out.removeAllViews();//清除所有布局
                tips_out.addView(textView);
                tips_out.addView(editText);
                textView.setText("请设置要提示的问题:");
                textView.setTextSize(20);
                editText.setBackground(getDrawable(R.drawable.round_edit_style));
                editText.setHint("请输入");
                editText.setMaxLines(2);

                editText.addTextChangedListener(new TextWatcher() {//文本内容变化监听
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        SQLite.SQLite_data.tips="问题提示";
                        SQLite.SQLite_data.tips_item=editText.getText().toString();
                    }
                });
            }else if(select_tip.equals("回忆音频提示")){
                LinearLayout tips_out=findViewById(R.id.tips_out);//布局文件
                tips_out.removeAllViews();//清除所有布局
                Button button=new Button(Set_tips.this);
                tips_out.addView(button);
                button.setBackground(getDrawable(R.drawable.general_button));
                button.setText("录音界面");
                button.setTextSize(20);

                /*
                以下是败笔，当时没有给这个方法封装好
                 */
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder recAudio=new AlertDialog.Builder(Set_tips.this);
                        recAudio.setTitle("录音界面");
                        recAudio.setPositiveButton("开始/重新录音", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(Set_tips.this,"开始录音",Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder recordingAudio=new AlertDialog.Builder(Set_tips.this);
                                recordingAudio.setTitle("正在录音");
                                recordingAudio.setPositiveButton("结束录音", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        stopRecord();
                                        Toast.makeText(Set_tips.this,"录音已保存",Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(Set_tips.this,"正在播放",Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(Set_tips.this,"播放结束",Toast.LENGTH_SHORT).show();
                            }
                        });
                        recAudio.show();
                    }
                });

                SQLite.SQLite_data.tips="回忆音频提示";
                SQLite.SQLite_data.tips_item=audioFilePath;
            }else if(select_tip.equals("回忆视频提示")){
                LinearLayout tips_out=findViewById(R.id.tips_out);//布局文件
                tips_out.removeAllViews();//清除所有布局
                Button button=new Button(Set_tips.this);
                tips_out.addView(button);
                button.setBackground(getDrawable(R.drawable.general_button));
                button.setText("请选择视频");
                button.setTextSize(20);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Create_blessing.VideoList = new ArrayList</*VideoInfo*/>();//视频列表
                        Create_blessing.getVideoFile(Create_blessing.VideoList, Environment.getExternalStorageDirectory());// 获得视频文件
                        String[] Video_name=new String[Create_blessing.VideoList.size()];
                        for(int i=0;i<Create_blessing.VideoList.size();i++)//将List中的name赋值到新的数组中，便于弹窗的调用
                            Video_name[i]=Create_blessing.VideoList.get(i).DisplayName;
                        AlertDialog.Builder Dialog = new AlertDialog.Builder(Set_tips.this);
                        Dialog.setTitle("选择视频文件：");
                        Dialog.setItems(Video_name, (dialog, which) -> {
                            button.setText(Create_blessing.VideoList.get(which).DisplayName);

                            SQLite.SQLite_data.tips="问题提示";
                            SQLite.SQLite_data.tips_item=Create_blessing.VideoList.get(which).Path;
                        });
                        Dialog.show();
                    }
                });
            }else if(select_tip.equals("密室小游戏")){
                LinearLayout tips_out=findViewById(R.id.tips_out);//布局文件
                tips_out.removeAllViews();//清除所有布局

                SQLite.SQLite_data.tips="密室小游戏";
                SQLite.SQLite_data.tips_item="";
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {//未选择
            select_tip="不设置提示";
        }
    }
    @Override
    public void onBackPressed(){//重写返回事件防止跳到后一步
        Intent intent = new Intent(this, Set_password.class);//显示intent
        startActivity(intent);
    }

    public void To_set_pack(View view){
        Intent intent = new Intent(this,Set_pack.class);//显示intent
        startActivity(intent);
    }

    public void To_set_password(View view){
        Intent intent = new Intent(this, Set_password.class);//显示intent
        startActivity(intent);
    }

    /**
     * 开始录音
     * 录音文件
     */
    public void startRecord() {
        // 开始录音
        File appDir = new File(Set_tips.this.getExternalFilesDir(null).getPath()+ "Audio/");//创建录音文件夹
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
}