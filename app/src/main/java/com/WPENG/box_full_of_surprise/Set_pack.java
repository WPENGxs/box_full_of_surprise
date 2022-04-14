package com.WPENG.box_full_of_surprise;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.WPENG.box_full_of_surprise.zxing.encode.QRcode;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Set_pack extends AppCompatActivity {

    String HtmlName;
    List<FTPFile> background_list=null;
    String[] list=null;
    static String Background="经典.png";
    int Background_which;

    boolean IsHtmlUpload=false;
    Handler handler=null;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pack);

        Linear_confirm_start();//Linearlayout布局

        GetBackground();//在服务器端获得背景列表

        handler=new android.os.Handler(){
            public void handleMessage(Message message){
                switch (message.what){
                    case 0x01:
                        IsHtmlUpload=true;
                        break;
                    case 0x02:
                        Toast.makeText(Set_pack.this,"上传完毕",Toast.LENGTH_SHORT).show();
                        break;
                    case 0x03:
                        Toast.makeText(Set_pack.this,"error!!!",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };


        Button background_choose_button=findViewById(R.id.background_choose_button);
        background_choose_button.setOnClickListener(new View.OnClickListener() {//设置选择背景按钮的监听
            @Override
            public void onClick(View v) {

                AlertDialog.Builder background_choose=new AlertDialog.Builder(Set_pack.this);
                background_choose.setTitle("选择贺卡背景");
                background_choose.setItems(list,(dialog, which) ->{
                    String url="https://www.wpengxs.cn/shu_meng_ge/shu_meng_ge_html/shu_meng_ge_background/"+list[which];//设定图片url
                    Background_which=which;

                    WebView img=new WebView(Set_pack.this);
                    img.loadUrl(url);

                    WebSettings imgSetting=img.getSettings();//获得WebView的设置
                    imgSetting.setUseWideViewPort(true);//页面大小自适应
                    imgSetting.setLoadWithOverviewMode(true);//加载完全缩小的WebView

                    AlertDialog.Builder show_background=new AlertDialog.Builder(Set_pack.this);
                    show_background.setView(img);
                    show_background.setPositiveButton("选择", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Background=list[Background_which];
                            Log.d("Background",Background);
                        }
                    });
                    show_background.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    show_background.show();
                });
                background_choose.show();
            }
        });

        Button review_button=findViewById(R.id.review_button);
        review_button.setOnClickListener(new View.OnClickListener() {//设置预览按钮的监听
            @Override
            public void onClick(View v) {
                /*
                预览效果放置于此
                 */
            }
        });

        Button finish_button=findViewById(R.id.finish_button);
        finish_button.setOnClickListener(new View.OnClickListener() {
            // 设置完成按钮的监听
            @Override
            public void onClick(View v) {
                HtmlOutput();//新建Html文件
                /*
                 * 在此把数据上传服务器并生成url,将url写入SQLite
                 * */
                SQLite.SQLite_data.QRCode_url="https://www.wpengxs.cn/shu_meng_ge/shu_meng_ge_html/"+HtmlName;

                MainActivity.insert_sql="insert into user(title,contents,audio_name,picture_name1,picture_name2,picture_name3,video_name," +
                        "audio_path,picture_path1,picture_path2,picture_path3,video_path," +
                        "password,tips,tips_item,QRCode_url,Html_path) " +
                        "values('"+ SQLite.SQLite_data.title +
                        "','"+SQLite.SQLite_data.contents+
                        "','"+SQLite.SQLite_data.audio_name+
                        "','"+SQLite.SQLite_data.picture_name1+
                        "','"+SQLite.SQLite_data.picture_name2+
                        "','"+SQLite.SQLite_data.picture_name3+
                        "','"+SQLite.SQLite_data.video_name+
                        "','"+SQLite.SQLite_data.audio_path+
                        "','"+SQLite.SQLite_data.picture_path1+
                        "','"+SQLite.SQLite_data.picture_path2+
                        "','"+SQLite.SQLite_data.picture_path3+
                        "','"+SQLite.SQLite_data.video_path+
                        "','"+SQLite.SQLite_data.password+
                        "','"+SQLite.SQLite_data.tips+
                        "','"+SQLite.SQLite_data.tips_item+
                        "','"+SQLite.SQLite_data.QRCode_url+
                        "','"+SQLite.SQLite_data.Html_path+
                        "')";
                MainActivity.Database.execSQL(MainActivity.insert_sql);

                new Thread(){//上传Html
                    @Override
                    public void run(){
                        /*Message html_message=new Message();*/
                        FTP_server FTP_uploadHtml=new FTP_server("1.15.28.84","shu_meng_ge_html","SMG_0628");
                        try {
                            FTP_uploadHtml.openConnect();
                            File html_file=new File(SQLite.SQLite_data.Html_path);//Html文件路径
                            FTP_uploadHtml.uploadingSingle(html_file);//上传html文件
                            FTP_uploadHtml.closeConnect();
                        } catch (IOException e) {
                            /*html_message.what=0x03;
                            handler.sendMessage(html_message);*/
                            e.printStackTrace();
                        }
                        /*html_message.what=0x01;
                        handler.sendMessage(html_message);*/

                        /*try {//在此向云端数据库发送标题、密码和提示方式
                            Data_socket socket=new Data_socket("1.15.28.84",39002);
                            socket.SentData(SQLite.SQLite_data.title+"/"+SQLite.SQLite_data.password+"/"+SQLite.SQLite_data.tips);
                            socket.Close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/
                    }
                }.start();

                if(!SQLite.SQLite_data.video_path.equals("")||!SQLite.SQLite_data.audio_path.equals("")){
                    ProgressDialog upload_dialog = new ProgressDialog(Set_pack.this);//上传弹窗
                    upload_dialog.setTitle("FTP上传");
                    upload_dialog.setMessage("正在上传中...");
                    upload_dialog.setButton(DialogInterface.BUTTON_POSITIVE, "查看二维码", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showQRCode();
                        }
                    });
                    upload_dialog.setCancelable(false);    //设置不可以被取消
                    upload_dialog.show();

                    new Thread(){
                        //开启一个子线程防止主页面卡死
                        // Android 4.0 之后不能在主线程中请求HTTP请求
                        @Override
                        public void run(){
                            Message file_message=new Message();
                            FTP_server FTP_upload=new FTP_server("1.15.28.84","shu_meng_ge_files","SMG0628");
                            try {
                                FTP_upload.openConnect();//打开FTP服务
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            File video_file=new File(SQLite.SQLite_data.video_path);//视频路径
                            File audio_file=new File(SQLite.SQLite_data.audio_path);//音频路径

                            FTP_upload.makeDir(SQLite.SQLite_data.title);//创建一个文件夹

                            FTP_upload.uploading_yourDir(video_file,SQLite.SQLite_data.title);//将文件上传到指定文件夹中
                            FTP_upload.uploading_yourDir(audio_file,SQLite.SQLite_data.title);

                            try {
                                FTP_upload.closeConnect();//关闭FTP服务
                                file_message.what=0x02;
                                handler.sendMessage(file_message);
                                upload_dialog.setMessage("上传完毕");
                                //upload_dialog.dismiss();//上传完毕结束弹窗
                            } catch (IOException e) {
                                file_message.what=0x03;
                                handler.sendMessage(file_message);
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
                else{
                    showQRCode();
                }
            }
        });
    }

    public void GetBackground(){
        new Thread(){
            public void run(){
                FTP_server FTP_background=new FTP_server("1.15.28.84","shu_meng_ge_html","SMG_0628");
                try {
                    FTP_background.openConnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    background_list=FTP_background.listFiles("shu_meng_ge_background");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(background_list!=null){
                    list=new String[background_list.size()-2];
                    for(int i=2;i<background_list.size();i++){//去掉i=0和i=1，这两个是.和..
                        list[i-2] = background_list.get(i).getName();

                    }
                }
                try {
                    FTP_background.closeConnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
    public void HtmlOutput(){
        //建立Html文件夹
        File appDir = new File(Set_pack.this.getExternalFilesDir(null).getPath()+ "Html");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        HtmlName=SQLite.SQLite_data.title+
                android.text.format.DateFormat.format("yyyyMMdd_HHmmss", Calendar.getInstance(Locale.CHINA))+
                ".html";
        File HtmlFile=new File(Set_pack.this.getExternalFilesDir(null).getPath()+ "Html"+
                "/"+HtmlName);//新建Html文件
        SQLite.SQLite_data.Html_path=HtmlFile.getPath();
        HtmlOutput HtmlOuter=new HtmlOutput();
        HtmlOuter.CreateHtml(SQLite.SQLite_data.title,SQLite.SQLite_data.contents,
                "https://www.wpengxs.cn/shu_meng_ge/shu_meng_ge_files/"+SQLite.SQLite_data.title+"/"+SQLite.SQLite_data.audio_name,
                "https://www.wpengxs.cn/shu_meng_ge/shu_meng_ge_files/"+SQLite.SQLite_data.title+"/"+SQLite.SQLite_data.video_name,HtmlFile);
    }

    public void showQRCode(){
        ImageView QRCode_img = new ImageView(Set_pack.this);
        Bitmap QRCode_bitmap= QRcode.createQRCode(SQLite.SQLite_data.QRCode_url,1000);
        //设置size为1000是为了增大图片，避免图片过小被系统忽略
        if(QRCode_bitmap!=null){
            QRCode_img.setImageBitmap(QRCode_bitmap);
            AlertDialog.Builder Dialog=new AlertDialog.Builder(Set_pack.this);
            Dialog.setTitle("二维码为");
            Dialog.setView(QRCode_img);
            Dialog.setPositiveButton("返回主界面",(dialog, which) -> {
                Intent intent = new Intent(Set_pack.this, MainActivity.class);//显示intent
                startActivity(intent);
            });
            Dialog.setNegativeButton("保存二维码并返回主界面",(dialog, which) -> {
                saveImageToGallery(Set_pack.this,QRCode_bitmap,SQLite.SQLite_data.title);
                Intent intent = new Intent(Set_pack.this, MainActivity.class);//显示intent
                startActivity(intent);
            });
            Dialog.show();
        }
        else{
            Toast.makeText(Set_pack.this,"不知道为什么，生成二维码失败",Toast.LENGTH_SHORT).show();
        }
    }
    public static void saveImageToGallery(Context context, Bitmap bmp, String fileName) {//保存图片到本地
        //检查有没有存储权限
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, "请打开应用权限", Toast.LENGTH_SHORT).show();
        } else {
            // 新建目录appDir，并把图片存到其下
            File appDir = new File(context.getExternalFilesDir(null).getPath()+ "QRCode");
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            File file = new File(appDir, fileName + getIOS8601Timestamp() + ".jpg");
            try {
                FileOutputStream fos = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("FileNotFoundException","FileNotFoundException");
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 把file里面的图片插入到系统相册中
            try {
                MediaStore.Images.Media.insertImage(context.getContentResolver(),
                        file.getAbsolutePath(), fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Toast.makeText(context, "二维码已保存", Toast.LENGTH_LONG).show();
            //Toast.makeText(this, file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            // 通知相册更新
            MediaScannerConnection.scanFile(context,new String[]{file.toString()},
                    new String[]{file.getName()},null);
            //context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            // 因为ACTION_MEDIA_SCANNER_SCAN_FILE弃用
        }
    }


    @SuppressLint("SetTextI18n")
    public void Linear_confirm_start(){//Linearlayout布局
        LinearLayout Linear_confirm=findViewById(R.id.Linear_confirm);
        TextView text=new TextView(Set_pack.this);
        Linear_confirm.removeAllViews();//清除所有布局
        Linear_confirm.addView(text);
        text.setText("标题:"+SQLite.SQLite_data.title+"\n"+
                    "文案:"+SQLite.SQLite_data.contents+"\n"+
                    "音频:"+SQLite.SQLite_data.audio_name+"\n"+
                    "视频:"+SQLite.SQLite_data.video_name+"\n"+
                    "密码:"+SQLite.SQLite_data.password+"\n"+
                    "提示方式:"+SQLite.SQLite_data.tips+"\n"+
                    "提示值:"+SQLite.SQLite_data.tips_item+"\n"
        );
        text.setTextSize(20);
    }

    /**
     * 传入Data类型日期，返回字符串类型时间（ISO8601标准时间）
     *
     * @return df
     *              返回字符串类型时间
     */
    public static String getIOS8601Timestamp(){

        /*获取当前系统时间*/
        long time = System.currentTimeMillis();
        /*时间戳转换成IOS8601字符串*/
        Date date = new Date(time);
        TimeZone tz = TimeZone.getTimeZone("Asia/Beijing");
        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        return df.format(date);
    }

    @Override
    public void onBackPressed(){//重写返回事件防止跳到后一步
        Intent intent = new Intent(this, Set_password.class);//显示intent
        startActivity(intent);
    }

    public void To_set_password(View view){
        Intent intent = new Intent(this, Set_password.class);//显示intent
        startActivity(intent);
    }
}